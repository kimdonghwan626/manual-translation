package manual.translation;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GptTranslator {
	
	public static final String OPENAI_API_URL = "https://api.openai.com/v1/responses";
	
	private final String model;
	private final String apiKey;
	
	public GptTranslator(String model, String apiKey) {
		this.model = model;
		this.apiKey = apiKey;
	}
	
	public String translate(String prompt) throws Exception {
		
		String englishAdoc = null;
		HttpURLConnection conn = null;
		try {
			conn = createConnection(prompt);
			
			int statusCode = conn.getResponseCode();
			if(statusCode == 200) {
				String json = read(conn.getInputStream());
				englishAdoc = extractContent(json);
			}else {
				throw new Exception(read(conn.getErrorStream()));
			}
		}catch(Exception e) {
			throw e;
		}finally {
			if(conn != null) {
				conn.disconnect();
			}
		}
		
		return englishAdoc;
	}
	
	private String read(InputStream is) throws Exception {
		StringBuilder sb = new StringBuilder();

		try(BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
			String line = null;
			while((line = br.readLine()) != null) {
				sb.append(line);
				sb.append(System.lineSeparator());
			}
		}

		return sb.toString();
	}
	
	private String extractContent(String json) throws Exception {
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode rootNode = objectMapper.readTree(json);
		JsonNode outputsNode = rootNode.get("output");
		JsonNode outputNode = outputsNode.get(0);
		JsonNode contentsNode = outputNode.get("content");
		JsonNode contentNode = contentsNode.get(0);
		JsonNode textNode = contentNode.get("text");
		return textNode.asText("");
	}
	
	private HttpURLConnection createConnection(String prompt) throws Exception {
		URL url = new URL(OPENAI_API_URL);
		
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
		conn.setRequestProperty("Authorization", "Bearer " + apiKey);
		conn.setRequestProperty("OpenAI-Beta", "response=v1");
		conn.setReadTimeout(90 * 1000);
		conn.setConnectTimeout(10 * 1000);
		conn.setDoOutput(true);
		
		if(conn instanceof HttpsURLConnection) {
			HttpsURLConnection _conn = (HttpsURLConnection) conn;
			
			SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
			sslContext.init(null, new TrustManager[] { new X509TrustManager() {

				@Override
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				@Override
				public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				}

				@Override
				public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
					
				}
			} }, null);
			_conn.setSSLSocketFactory(sslContext.getSocketFactory());
		}
		
		String body = createBody(prompt);
		
		try(BufferedOutputStream bos = new BufferedOutputStream(conn.getOutputStream())) {
			byte[] input = body.getBytes(StandardCharsets.UTF_8);
			bos.write(input);
		}
		
		return conn;
	}
	
	private String createBody(String prompt) throws Exception {
		Map<String, Object> map = new HashMap<>();
		map.put("model", model);
		map.put("input", prompt);
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(map);
	}
}