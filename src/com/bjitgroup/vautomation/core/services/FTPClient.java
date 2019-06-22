package com.bjitgroup.vautomation.core.services;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPReply;

import com.bjitgroup.vautomation.core.models.ServerDetails;
import com.bjitgroup.vautomation.core.models.filesystem.FileInformation;

public class FTPClient {
	
	private ServerDetails serverDetails;
	private org.apache.commons.net.ftp.FTPClient ftpClient;
	
	public FTPClient(ServerDetails serverDetails) throws Exception {
		this.serverDetails = serverDetails;
		
		ftpClient = new org.apache.commons.net.ftp.FTPClient();
		ftpClient.addProtocolCommandListener(new PrintCommandListener(System.out));
		
		try {
			ftpClient.connect(serverDetails.getAddress());
		} catch (UnknownHostException unknownHostException) {
			throw new Exception("Unable to locate remote server.");
		}
		
		if (!FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
			ftpClient.disconnect();
		}
		
		if (!ftpClient.login(serverDetails.getUserName(), new String(serverDetails.getPassword()))) {
			throw new Exception("Invalid username or password.");
		}
		
		ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
		ftpClient.enterLocalPassiveMode();
	}
	
	private boolean upload(String localFilePath, String remoteFilePath) {
		try {
			FileInputStream fileInputStream = new FileInputStream(localFilePath);
			ftpClient.storeFile(remoteFilePath, fileInputStream);
			
			return true;
		} catch (Exception exception) {
			exception.printStackTrace();
			
			return false;
		}
	}
	
	public void upload(List<FileInformation> fileInformationList) {
		Map<String, String> localToRemoteDirectoryMap = serverDetails.getDirectoryPathMap();
		Set<String> localDirectories = localToRemoteDirectoryMap.keySet();
		
		for (FileInformation fileInformation : fileInformationList) {
			for (String localDirectory : localDirectories) {
				String localFilePath = fileInformation.getPath();
				
				int indexOfLocalDirectory = localFilePath.indexOf(localDirectory);
				
				if (indexOfLocalDirectory == -1) {
					continue;
				}
				
				String remoteFilePath = localFilePath.substring(indexOfLocalDirectory + localDirectory.length());
				remoteFilePath = localToRemoteDirectoryMap.get(localDirectory) + remoteFilePath;
				
				boolean uploaded = upload(localFilePath, remoteFilePath);
				
				fileInformation.setUploaded(uploaded);
			}
		}
	}
	
	public void disconnect() {
		if (ftpClient.isConnected()) {
			try {
				ftpClient.logout();
				ftpClient.disconnect();
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}
	}
	
}