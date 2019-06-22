package com.bjitgroup.vautomation.ui;

import java.awt.Color;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.DefaultTableCellRenderer;

import com.bjitgroup.vautomation.core.models.filesystem.FileInformation;
import com.bjitgroup.vautomation.core.utilities.PlatformDependentUtilities;
import com.bjitgroup.vautomation.ui.models.FileInformationTableModel;
import com.bjitgroup.vautomation.ui.utilities.UIUtilities;

public class FileInformationTable extends JTable implements PopupMenuListener, ActionListener {
	
	private static final long serialVersionUID = -6810444696637741668L;
	
	private JPopupMenu popupMenu;
	
	private JMenuItem menuItemCopyFileName;
	private JMenuItem menuItemCopyFileExtension;
	private JMenuItem menuItemCopyRelativeFilePath;
	private JMenuItem menuItemCopyCompleteRelativeFilePath;
	private JMenuItem menuItemCopyFilePath;
	private JMenuItem menuItemCopyCompleteFilePath;
	private JMenuItem menuItemShowInSystemExplorer;
	
	private FileInformationTableModel tableModel;
	
	public FileInformationTable() {
		initialize();
	}
	
	private void initialize() {
		setBackground(Color.WHITE);
		UIUtilities.changeExistingFontSizeAndStyle(13, -1, this);
		UIUtilities.changeExistingFontSizeAndStyle(13, -1, getTableHeader());
		setRowHeight(30);
		
		tableModel = new FileInformationTableModel();
		setModel(tableModel);
		
		DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
		cellRenderer.setHorizontalAlignment(0);
		
		getColumnModel().getColumn(3).setCellRenderer(cellRenderer);
		getColumnModel().getColumn(4).setCellRenderer(cellRenderer);
		
        popupMenu = new JPopupMenu();
		popupMenu.addPopupMenuListener(this);
		setComponentPopupMenu(popupMenu);
		
		menuItemCopyFileName = new JMenuItem("Copy file name(s)");
		menuItemCopyFileName.addActionListener(this);
		popupMenu.add(menuItemCopyFileName);
		
		menuItemCopyFileExtension = new JMenuItem("Copy file extension(s)");
		menuItemCopyFileExtension.addActionListener(this);
		popupMenu.add(menuItemCopyFileExtension);
		
		popupMenu.addSeparator();
		
		menuItemCopyRelativeFilePath = new JMenuItem("Copy relative file path(s)");
		menuItemCopyRelativeFilePath.addActionListener(this);
		popupMenu.add(menuItemCopyRelativeFilePath);
		
		menuItemCopyCompleteRelativeFilePath = new JMenuItem("Copy complete relative file path(s)");
		menuItemCopyCompleteRelativeFilePath.addActionListener(this);
		popupMenu.add(menuItemCopyCompleteRelativeFilePath);
		
		popupMenu.addSeparator();
		
		menuItemCopyFilePath = new JMenuItem("Copy file path(s)");
		menuItemCopyFilePath.addActionListener(this);
		popupMenu.add(menuItemCopyFilePath);
		
		menuItemCopyCompleteFilePath = new JMenuItem("Copy complete file path(s)");
		menuItemCopyCompleteFilePath.addActionListener(this);
		popupMenu.add(menuItemCopyCompleteFilePath);
		
		popupMenu.addSeparator();
		
		menuItemShowInSystemExplorer = new JMenuItem("Show in system explorer");
		menuItemShowInSystemExplorer.addActionListener(this);
		popupMenu.add(menuItemShowInSystemExplorer);
	}
	
	public void addRow(FileInformation fileInformation) {
		tableModel.addRow(fileInformation);
	}
	
	public void updateRow(int rowIndex, FileInformation fileInformation) {
		tableModel.updateRow(rowIndex, fileInformation);
	}
	
	public FileInformation deleteRow(int rowIndex) {
		return tableModel.deleteRow(rowIndex);
	}
	
	public void deleteRows() {
		tableModel.deleteRows();
	}
	
	public boolean contains(FileInformation fileInformation) {
		return tableModel.contains(fileInformation);
	}
	
	public FileInformation getFileInformation(int index) {
		return tableModel.getFileInformation(index);
	}
	
	public synchronized List<FileInformation> getSelectedFileInformation() {
		int[] selectedRows = getSelectedRows();
		
		List<FileInformation> selectedFileInformation = new ArrayList<FileInformation>(selectedRows.length);
		
		for (int i = 0; i < selectedRows.length; i++) {
			selectedFileInformation.add(getFileInformation(selectedRows[i]));
		}
		
		return selectedFileInformation;
	}
	
	public ListSelectionModel addListSelectionListener(ListSelectionListener listSelectionListener) {
		ListSelectionModel selectionModel = getSelectionModel();
		selectionModel.addListSelectionListener(listSelectionListener);
		
		return selectionModel;
	}
	
	private FileInformationTable getCurrentInstance() {
		return this;
	}
	
	@Override
	public void popupMenuWillBecomeVisible(PopupMenuEvent event) {
		if (getSelectedRowCount() < 2) {
			SwingUtilities.invokeLater(new Runnable() {
				
				@Override
				public void run() {
					int rowIndex = rowAtPoint(SwingUtilities.convertPoint(popupMenu, new Point(0, 0), getCurrentInstance()));
					
					if (rowIndex != -1) {
			        	setRowSelectionInterval(rowIndex, rowIndex);
			        }
				}
			});
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		Object source = event.getSource();
		
		List<FileInformation> selectedFileInformation = getSelectedFileInformation();
		
		StringBuilder clipboardContentBuilder = new StringBuilder(4096);
		
		for (int i = 0; i < selectedFileInformation.size(); i++) {
			FileInformation fileInformation = selectedFileInformation.get(i);
			
			if (clipboardContentBuilder.length() != 0) {
				clipboardContentBuilder.append('\n');
			}
			
			if (source.equals(menuItemCopyFileName)) {
				clipboardContentBuilder.append(fileInformation.getName());
			} else if (source.equals(menuItemCopyFileExtension)) {
				clipboardContentBuilder.append(fileInformation.getExtension());
			} else if (source.equals(menuItemCopyRelativeFilePath)) {
				clipboardContentBuilder.append(fileInformation.getRelativePath());
			} else if (source.equals(menuItemCopyCompleteRelativeFilePath)) {
				clipboardContentBuilder.append(fileInformation.getRelativePath());
				clipboardContentBuilder.append(fileInformation.getName());
			} else if (source.equals(menuItemCopyFilePath)) {
				clipboardContentBuilder.append(fileInformation.getPath().substring(0, fileInformation.getPath().lastIndexOf(File.separator)));
			} else if (source.equals(menuItemCopyCompleteFilePath)) {
				clipboardContentBuilder.append(fileInformation.getPath());
			} else if (source.equals(menuItemShowInSystemExplorer)) {
				PlatformDependentUtilities.showInSystemExplorer(fileInformation.getPath());
			}
		}
		
		if (clipboardContentBuilder.length() != 0) {
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(clipboardContentBuilder.toString()), null);
		}
	}
	
	@Override
	public void popupMenuWillBecomeInvisible(PopupMenuEvent event) { }
	
	@Override
	public void popupMenuCanceled(PopupMenuEvent event) { }
	
}