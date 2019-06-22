package com.bjitgroup.vautomation.core.utilities;

import java.awt.Desktop;
import java.io.File;
import java.util.Scanner;

public final class PlatformDependentUtilities {
	
	public static final byte OPERATING_SYSTEM = getOperatingSystem();
	
	public static final byte OS_UNSUPPORTED = 0;
	public static final byte OS_WINDOWS = 1;
	public static final byte OS_UBUNTU = 2;
	
	private static byte getOperatingSystem() {
		String operatingSystemName = System.getProperty("os.name");
		
		if (operatingSystemName == null) {
			return OS_UNSUPPORTED;
		}
		
		operatingSystemName = operatingSystemName.toLowerCase();
		
		if (operatingSystemName.indexOf("win") > -1) {
			return OS_WINDOWS;
		} else if (operatingSystemName.indexOf("nix") > -1 ||
				   operatingSystemName.indexOf("nux") > -1 ||
				   operatingSystemName.indexOf("aix") > -1) {
			if (execute(true, "nautilus", "--help")) {
				return OS_UBUNTU;
			}
		}
		
		return OS_UNSUPPORTED;
	}
	
	public static String getWorkingDirectoryPath() {
		String workingDirectoryPath = PlatformDependentUtilities.class.getResource("").getPath();
		
		int index = workingDirectoryPath.lastIndexOf(".jar!" + File.separator);
		
		if (index == -1) {
			workingDirectoryPath = System.getProperty("user.dir");
		} else {
			switch (OPERATING_SYSTEM) {
			case OS_UBUNTU:
				workingDirectoryPath = workingDirectoryPath.substring(0, index);
				
				if ((index = workingDirectoryPath.indexOf(File.separator)) != -1) {
					workingDirectoryPath = workingDirectoryPath.substring(index, workingDirectoryPath.length());
				}
				
				workingDirectoryPath = workingDirectoryPath.substring(0, workingDirectoryPath.lastIndexOf(File.separator));
				
				break;
			default:
				break;
			}
		}
		
		return workingDirectoryPath;
	}
	
	// returns 'true' if successful...
	// returns 'false' if failed...
	public static boolean open(boolean allowFile, String path) {
		boolean successful = false;
		
		File file = new File(path);
		
		if (file.exists()) {
			if (!allowFile && file.isFile()) {
				int lastIndexOfFileSeparator = path.lastIndexOf(File.separator);
				
				if (lastIndexOfFileSeparator != -1) {
					path = path.substring(0, lastIndexOfFileSeparator);
				}
			}
			
			try {
				Desktop.getDesktop().open(file);
				
				successful = true;
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}
		
		return successful;
	}
	
	// returns 'true' if successful...
	// returns 'false' if failed...
	private static boolean execute(boolean checkProcessOutput, String... command) {
		boolean successful = true;
		
		try {
			ProcessBuilder processBuilder = new ProcessBuilder(command);
			Process process = processBuilder.start();
			
			if (checkProcessOutput) {
				Scanner scanner = new Scanner(process.getInputStream());
				
				if (scanner.hasNext()) {
					String line = scanner.nextLine().toLowerCase();
					
					if (line.contains("not found")) {
						successful = false;
					}
				}
				
				scanner.close();
			}
		} catch (Exception exception) {
			exception.printStackTrace();
			
			successful = false;
		}
		
		return successful;
	}
	
	public static void showInSystemExplorer(String path) {
		boolean successful = false;
		
		File file = new File(path);
		
		try {
			path = file.getCanonicalPath();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		
		switch (OPERATING_SYSTEM) {
		case OS_WINDOWS:
			successful = execute(false, "explorer.exe", "/select,", path);
			
			break;
		case OS_UBUNTU:
			successful = execute(false, "nautilus", path);
			
			break;
		default:
			break;
		}
		
		if (!successful) {
			open(false, path);
		}
	}
	
}