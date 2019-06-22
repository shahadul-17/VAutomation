package com.bjitgroup.vautomation.core.models;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.bjitgroup.vautomation.Application;
import com.bjitgroup.vautomation.core.services.DataManagement;

public class ServerDetails {
	
	private char[] password;
	
	private String name = "test";		// server name...
	private String userName;
	private String address;
	
	private Map<String, String> directoryPathMap;
	
	private static final String fileExtension = "svr";
	private static final String directoryPath = Application.DATA_DIRECTORY_PATH + "remote-servers";
	
	public ServerDetails() {
		directoryPathMap = new HashMap<String, String>();
	}
	
	public char[] getPassword() {
		return password;
	}

	public void setPassword(char[] password) {
		this.password = password;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
	public Map<String, String> getDirectoryPathMap() {
		return directoryPathMap;
	}
	
	public boolean save() {
		StringBuilder directoryPathMapBuilder = new StringBuilder();
		Set<String> keySet = directoryPathMap.keySet();
		
		int i = 0;
		
		for (String key : keySet) {
			directoryPathMapBuilder.append(key);
			directoryPathMapBuilder.append("::");
			directoryPathMapBuilder.append(directoryPathMap.get(key));
			
			if (i != keySet.size() - 1) {
				directoryPathMapBuilder.append("::::");
			}
		}
		
		Map<String, String> dataMap = new HashMap<String, String>();
		dataMap.put("password", new String(password));
		dataMap.put("userName", userName);
		dataMap.put("address", address);
		dataMap.put("directoryPathMap", directoryPathMapBuilder.toString());
		
		return DataManagement.saveMap(directoryPath + File.separator + name + '.' + fileExtension, dataMap);
	}
	
	@Override
	protected void finalize() throws Throwable {
		if (password != null) {
			for (int i = 0; i < password.length; i++) {
				password[i] = 0;
			}
		}
		
		super.finalize();
	}
	
	@Override
	public String toString() {
		return address;
	}
	
	public static String getFileExtension() {
		return fileExtension;
	}
	
	public static String getDirectoryPath() {
		return directoryPath;
	}
	
	public static ServerDetails load(String name) {
		return load(new File(directoryPath + File.separator + name + '.' + fileExtension));
	}
	
	public static ServerDetails load(File server) {
		Map<String, String> dataMap = DataManagement.loadMap(server);
		
		ServerDetails serverDetails = new ServerDetails();
		serverDetails.name = server.getName();
		serverDetails.name = serverDetails.name.substring(0, serverDetails.name.lastIndexOf('.'));
		
		String password = dataMap.get("password");
		
		if (password != null) {
			serverDetails.password = password.toCharArray();
		}
		
		String userName = dataMap.get("userName");
		
		if (userName != null) {
			serverDetails.userName = userName.trim();
		}
		
		String address = dataMap.get("address");
		
		if (address != null) {
			serverDetails.address = address.trim();
		}
		
		String directoryPathMapData = dataMap.get("directoryPathMap");
		
		if (directoryPathMapData != null) {
			String[] splittedDirectoryPathMapData = directoryPathMapData.trim().split("::::");
			
			for (int i = 0; i < splittedDirectoryPathMapData.length; i++) {
				String[] splittedDirectoryPathData = splittedDirectoryPathMapData[i].split("::");
				
				if (splittedDirectoryPathData.length != 2) {
					continue;
				}
				
				serverDetails.directoryPathMap.put(splittedDirectoryPathData[0].trim(), splittedDirectoryPathData[1].trim());
			}
		}
		
		return serverDetails;
	}
	
}