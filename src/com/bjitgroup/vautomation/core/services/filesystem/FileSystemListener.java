package com.bjitgroup.vautomation.core.services.filesystem;

import java.util.List;

import com.bjitgroup.vautomation.core.models.filesystem.FileInformation;

public interface FileSystemListener {
	
	void trackedFilesChanged(List<FileInformation> fileInformationList);
	void filesChanged(List<FileInformation> fileInformationList);
	
}