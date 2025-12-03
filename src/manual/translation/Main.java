package manual.translation;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class Main {

    public static void main(String[] args) throws Exception {
    	
    	System.setProperty("https.protocols", "TLSv1.2,TLSv1.3");
    	
    	String root = System.getProperty("user.dir");
    	
    	System.out.println("현재 작업 경로 : " + root);

    	File propFile = new File(root + File.separator + "config.properties");
    	if(!propFile.exists()) {
    		throw new Exception("config.properties 파일이 없습니다.");
    	}
    	
    	Properties prop = Utils.loadProperties(propFile);

    	final String OPENAI_API_KEY = prop.getProperty("OPENAI_API_KEY");
    	if(OPENAI_API_KEY == null) {
    		throw new Exception("OPENAI_API_KEY가 없습니다.");
    	}
    	
    	final String TARGET_PATH = prop.getProperty("TARGET_PATH");
    	if(TARGET_PATH == null) {
    		throw new Exception("TARGET_PATH가 없습니다.");
    	}
    	
    	final String SOURCE_PATH = prop.getProperty("SOURCE_PATH");
    	if(SOURCE_PATH == null) {
    		throw new Exception("SOURCE_PATH가 없습니다.");
    	}
    	
    	String MODEL = prop.getProperty("MODEL");
    	if(MODEL == null) {
    		MODEL = "gpt-5.1";
    	}
    	
    	File contextFile = new File(root + File.separator + "context.csv");
    	if(!contextFile.exists()) {
    		throw new Exception("context.csv 파일이 없습니다.");
    	}
    	
    	String contextFileContent = Utils.getFileContent(contextFile);
    	
    	File promptTemplateFile = new File(root + File.separator + "promptTemplate.txt");
    	String promptTemplate = null;
    	if(promptTemplateFile.exists()) {
    		System.out.println("promptTemplate.txt 파일 존재");
    		
    		promptTemplate = Utils.getFileContent(promptTemplateFile);
    	}
    	
    	List<File> files = Utils.collectAdocFiles(SOURCE_PATH);
    	
    	System.out.println("모든 ADOC 파일 개수 : " + files.size());
    	
    	GptTranslator translator = new GptTranslator(MODEL, OPENAI_API_KEY);
    	Report report = new Report();
    	
    	for(File f : files) {
    		File newFile = Utils.getNewFile(f, SOURCE_PATH, TARGET_PATH);
			
    		if(newFile.exists()) {
    			System.out.println(f.getAbsolutePath() + "의 영문 파일은 이미 TARGET_PATH에 존재합니다.");
    			report.increaseSkip();
    			continue;
    		}
    		
    		String koreanAdoc = Utils.getFileContent(f);
    		String prompt = null;
    		
    		if(promptTemplate != null && promptTemplate.length() > 0) {
    			Map<String, String> variables = new HashMap<>();
    			variables.put("context", contextFileContent);
    			variables.put("koreanAdoc", koreanAdoc);
    			prompt = Utils.variableMapping(promptTemplate, variables);
    		}else {
    			prompt = DefaultPromptTemplate.buildPrompt(contextFileContent, koreanAdoc);
    		}

    		try {
    			String englishAdoc = translator.translate(prompt);
    			
    			if(englishAdoc != null) {
    				englishAdoc = englishAdoc.replace("image::ko", "image:en");
    			}
    			if(Utils.containsKorean(englishAdoc)) {
    				 System.out.println(f.getAbsolutePath() + "파일에 한글이 포함되어 있습니다.");
    				report.addKoreanContainFile(f);
    			}
    			
    			Utils.writeFileContent(newFile, englishAdoc);
    			
    			System.out.println(f.getAbsolutePath() + " 변환 성공");
    			report.increaseSuccess();
    		}catch(Exception e) {
    			System.err.println(f.getAbsolutePath() + " 변환 실패");
    			report.addMessage(f.getAbsolutePath() + " 변환 실패 -> " + e.getMessage());
    			report.increaseFailed();
    		}
    	}
    	
    	Utils.writeFileContent(new File(root + File.separator + "report.txt"), report.generate());
    }
}