package com.bjitgroup.vautomation.core.utilities;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.bjitgroup.vautomation.core.models.filesystem.FileInformation;
import com.bjitgroup.vautomation.core.models.filesystem.FileVisitor;

public class FileSystemUtilities {
	
	// private static final String directoryPath = Application.DATA_DIRECTORY_PATH + "file-lists";
	
	/*public static String getCanonicalPath(String filePath) {
		return getCanonicalPath(new File(filePath));
	}*/
	
	/*public static String getCanonicalPath(File file) {
		if (file == null) {
			return null;
		}
		
		String canonicalPath;
		
		try {
			canonicalPath = file.getCanonicalPath();
		} catch (Exception exception) {
			canonicalPath = file.getAbsolutePath();
			
			exception.printStackTrace();
		}
		
		return canonicalPath;
	}*/
	
	public static List<FileInformation> toFileInformationList(String rootDirectory, List<String> filePaths) {
		if (filePaths == null || filePaths.size() == 0) {
			return null;
		}
		
		List<FileInformation> fileInformationList = new ArrayList<FileInformation>(filePaths.size());
		
		for (int i = 0; i < filePaths.size(); i++) {
			FileInformation fileInformation = new FileInformation(rootDirectory, filePaths.get(i));
			
			fileInformationList.add(fileInformation);
		}
		
		return fileInformationList;
	}
	
	/*public static ArrayList<String> getAllFilePaths(String directoryPath) {
		return getAllFilePaths(new File(directoryPath));
	}*/
	
	/*public static ArrayList<String> getAllFilePaths(File directory) {
		if (directory.exists() && directory.isDirectory()) {
			File[] files = directory.listFiles();
			
			ArrayList<String> filePaths = new ArrayList<String>();
			
			for (File file : files) {
				if (!file.exists()) {
					continue;
				}
				
				if (file.isDirectory()) {
					ArrayList<String> temporaryFilePaths = getAllFilePaths(file);
					
					if (temporaryFilePaths == null || temporaryFilePaths.size() == 0) {
						continue;
					}
					
					filePaths.addAll(temporaryFilePaths);
				} else {
					String filePath;
					
					try {
						filePath = file.getCanonicalPath();
					} catch (Exception exception) {
						filePath = file.getAbsolutePath();
						
						exception.printStackTrace();
					}
					
					filePaths.add(filePath);
				}
			}
			
			return filePaths;
		}
		
		return null;
	}*/
	
	/*public static ArrayList<String> getAllSubdirectoryPaths(String directoryPath) {
		return getAllSubdirectoryPaths(new File(directoryPath));
	}*/
	
	/*public static ArrayList<String> getAllSubdirectoryPaths(File directory) {
		if (directory.exists() && directory.isDirectory()) {
			File[] subdirectories = directory.listFiles();
			
			ArrayList<String> subdirectoryPaths = new ArrayList<String>();
			
			for (File subdirectory : subdirectories) {
				if (subdirectory.exists() && subdirectory.isDirectory()) {
					String subdirectoryPath;
					
					try {
						subdirectoryPath = subdirectory.getCanonicalPath();
					} catch (Exception exception) {
						subdirectoryPath = subdirectory.getAbsolutePath();
						
						exception.printStackTrace();
					}
					
					subdirectoryPaths.add(subdirectoryPath);
					subdirectoryPaths.addAll(getAllSubdirectoryPaths(subdirectory));
				}
			}
			
			return subdirectoryPaths;
		}
		
		return null;
	}*/
	
	/*public static List<String> loadFilePaths(ProfileInformation profileInformation) {
		File filePathList = new File(directoryPath + File.separator + profileInformation.getName());
		
		if (!(filePathList.exists() && filePathList.isFile() && filePathList.getName().endsWith(ProfileInformation.getFileExtension()))) {
			return null;
		}
		
		Scanner scanner;
		
		try {
			scanner = new Scanner(filePathList);
		} catch (Exception exception) {
			exception.printStackTrace();
			
			return null;
		}
		
		List<String> filePaths = new ArrayList<String>();
		
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine().trim();
			
			if (StringUtilities.isNullOrEmpty(line)) {
				continue;
			}
			
			filePaths.add(line);
		}
		
		scanner.close();
		
		return filePaths;
	}*/
	
	/*public static boolean saveFilePaths(ProfileInformation profileInformation, List<String> filePaths) {
		File filePathsDirectory = new File(directoryPath);
		
		if (!(filePathsDirectory.exists() && filePathsDirectory.isDirectory())) {
			try {
				filePathsDirectory.mkdirs();
			} catch (Exception exception) {
				exception.printStackTrace();
				
				return false;
			}
		}
		
		PrintWriter printWriter;
		
		try {
			printWriter = new PrintWriter(new File(filePathsDirectory.getAbsolutePath() + File.separator + profileInformation.getName()));
		} catch (Exception exception) {
			exception.printStackTrace();
			
			return false;
		}
		
		for (int i = 0; i < filePaths.size(); i++) {
			printWriter.println(filePaths.get(i));
		}
		
		if (printWriter.checkError()) {
			return false;
		}
		
		printWriter.close();
		
		return true;
	}*/
	
	public static List<String> getAllFilePaths(boolean relative, String directoryPath) {
		return getAllFilePaths(relative, Paths.get(directoryPath));
	}
	
	public static List<String> getAllFilePaths(boolean relative, Path directory) {
		String rootDirectoryPath = null;
		
		if (relative) {
			rootDirectoryPath = directory.toString();
		}
		
		List<String> filePaths = new ArrayList<String>();
		FileVisitor fileVisitor = new FileVisitor(rootDirectoryPath, filePaths);
		
		try {
			Files.walkFileTree(directory, fileVisitor);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		
		return filePaths;
	}
	
	public static void registerDirectory(String directoryPath,
			WatchService watchService, HashMap<WatchKey, Path> directoryMap) throws Exception {
		registerDirectory(Paths.get(directoryPath), watchService, directoryMap);
	}
	
	public static void registerDirectory(Path directory,
		WatchService watchService, HashMap<WatchKey, Path> directoryMap) throws Exception {
		FileVisitor directoryVisitor = new FileVisitor(watchService, directoryMap);
		
		Files.walkFileTree(directory, directoryVisitor);
	}
	
}