package com.bjitgroup.vautomation.core.services.filesystem;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.bjitgroup.vautomation.core.models.ProfileInformation;
import com.bjitgroup.vautomation.core.models.filesystem.FileInformation;
import com.bjitgroup.vautomation.core.utilities.FileSystemUtilities;

public class FileSystemTracker extends Thread {
	
	private volatile boolean started;
	
	private ProfileInformation profileInformation;
	private WatchService watchService;
	
	private List<FileSystemListener> fileSystemListeners;
	
	private HashMap<String, FileInformation> fileInformationMap;
	private HashMap<WatchKey, Path> directoryMap;
	
	public FileSystemTracker(ProfileInformation profileInformation) throws Exception {
		watchService = FileSystems.getDefault().newWatchService();
		directoryMap = new HashMap<WatchKey, Path>();
		
		FileSystemUtilities.registerDirectory(profileInformation.getProjectDirectory(), watchService, directoryMap);
		
		this.profileInformation = profileInformation;
		fileSystemListeners = new ArrayList<FileSystemListener>();
		fileInformationMap = new HashMap<String, FileInformation>();
	}
	
	public void addFileSystemListener(FileSystemListener fileSystemListener) {
		if (fileSystemListener == null) {
			return;
		}
		
		fileSystemListeners.add(fileSystemListener);
	}
	
	public synchronized void close() {
		started = false;
		
		Set<WatchKey> watchKeys = directoryMap.keySet();
		
		for (WatchKey watchKey : watchKeys) {
			watchKey.cancel();
		}
		
		directoryMap.clear();
		
		try {
			watchService.close();
		} catch (IOException exception) {
			exception.printStackTrace();
		}
		
		fileSystemListeners.clear();
		fileInformationMap.clear();
		profileInformation.saveAllFilePaths();
	}
	
	private void retrieveTrackedFiles() {
		List<FileInformation> trackedFileInformationList = profileInformation.getTrackedFileInformationList();
		List<FileInformation> modifiedTrackedFileInformationList = new ArrayList<FileInformation>();
		
		for (int i = 0; i < trackedFileInformationList.size(); i++) {
			FileInformation fileInformation = fileInformationMap.get(trackedFileInformationList.get(i).getPath());
			
			if (fileInformation == null) {
				continue;
			}
			
			modifiedTrackedFileInformationList.add(fileInformation);
		}
		
		for (int i = 0; i < fileSystemListeners.size(); i++) {
			fileSystemListeners.get(i).trackedFilesChanged(modifiedTrackedFileInformationList);
		}
	}
	
	private void retrieveAllModifiedFiles() {
		List<FileInformation> allModifiedFileInformationList = profileInformation.getAllModifiedFileInformationList();
		
		for (int i = 0; i < allModifiedFileInformationList.size(); i++) {
			FileInformation fileInformation = allModifiedFileInformationList.get(i);
			
			fileInformationMap.put(fileInformation.getPath(), fileInformation);
		}
	}
	
	private void retrieveUntrackedFiles() {
		List<String> currentFilePaths = FileSystemUtilities.getAllFilePaths(false, profileInformation.getProjectDirectory());
		List<String> previouslyStoredFilePaths = profileInformation.getAllFilePaths();
		
		if (previouslyStoredFilePaths.size() == 0) {
			return;
		}
		
		for (int i = 0; i < previouslyStoredFilePaths.size(); i++) {
			currentFilePaths.remove(previouslyStoredFilePaths.get(i));
		}
		
		List<FileInformation> modifiedFileInformationList = FileSystemUtilities.toFileInformationList(
			profileInformation.getProjectDirectory(), currentFilePaths
		);
		
		if (modifiedFileInformationList == null) {
			return;
		}
		
		for (int i = 0; i < modifiedFileInformationList.size(); i++) {
			FileInformation fileInformation = modifiedFileInformationList.get(i);
			
			fileInformationMap.put(fileInformation.getPath(), fileInformation);
		}
	}
	
	@Override
	public synchronized void start() {
		started = true;
		
		super.start();
	}
	
	@Override
	public void run() {
		retrieveAllModifiedFiles();
		retrieveUntrackedFiles();
		
		List<FileInformation> modifiedFileInformationList = new ArrayList<FileInformation>(fileInformationMap.values());
		
		profileInformation.clearAllModifiedFileInformationList();
		profileInformation.addToAllModifiedFileInformationList(modifiedFileInformationList);
		profileInformation.save();
		
		for (int i = 0; i < fileSystemListeners.size(); i++) {
			fileSystemListeners.get(i).filesChanged(modifiedFileInformationList);
		}
		
		retrieveTrackedFiles();
		
		while (started) {
			WatchKey watchKey;
			
			try {
				watchKey = watchService.take();
			} catch (Exception exception) {
				exception.printStackTrace();
				
				break;
			}
			
			for (WatchEvent<?> watchEvent : watchKey.pollEvents()) {
				Path modifiedFilePath = directoryMap.get(watchKey).resolve((Path) watchEvent.context());
				String modifiedFilePathString = modifiedFilePath.toString();
				Kind<?> watchEventKind = watchEvent.kind();
				
				if (watchEventKind.equals(StandardWatchEventKinds.ENTRY_CREATE)) {
					if (Files.isDirectory(modifiedFilePath)) {		// if a directory is created, we register that directory...
						try {
							FileSystemUtilities.registerDirectory(modifiedFilePath, watchService, directoryMap);
						} catch (Exception exception) {
							exception.printStackTrace();
						}
						
						// duplicate code below...
						List<String> modifiedFilePaths = FileSystemUtilities.getAllFilePaths(false, modifiedFilePathString);
						
						for (int i = 0; i < modifiedFilePaths.size(); i++) {
							String temporaryModifiedFilePath = modifiedFilePaths.get(i);
							
							if (!fileInformationMap.containsKey(temporaryModifiedFilePath)) {
								FileInformation fileInformation = new FileInformation(
									profileInformation.getProjectDirectory(),
									temporaryModifiedFilePath
								);
								
								fileInformationMap.put(temporaryModifiedFilePath, fileInformation);
							}
						}
					} else {										// if a file is created, we put that file to our mapping...
						FileInformation fileInformation = new FileInformation(
							profileInformation.getProjectDirectory(),
							modifiedFilePathString
						);
						
						fileInformationMap.put(modifiedFilePathString, fileInformation);
					}
				} else if (watchEventKind.equals(StandardWatchEventKinds.ENTRY_MODIFY)) {
					if (Files.isDirectory(modifiedFilePath)) {		// when a directory is modified, we manually retrieve the file paths...
						// duplicate code below...
						List<String> modifiedFilePaths = FileSystemUtilities.getAllFilePaths(false, modifiedFilePathString);
						
						for (int i = 0; i < modifiedFilePaths.size(); i++) {
							String temporaryModifiedFilePath = modifiedFilePaths.get(i);
							
							if (!fileInformationMap.containsKey(temporaryModifiedFilePath)) {
								FileInformation fileInformation = new FileInformation(
									profileInformation.getProjectDirectory(),
									temporaryModifiedFilePath
								);
								
								fileInformationMap.put(temporaryModifiedFilePath, fileInformation);
							}
						}
					} else {	// if a file is modified, we update that file's information...
						FileInformation modifiedFileInformation = fileInformationMap.get(modifiedFilePathString);
						
						if (modifiedFileInformation == null) {
							modifiedFileInformation = new FileInformation(
								profileInformation.getProjectDirectory(),
								modifiedFilePathString
							);
							
							fileInformationMap.put(modifiedFilePathString, modifiedFileInformation);
						} else {
							// updates file size and last modified date...
							modifiedFileInformation.update();
						}
					}
				} else {		// StandardWatchEventKinds.ENTRY_DELETE
					fileInformationMap.remove(modifiedFilePathString);		// removing the path that was deleted...
					
					// checking rest of the files if they exist... if not, we remove that file...
					List<FileInformation> fileInformationList = new ArrayList<FileInformation>(fileInformationMap.values());
					
					for (int i = 0; i < fileInformationList.size(); i++) {
						FileInformation fileInformation = fileInformationList.get(i);
						
						if (!fileInformation.exists()) {
							fileInformationMap.remove(fileInformation.getPath());
						}
					}
				}
			}
			
			modifiedFileInformationList.clear();
			modifiedFileInformationList = new ArrayList<FileInformation>(fileInformationMap.values());
			
			profileInformation.clearAllModifiedFileInformationList();
			profileInformation.addToAllModifiedFileInformationList(modifiedFileInformationList);
			profileInformation.save();
			
			for (int i = 0; i < fileSystemListeners.size(); i++) {
				fileSystemListeners.get(i).filesChanged(modifiedFileInformationList);
			}
			
			retrieveTrackedFiles();
			
			if (!watchKey.reset()) {
				directoryMap.remove(watchKey);
				
				if (directoryMap.isEmpty()) {
					break;
				}
			}
		}
	}
	
}