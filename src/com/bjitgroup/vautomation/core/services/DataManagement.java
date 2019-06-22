package com.bjitgroup.vautomation.core.services;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public final class DataManagement {
	
	public static void delete(String dataFilePath) {
		File dataFile = new File(dataFilePath);
		
		if (dataFile.exists()) {
			try {
				dataFile.delete();
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}
	}
	
	public static List<String> loadList(String dataFilePath) {
		return loadList(new File(dataFilePath));
	}
	
	public static List<String> loadList(File dataFile) {
		List<String> dataList = new ArrayList<String>();
		
		if (!dataFile.exists()) {
			return dataList;
		}
		
		try {
			Scanner dataScanner = new Scanner(dataFile);
			
			while (dataScanner.hasNextLine()) {
				String line = dataScanner.nextLine();
				
				if (line == null || (line = line.trim()).isEmpty()) {
					continue;
				}
				
				dataList.add(line);
			}
			
			dataScanner.close();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		
		return dataList;
	}
	
	public static boolean saveList(String dataFilePath, List<String> dataList) {
		try {
			// creates any missing directory in dataFilePath...
			new File(dataFilePath.substring(0, dataFilePath.lastIndexOf(File.separator))).mkdirs();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		
		PrintWriter dataWriter;
		
		try {
			dataWriter = new PrintWriter(dataFilePath);
		} catch (Exception exception) {
			exception.printStackTrace();
			
			return false;
		}
		
		StringBuilder dataBuilder = new StringBuilder();
		
		for (int i = 0; i < dataList.size(); i++) {
			dataBuilder.append(dataList.get(i));
			dataBuilder.append('\n');
		}
		
		dataWriter.print(dataBuilder.toString().trim());
		dataWriter.flush();
		dataWriter.close();
		
		return true;
	}
	
	public static Map<String, String> loadMap(String dataFilePath) {
		return loadMap(new File(dataFilePath));
	}
	
	public static Map<String, String> loadMap(File dataFile) {
		Map<String, String> dataMap = new HashMap<String, String>();
		
		if (!dataFile.exists()) {
			return dataMap;
		}
		
		try {
			Scanner dataScanner = new Scanner(dataFile);
			
			while (dataScanner.hasNextLine()) {
				String data = dataScanner.nextLine();
				
				if (data.isEmpty()) {
					continue;
				}
				
				int indexOfEqualSign = data.indexOf('=');
				
				if (indexOfEqualSign == -1 || indexOfEqualSign == 0 ||
						indexOfEqualSign == data.length() - 1) {
					continue;
				}
				
				String key = data.substring(0, indexOfEqualSign).trim();
				String value = data.substring(indexOfEqualSign + 1).trim();
				
				try {
					dataMap.put(key, value);
				} catch (Exception exception) {
					exception.printStackTrace();
				}
			}
			
			dataScanner.close();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		
		return dataMap;
	}
	
	public static boolean saveMap(String dataFilePath, Map<String, String> dataMap) {
		try {
			// creates any missing directory in dataFilePath...
			new File(dataFilePath.substring(0, dataFilePath.lastIndexOf(File.separator))).mkdirs();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		
		PrintWriter dataWriter;
		
		try {
			dataWriter = new PrintWriter(dataFilePath);
		} catch (Exception exception) {
			exception.printStackTrace();
			
			return false;
		}
		
		StringBuilder dataBuilder = new StringBuilder();
		
		Set<String> keySet = dataMap.keySet();
		
		for (String key : keySet) {
			dataBuilder.append(key);
			dataBuilder.append('=');
			dataBuilder.append(dataMap.get(key));
			dataBuilder.append('\n');
		}
		
		dataWriter.print(dataBuilder.toString().trim());
		dataWriter.flush();
		dataWriter.close();
		
		return true;
	}
	
}