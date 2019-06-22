package com.bjitgroup.vautomation.core.models;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bjitgroup.vautomation.Application;
import com.bjitgroup.vautomation.core.models.filesystem.FileInformation;
import com.bjitgroup.vautomation.core.services.DataManagement;
import com.bjitgroup.vautomation.core.utilities.FileSystemUtilities;
import com.bjitgroup.vautomation.core.utilities.StringUtilities;

public class ProfileInformation {
	
	private String name;
	private String projectDirectory;		// root directory of the project...
	
	private ServerDetails serverDetails;	// remote server information...
	
	private List<FileInformation> trackedFileInformationList;
	private List<FileInformation> allModifiedFileInformationList;
	
	private static final String fileExtension = "vap";
	private static final String directoryPath = Application.DATA_DIRECTORY_PATH + "profiles";
	
	public ProfileInformation() {
		trackedFileInformationList = new ArrayList<FileInformation>();
		allModifiedFileInformationList = new ArrayList<FileInformation>();
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getProjectDirectory() {
		return projectDirectory;
	}
	
	public void setProjectDirectory(String projectDirectory) {
		this.projectDirectory = projectDirectory;
	}
	
	public ServerDetails getServerDetails() {
		return serverDetails;
	}
	
	public List<String> getAllFilePaths() {
		List<String> dataList = DataManagement.loadList(directoryPath + File.separator + '$' + name);
		
		for (int i = 0; i < dataList.size(); i++) {
			dataList.set(i, projectDirectory + dataList.get(i));
		}
		
		return dataList;
	}
	
	public void deleteAllFilePaths() {
		DataManagement.delete(directoryPath + File.separator + '$' + name);
	}
	
	public void saveAllFilePaths() {
		DataManagement.saveList(
			directoryPath + File.separator + '$' + name,
			FileSystemUtilities.getAllFilePaths(true, projectDirectory)
		);
	}
	
	public List<FileInformation> getTrackedFileInformationList() {
		return trackedFileInformationList;
	}
	
	public List<FileInformation> getAllModifiedFileInformationList() {
		return allModifiedFileInformationList;
	}
	
	public void addToTrackedFileInformationList(FileInformation informationOfFileToTrack) {
		trackedFileInformationList.add(informationOfFileToTrack);
	}
	
	public void removeFromTrackedFileInformationList(FileInformation informationOfFileToTrack) {
		trackedFileInformationList.remove(informationOfFileToTrack);
	}
	
	public void clearTrackedFileInformationList() {
		trackedFileInformationList.clear();
	}
	
	public void addToAllModifiedFileInformationList(FileInformation modifiedFileInformation) {
		allModifiedFileInformationList.add(modifiedFileInformation);
	}
	
	public void addToAllModifiedFileInformationList(List<FileInformation> modifiedFileInformationList) {
		allModifiedFileInformationList.addAll(modifiedFileInformationList);
	}
	
	public void removeFromAllModifiedFileInformationList(FileInformation modifiedFileInformation) {
		allModifiedFileInformationList.remove(modifiedFileInformation);
	}
	
	public void clearAllModifiedFileInformationList() {
		allModifiedFileInformationList.clear();
	}
	
	public boolean save() {
		Map<String, String> dataMap = new HashMap<String, String>();
		
		if (serverDetails != null) {
			dataMap.put("serverName", serverDetails.getName());
		}
		
		dataMap.put("projectDirectory", projectDirectory);
		dataMap.put("trackedFileInformationList", StringUtilities.getFormattedFileInformationList(trackedFileInformationList));
		dataMap.put("allModifiedFileInformationList", StringUtilities.getFormattedFileInformationList(allModifiedFileInformationList));
		
		return DataManagement.saveMap(directoryPath + File.separator + name + '.' + fileExtension, dataMap);
	}
	
	@Override
	protected void finalize() throws Throwable {
		clearTrackedFileInformationList();
		super.finalize();
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	public static String getFileExtension() {
		return fileExtension;
	}
	
	public static String getDirectoryPath() {
		return directoryPath;
	}
	
	public static ProfileInformation load(File profile) {
		Map<String, String> dataMap = DataManagement.loadMap(profile);
		
		if (dataMap == null) {
			return null;
		}
		
		ProfileInformation profileInformation = new ProfileInformation();
		profileInformation.name = profile.getName().substring(0, profile.getName().lastIndexOf('.'));
		profileInformation.projectDirectory = dataMap.get("projectDirectory");
		
		String serverName = dataMap.get("serverName");
		
		if (serverName != null) {
			profileInformation.serverDetails = ServerDetails.load(serverName.trim());
		}
		
		String trackedFileInformationListData = dataMap.get("trackedFileInformationList");
		
		if (trackedFileInformationListData != null) {
			String[] splittedFileInformationListData = trackedFileInformationListData.split("::::");
			
			for (int i = 0; i < splittedFileInformationListData.length; i++) {
				String[] splittedFileInformationData = splittedFileInformationListData[i].split("::");
				
				if (splittedFileInformationData.length != 4) {
					continue;
				}
				
				boolean uploaded = Boolean.parseBoolean(splittedFileInformationData[0].trim());
				long size = Long.parseLong(splittedFileInformationData[1].trim());
				long lastModified = Long.parseLong(splittedFileInformationData[2].trim());
				
				FileInformation fileInformation = new FileInformation(profileInformation.projectDirectory, splittedFileInformationData[3].trim());
				fileInformation.update(uploaded, size, lastModified);
				
				profileInformation.trackedFileInformationList.add(fileInformation);
			}
		}
		
		String allModifiedFileInformationListData = dataMap.get("allModifiedFileInformationList");
		
		if (allModifiedFileInformationListData != null) {
			String[] splittedFileInformationListData = allModifiedFileInformationListData.split("::::");
			
			for (int i = 0; i < splittedFileInformationListData.length; i++) {
				String[] splittedFileInformationData = splittedFileInformationListData[i].split("::");
				
				if (splittedFileInformationData.length != 4) {
					continue;
				}
				
				boolean uploaded = Boolean.parseBoolean(splittedFileInformationData[0].trim());
				long size = Long.parseLong(splittedFileInformationData[1].trim());
				long lastModified = Long.parseLong(splittedFileInformationData[2].trim());
				
				FileInformation fileInformation = new FileInformation(profileInformation.projectDirectory, splittedFileInformationData[3].trim());
				fileInformation.update(uploaded, size, lastModified);
				
				profileInformation.allModifiedFileInformationList.add(fileInformation);
			}
		}
		
		return profileInformation;
	}
	
}