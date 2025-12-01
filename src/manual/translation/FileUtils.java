package manual.translation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class FileUtils {
	
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
	
	public static List<File> filterNotCreatedFile(List<File> files, String root) {
		List<File> list = new ArrayList<>();
		
		for(File f : files) {
			File newFile = new File(getNewPath(f, root));
			
			if(!newFile.exists()) {
				list.add(f);
			}
		}
		
		return list;
	}
	
	public static String getNewPath(File file, String root) {
		String original = file.getAbsolutePath();
		
		Path rootPath = Paths.get(root);
		Path originalPath = Paths.get(original);
		
		Path relative = rootPath.relativize(originalPath);
		Path newPath = rootPath.resolve("en").resolve(relative);
		
		return newPath.toString();
	}
	
	public static Properties loadProperties(String fileName) {
	    Properties props = new Properties();

	    try (InputStream is =
	            Thread.currentThread()
	                  .getContextClassLoader()
	                  .getResourceAsStream(fileName)) {

	        if (is == null) {
	            throw new RuntimeException("properties 파일을 찾을 수 없습니다: " + fileName);
	        }

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
		try(OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
			osw.write(content);
		}catch(Exception e) {
			throw new Exception(file.getName() + "파일 쓰기 중에 에러가 발생했습니다.", e);
		}
	}
	
	public static void main(String[] args) {
		System.out.println(File.pathSeparator);
		System.out.println(File.separator);
	}
}
