package manual.translation.translator;

public class GptTranslator {
	
	public static final String URL = "https://api.openai.com/v1/conversations";
	
	private final String apiKey;
	private final String prompt;
	
	public GptTranslator(String apiKey, String prompt) {
		this.apiKey = apiKey;
		this.prompt = prompt;
	}
	
	public String translate() throws Exception {
		return null;
	}
}
