package com.bjitgroup.vautomation.ui.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.table.DefaultTableModel;

import com.bjitgroup.vautomation.core.models.filesystem.FileInformation;
import com.bjitgroup.vautomation.ui.ImageIcons;

public class FileInformationTableModel extends DefaultTableModel {
	
	private static final long serialVersionUID = -2448721830787957071L;
	
	private Map<Integer, FileInformation> fileInformationMap;
	
	private static final Class<?>[] columnTypes = new Class<?>[] {
		ImageIcon.class, String.class, String.class, String.class, String.class
	};
	
	private static final String[] columnNames = new String[] {
		"", "Name", "Relative path", "Size", "Last modified"
	};
	
	public FileInformationTableModel() {
		super(null, columnNames);
		
		fileInformationMap = new IdentityHashMap<Integer, FileInformation>();
	}
	
	public boolean contains(FileInformation fileInformation) {
		return fileInformationMap.values().contains(fileInformation);
	}
	
	public FileInformation getFileInformation(int index) {
		return fileInformationMap.get(index);
	}
	
	public synchronized void addRow(FileInformation fileInformation) {
		List<FileInformation> fileInformationList = getFileInformationList();
		fileInformationList.add(fileInformation);
		
		Collections.sort(fileInformationList);
		
		deleteRows();
		
		for (int i = 0; i < fileInformationList.size(); i++) {
			ImageIcon imageIcon = ImageIcons.notUploaded;
			FileInformation temporaryFileInformation = fileInformationList.get(i);
			
			if (temporaryFileInformation.isUploaded()) {
				imageIcon = ImageIcons.uploaded;
			}
			
			Object[] rowData = {
				imageIcon,
				temporaryFileInformation.getName(),
				temporaryFileInformation.getRelativePath(),
				temporaryFileInformation.getSize(),
				temporaryFileInformation.getLastModified()
			};
			
			addRow(rowData);
			
			fileInformationMap.put(getRowCount() - 1, temporaryFileInformation);
		}
	}
	
	public synchronized void updateRow(int rowIndex, FileInformation fileInformation) {
		FileInformation existingFileInformation = fileInformationMap.remove(rowIndex);
		
		if (existingFileInformation != null) {
			existingFileInformation.update(fileInformation);
		}
		
		addRow(existingFileInformation);
		
		/*int columnCount = getColumnCount();
		
		ImageIcon imageIcon = ImageIcons.notUploaded;
		
		if (fileInformation.isDeployed()) {
			imageIcon = ImageIcons.uploaded;
		}
		
		Object[] rowData = {
			imageIcon,
			fileInformation.getName(),
			fileInformation.getRelativePath(),
			fileInformation.getSize(),
			fileInformation.getLastModified()
		};
		
		for (int i = 0; i < columnCount; i++) {
			setValueAt(rowData[i], rowIndex, i);
		}
		
		fileInformationMap.get(rowIndex).update(fileInformation);*/
	}
	
	public synchronized FileInformation deleteRow(int rowIndex) {
		FileInformation fileInformation = fileInformationMap.remove(rowIndex);
		
		if (fileInformation != null) {
			List<FileInformation> fileInformationList = getFileInformationList();
			
			fileInformationMap.clear();
			Collections.sort(fileInformationList);
			
			for (int i = 0; i < fileInformationList.size(); i++) {
				fileInformationMap.put(i, fileInformationList.get(i));
			}
			
			removeRow(rowIndex);
		}
		
		return fileInformation;
	}
	
	public synchronized void deleteRows() {
		fileInformationMap.clear();
		setRowCount(0);
	}
	
	private synchronized List<FileInformation> getFileInformationList() {
		return new ArrayList<FileInformation>(fileInformationMap.values());
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return columnTypes[columnIndex];
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}
	
}