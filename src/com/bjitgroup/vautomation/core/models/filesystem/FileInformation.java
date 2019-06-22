package com.bjitgroup.vautomation.core.models.filesystem;

import java.io.File;

import com.bjitgroup.vautomation.core.utilities.StringUtilities;

public class FileInformation implements Comparable<FileInformation> {
	
	private boolean uploaded;		// if file is deployed in server...
	
	private long size;				// size of the file...
	private long lastModified;		// last modified date of the file...
	
	private String name;
	private String extension;
	private String relativePath;	// file path relative to project directory (without file name)...
	private String path;			// complete file path with file name...
	
	public FileInformation(String rootDirectoryPath, String path) {
		this.path = path;
		
		File file = new File(path);
		size = file.length();
		lastModified = file.lastModified();
		name = file.getName();
		extension = name.substring(name.lastIndexOf('.') + 1);
		
		relativePath = path.replace(rootDirectoryPath, "");
		
		if (relativePath.charAt(0) != File.separatorChar) {
			relativePath = File.separator + relativePath;
		}
		
		relativePath = relativePath.substring(0, relativePath.lastIndexOf(File.separator) + 1);
	}
	
	public boolean isUploaded() {
		return uploaded;
	}
	
	public void setUploaded(boolean uploaded) {
		this.uploaded = uploaded;
	}
	
	public String getName() {
		return name;
	}
	
	public String getExtension() {
		return extension;
	}
	
	public String getRelativePath() {
		return relativePath;
	}
	
	public String getSize() {
		return StringUtilities.getFormattedSize(size);
	}
	
	public String getLastModified() {
		return StringUtilities.getFormattedDate(lastModified);
	}
	
	public String getPath() {
		return path;
	}
	
	public boolean exists() {
		File file = new File(path);
		
		return file.exists() && file.isFile();
	}
	
	public void update() {
		uploaded = false;
		
		File file = new File(path);
		size = file.length();
		lastModified = file.lastModified();
	}
	
	public void update(boolean uploaded, long size, long lastModified) {
		if (Long.compare(this.size, size) == 0 &&
			Long.compare(this.lastModified, lastModified) == 0) {
			this.uploaded = uploaded;
		} else {
			this.uploaded = false;
		}
	}
	
	public void update(FileInformation fileInformation) {
		if (fileInformation == null) {
			return;
		}
		
		uploaded = fileInformation.uploaded;
		size = fileInformation.size;
		lastModified = fileInformation.lastModified;
		name = fileInformation.name;
		extension = fileInformation.extension;
		relativePath = fileInformation.relativePath;
		path = fileInformation.path;
	}
	
	@Override
	public int compareTo(FileInformation fileInformation) {
		// we want files to be sorted in descending order according to last modified...
		return Long.compare(fileInformation.lastModified, lastModified);
	}
	
	@Override
	public boolean equals(Object object) {
		if (object == null) {
			return false;
		}
		
		return path.equalsIgnoreCase(object.toString());
	}
	
	public String toStorableString() {
		return uploaded + "::" + size + "::" + lastModified + "::" + path;
	}
	
	@Override
	public String toString() {
		return path;
	}
	
}