package manual.translation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.List;

import manual.translation.template.PromptTemplate;

public class Main {

    private static final String OPENAI_API_KEY = 
    		"";

    private static final String OPENAI_API_URL =
            "https://api.openai.com/v1/conversations";

    public static void main(String[] args) {
    	
    	String root = System.getProperty("user.dir");
    	
    	System.out.println("ROOT : " + root);
    	
    	List<File> files = FileUtils.collectAdocFiles(root);
    	List<File> filteredFiles = FileUtils.filterNotCreatedFile(files);
    	
    	
        try {
            // 예: glossary.txt / manual.adoc 로컬 파일
            String glossary = readFile("glossary.txt");
            String koreanAdoc = readFile("manual.adoc");

            // Prompt 생성
            String prompt = PromptTemplate.buildPrompt(
                    glossary,
                    koreanAdoc
            );

            String result = requestGPT(prompt);

            System.out.println("========== TRANSLATION RESULT ==========");
            System.out.println(result);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String requestGPT(String prompt) throws Exception {

        URL url = new URL(OPENAI_API_URL);
        HttpURLConnection conn =
                (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type",
                "application/json; charset=UTF-8");
        conn.setRequestProperty("Authorization",
                "Bearer " + OPENAI_API_KEY);
        conn.setDoOutput(true);

        String requestJson = buildRequestBody(prompt);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(
                requestJson.getBytes(StandardCharsets.UTF_8)
            );
        }

        int status = conn.getResponseCode();

        InputStream responseStream =
                (status >= 200 && status < 300)
                        ? conn.getInputStream()
                        : conn.getErrorStream();

        String response =
                readStream(responseStream);

        // === 실제 번역 텍스트만 추출 ===
        return parseMessageContent(response);
    }

    // ----------------------------
    // 요청 바디 생성
    // ----------------------------
    private static String buildRequestBody(String prompt) {

    	String json = "{\r\n"
    			+ "          \"model\": \"gpt-5.1\",\r\n"
    			+ "          \"temperature\": 0.0,\r\n"
    			+ "          \"messages\": [\r\n"
    			+ "            {\r\n"
    			+ "              \"role\": \"system\",\r\n"
    			+ "              \"content\": \"You are a technical translation engine.\"\r\n"
    			+ "            },\r\n"
    			+ "            {\r\n"
    			+ "              \"role\": \"user\",\r\n"
    			+ "              \"content\": \"{0}\"\r\n"
    			+ "            }\r\n"
    			+ "          ]\r\n"
    			+ "        }";
    	return MessageFormat.format(json, new Object[] {escapeJson(prompt)});
    }

    private static String escapeJson(String text) {
        return text.replace("\\", "\\\\")
                        .replace("\"", "\\\"")
                        .replace("\n", "\\n")
                        .replace("\r", "\\r");
    }

    private static String parseMessageContent(String json)
            throws Exception {

        // 초간단 파싱 (외부 라이브러리 미사용 기준)
        String key = "\"content\":\"";
        int start = json.indexOf(key);

        if (start < 0) {
            throw new RuntimeException("No content found: " + json);
        }

        start += key.length();
        int end = json.indexOf("\"", start);

        String content = json.substring(start, end);

        return content
                .replace("\\n", "\n")
                .replace("\\\"", "\"")
                .replace("\\\\", "\\");
    }

    // ----------------------------
    // File Reader
    // ----------------------------
    private static String readFile(String path)
            throws Exception {

        try (BufferedReader br =
                     new BufferedReader(
                             new InputStreamReader(
                                     new FileInputStream(path),
                                     StandardCharsets.UTF_8
                             )
                     )
        ) {

            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }

            return sb.toString();
        }
    }

    private static String readStream(InputStream is)
            throws Exception {
    	byte[] bytes = is.readAllBytes();
    	return new String(bytes, StandardCharsets.UTF_8);
    }

}
