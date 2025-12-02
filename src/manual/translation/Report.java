package manual.translation;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Report {
	
	private List<String> messages = new ArrayList<>();
	private List<File> koreanContainFiles = new ArrayList<>();
	private int successFileCount = 0;
	private int skipFileCount = 0;
	private int failedFileCount = 0;
	private long start;
	
	public Report() {
		start = System.currentTimeMillis();
	}
	
	public void increaseSuccess() {
		successFileCount++;
	}
	
	public void increaseSkip() {
		skipFileCount++;
	}
	
	public void increaseFailed() {
		failedFileCount++;
	}
	
	public void addKoreanContainFile(File file) {
		koreanContainFiles.add(file);
	}
	
	public void addMessage(String message) {
		messages.add(message);
	}
	
	public String generate() {
		long end = System.currentTimeMillis();
		StringBuilder sb = new StringBuilder();

		String joinMessage = String.join(System.lineSeparator(), messages);
		if(joinMessage != null && joinMessage.length() > 0) {
			sb.append(joinMessage);
			sb.append(System.lineSeparator());
		}
		if(koreanContainFiles.size() > 0) {
			sb.append("---------- 한국어가 포함된 파일 ----------" + System.lineSeparator());
		}
    	for(File f : koreanContainFiles) {
    		sb.append(f.getAbsolutePath() + System.lineSeparator());
    	}
    	
    	sb.append("---------- 요약 ----------" + System.lineSeparator());
    	sb.append("Adoc 파일 수 : " + (successFileCount + skipFileCount + failedFileCount) + System.lineSeparator());
    	sb.append("성공 파일 수 : " + successFileCount + System.lineSeparator());
    	sb.append("skip 파일 수 : " + skipFileCount + System.lineSeparator());
    	sb.append("실패 파일 수 : " + failedFileCount + System.lineSeparator());
    	sb.append("소요 시간 : " + (end - start) + " ms");

		return sb.toString();
	}
}