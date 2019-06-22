package com.bjitgroup.vautomation.core.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.bjitgroup.vautomation.Application;
import com.bjitgroup.vautomation.core.models.filesystem.FileInformation;

public class ZipArchiver {
	
	private String name;		// archive name -> {name}.zip
	private String path;
	
	private List<FileInformation> fileInformationList;
	
	private static final String directoryPath = Application.DATA_DIRECTORY_PATH + "archives";
	
	public ZipArchiver(String name, List<FileInformation> fileInformationList) {
		this.name = name;
		this.fileInformationList = fileInformationList;
	}
	
	public boolean create() {
		try {
			File file = new File(directoryPath);
			
			if (!(file.exists() && file.isDirectory())) {
				file.mkdirs();
			}
			
			path = file.getAbsolutePath() + File.separator + name + ".zip";
			
			for (int i = 2; new File(path).exists(); i++) {
				path = file.getAbsolutePath() + File.separator + name + " (" + i + ").zip";
			}
			
			FileOutputStream fileOutputStream = new FileOutputStream(path);
			ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream);
			
			byte[] buffer = new byte[4096];
			int bytesRead = 0;
			
			for (int i = 0; i < fileInformationList.size(); i++) {
				FileInformation fileInformation = fileInformationList.get(i);
				
				try {
					FileInputStream fileInputStream = new FileInputStream(fileInformation.getPath());
					String zipEntryName = fileInformation.getRelativePath() + fileInformation.getName();
					ZipEntry zipEntry = new ZipEntry(zipEntryName.substring(1));		// removing starting path separator...
					
					zipOutputStream.putNextEntry(zipEntry);
					
					while ((bytesRead = fileInputStream.read(buffer, 0, buffer.length)) > -1) {
						zipOutputStream.write(buffer, 0, bytesRead);
						zipOutputStream.flush();
					}
					
					zipOutputStream.closeEntry();
					fileInputStream.close();
				} catch (Exception exception) {
					exception.printStackTrace();
				}
			}
			
			zipOutputStream.close();
			fileOutputStream.close();
			
			return true;
		} catch (Exception exception) {
			exception.printStackTrace();
			
			return false;
		}
	}
	
	public String getPath() {
		return path;
	}
	
	public static String getDirectoryPath() {
		return directoryPath;
	}
	
}