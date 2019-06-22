package com.bjitgroup.vautomation.core.models.filesystem;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.List;

public class FileVisitor extends SimpleFileVisitor<Path> {
	
	private String rootDirectoryPath = "";
	private WatchService watchService;
	
	private List<String> filePaths;
	private HashMap<WatchKey, Path> directoryMap;
	
	public FileVisitor(String rootDirectoryPath, List<String> filePaths) {
		if (rootDirectoryPath != null) {
			this.rootDirectoryPath = rootDirectoryPath;
		}
		
		this.filePaths = filePaths;
	}
	
	public FileVisitor(WatchService watchService, HashMap<WatchKey, Path> directoryMap) {
		this.watchService = watchService;
		this.directoryMap = directoryMap;
	}
	
	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) throws IOException {
		if (filePaths != null && attributes.isRegularFile()) {
			filePaths.add(file.toString().replace(rootDirectoryPath, ""));
		}
		
		return FileVisitResult.CONTINUE;
	}
	
	@Override
	public FileVisitResult visitFileFailed(Path file, IOException exception) {
		exception.printStackTrace();
		
		return FileVisitResult.CONTINUE;
	}
	
	@Override
	public FileVisitResult preVisitDirectory(Path directory, BasicFileAttributes fileAttributes) throws IOException {
		if (watchService != null) {
			WatchKey watchKey = directory.register(watchService,
				StandardWatchEventKinds.ENTRY_CREATE,
				StandardWatchEventKinds.ENTRY_MODIFY,
				StandardWatchEventKinds.ENTRY_DELETE
			);
			
			directoryMap.put(watchKey, directory);
		}
		
		return FileVisitResult.CONTINUE;
	}
	
}