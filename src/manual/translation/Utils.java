package manual.translation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.regex.Pattern;

public class Utils {
	
	public static List<File> collectAdocFiles(String root) {
		List<File> list = new ArrayList<>();
		collectInternal(new File(root), list);
		return list;
	}
	
	private static void collectInternal(File directory, List<File> list) {
		for(File f : directory.listFiles()) {
			if(f.isDirectory()) {
				collectInternal(f, list);
			}else {
				if(isAdocFile(f)) {
					list.add(f);
				}
			}
		}
	}
	
	public static boolean isAdocFile(File file) {
		return file.getName().endsWith(".adoc");
	}
	
	public static File getNewFile(File file, String sourcePath, String targetPath) {
		String _sourcePath = sourcePath.replace("/", "\\");
		String _targetPath = targetPath.replace("/", "\\");
		
		String newPath = file.getAbsolutePath().replace(_sourcePath, _targetPath);
		return new File(newPath);
	}
	
	public static Properties loadProperties(File file) {
	    Properties props = new Properties();

	    try (InputStream is = new FileInputStream(file)) {
	        props.load(is);
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }

	    return props;
	}
	
	public static String getFileContent(File file) throws Exception {
		try(InputStream is = new FileInputStream(file)) {
			return new String(is.readAllBytes(), StandardCharsets.UTF_8);
		}catch(Exception e) {
			throw new Exception(file.getName() + "파일 읽는 중에 에러가 발생했습니다.", e);
		}
	}
	
	public static void writeFileContent(File file, String content) throws Exception {
		File directory = file.getParentFile();
		if(!directory.exists()) {
			directory.mkdirs();
		}
		
		try(OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
			osw.write(content);
		}catch(Exception e) {
			throw new Exception(file.getName() + "파일 쓰기 중에 에러가 발생했습니다.", e);
		}
	}
	
	public static String variableMapping(String content, Map<String, String> variables) {
		String newContent = content;
		
		for(Entry<String, String> entry : variables.entrySet()) {
			newContent = newContent.replace("{" + entry.getKey() + "}", entry.getValue());
		}
		
		return newContent;
	}
	
	public static boolean containsKorean(String text) {
	    if (text == null) return false;
	    return Pattern.compile("[ㄱ-ㅎㅏ-ㅣ가-힣]").matcher(text).find();
	}
}