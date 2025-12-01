package manual.translation;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
	
	public List<File> filterNotCreatedFile(List<File> files) {
		List<File> list = new ArrayList<>();
		
		for(File f : files) {
			//TODO
		}
		
		return list;
	}
}
