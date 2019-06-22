package com.bjitgroup.vautomation.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Event;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableColumnModel;

import com.bjitgroup.vautomation.Application;
import com.bjitgroup.vautomation.core.models.ProfileInformation;
import com.bjitgroup.vautomation.core.models.filesystem.FileInformation;
import com.bjitgroup.vautomation.core.services.FTPClient;
import com.bjitgroup.vautomation.core.services.ZipArchiver;
import com.bjitgroup.vautomation.core.services.filesystem.FileSystemListener;
import com.bjitgroup.vautomation.core.services.filesystem.FileSystemTracker;
import com.bjitgroup.vautomation.core.utilities.PlatformDependentUtilities;
import com.bjitgroup.vautomation.ui.utilities.UIUtilities;

public class Frame extends JFrame implements FileSystemListener, ListSelectionListener,
											 ComponentListener, ActionListener, WindowListener {
	
	private static final long serialVersionUID = -2059080585182180950L;
	
	private JMenuItem menuItemCreateNewProfile;
	private JMenuItem menuItemOpenExistingProfile;
	private JMenuItem menuItemEditProfile;
	private JMenuItem menuItemAddNewServer;
	private JMenuItem menuItemExit;
	private JMenuItem menuItemAbout;
	
	private JPanel contentPane;
	private JSplitPane panelCenter;
	private JPanel panelTrackedFiles;
	private JPanel panelAllModifiedFiles;
	private JPanel panelControlsBottomLeft;
	
	private JScrollPane scrollPaneTrackedFiles;
	private JScrollPane scrollPaneAllModifiedFiles;
	
	private JTextField textFieldProjectDirectory;
	
	private JLabel labelTrackedFiles;
	private JLabel labelAllModifiedFiles;
	private JLabel labelStatus;
	
	private ListSelectionModel trackedFileSelectionModel;
	private ListSelectionModel allModifiedFileSelectionModel;
	
	private FileInformationTable activeTable;
	private FileInformationTable tableTrackedFiles;
	private FileInformationTable tableAllModifiedFiles;
	
	private JButton buttonBrowse;
	private JButton buttonAddToTrackedFiles;
	private JButton buttonRemoveFromTrackedFiles;
	private JButton buttonCreateArchive;
	private JButton buttonUpload;
	
	private JProgressBar progressBarStatus;
	
	private ProfileInformation currentProfileInformation;
	private FileSystemTracker fileSystemTracker;
	private JPanel panelControlsBottomCenter;
	private JButton button;
	private JButton button_1;
	private JButton button_2;
	
	public Frame() {
		initialize();
	}
	
	private void initialize() {
		JMenuBar menuBar = new JMenuBar();
		
		JMenu fileMenu = new JMenu("File");
		
		menuItemCreateNewProfile = new JMenuItem("Create new profile");
		menuItemCreateNewProfile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, Event.CTRL_MASK));
		menuItemCreateNewProfile.addActionListener(this);
		fileMenu.add(menuItemCreateNewProfile);
		
		menuItemOpenExistingProfile = new JMenuItem("Open existing profile");
		menuItemOpenExistingProfile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, Event.CTRL_MASK));
		menuItemOpenExistingProfile.addActionListener(this);
		fileMenu.add(menuItemOpenExistingProfile);
		
		menuItemEditProfile = new JMenuItem("Edit profile");
		menuItemEditProfile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, Event.CTRL_MASK));
		menuItemEditProfile.addActionListener(this);
		fileMenu.add(menuItemEditProfile);
		
		fileMenu.addSeparator();
		
		menuItemAddNewServer = new JMenuItem("Add new server");
		menuItemAddNewServer.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, Event.CTRL_MASK));
		menuItemAddNewServer.addActionListener(this);
		fileMenu.add(menuItemAddNewServer);
		
		fileMenu.addSeparator();
		
		menuItemExit = new JMenuItem("Exit");
		menuItemExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, Event.ALT_MASK));
		menuItemExit.addActionListener(this);
		fileMenu.add(menuItemExit);
		
		menuBar.add(fileMenu);
		
		JMenu helpMenu = new JMenu("Help");
		
		menuItemAbout = new JMenuItem("About");
		menuItemAbout.addActionListener(this);
		helpMenu.add(menuItemAbout);
		
		menuBar.add(helpMenu);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout());
		
		setTitle(Application.TITLE);
		setSize(850, 625);
		setMinimumSize(getSize());
		setLocationRelativeTo(null);
		setJMenuBar(menuBar);
		setContentPane(contentPane);
		addWindowListener(this);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel panelControls = new JPanel();
		panelControls.setLayout(new GridLayout(2, 1));
		contentPane.add(panelControls, BorderLayout.NORTH);
		
		JPanel panelControlsTop = new JPanel();
		panelControls.add(panelControlsTop);
		
		JLabel labelProjectDirectory = new JLabel("Project directory");
		UIUtilities.changeExistingFontSizeAndStyle(14, -1, labelProjectDirectory);
		panelControlsTop.add(labelProjectDirectory);
		
		textFieldProjectDirectory = new JTextField();
		textFieldProjectDirectory.setColumns(45);
		textFieldProjectDirectory.setEditable(false);
		textFieldProjectDirectory.setBackground(Color.WHITE);
		UIUtilities.changeExistingFontSizeAndStyle(14, -1, textFieldProjectDirectory);
		panelControlsTop.add(textFieldProjectDirectory);
		
		buttonBrowse = new JButton();
		buttonBrowse.setEnabled(false);
		buttonBrowse.setIcon(ImageIcons.browse);
		buttonBrowse.addActionListener(this);
		panelControlsTop.add(buttonBrowse);
		
		JPanel panelControlsBottom = new JPanel(new BorderLayout(5, 5));
		panelControls.add(panelControlsBottom);
		
		panelControlsBottomLeft = new JPanel();
		panelControlsBottom.add(panelControlsBottomLeft, BorderLayout.WEST);
		
		buttonAddToTrackedFiles = new JButton();
		buttonAddToTrackedFiles.setToolTipText("Add file(s) to tracked list");
		buttonAddToTrackedFiles.setEnabled(false);
		buttonAddToTrackedFiles.setIcon(ImageIcons.add);
		buttonAddToTrackedFiles.addActionListener(this);
		panelControlsBottomLeft.add(buttonAddToTrackedFiles);
		
		buttonRemoveFromTrackedFiles = new JButton();
		buttonRemoveFromTrackedFiles.setToolTipText("Remove file(s) from tracked list");
		buttonRemoveFromTrackedFiles.setEnabled(false);
		buttonRemoveFromTrackedFiles.setIcon(ImageIcons.remove);
		buttonRemoveFromTrackedFiles.addActionListener(this);
		panelControlsBottomLeft.add(buttonRemoveFromTrackedFiles);
		
		buttonCreateArchive = new JButton();
		buttonCreateArchive.setToolTipText("Create ZIP archive");
		buttonCreateArchive.setEnabled(false);
		buttonCreateArchive.setIcon(ImageIcons.zip);
		buttonCreateArchive.addActionListener(this);
		panelControlsBottomLeft.add(buttonCreateArchive);
		
		buttonUpload = new JButton();
		buttonUpload.setToolTipText("Upload file(s) to remote server");
		buttonUpload.setEnabled(false);
		buttonUpload.setIcon(ImageIcons.upload);
		buttonUpload.addActionListener(this);
		panelControlsBottomLeft.add(buttonUpload);
		
		panelControlsBottomCenter = new JPanel();
		FlowLayout fl_panelControlsBottomCenter = (FlowLayout) panelControlsBottomCenter.getLayout();
		fl_panelControlsBottomCenter.setAlignment(FlowLayout.RIGHT);
		panelControlsBottom.add(panelControlsBottomCenter, BorderLayout.CENTER);
		
		button = new JButton();
		button.setToolTipText("Create ZIP archive");
		button.setEnabled(false);
		button.setIcon(ImageIcons.start);
		panelControlsBottomCenter.add(button);
		
		button_1 = new JButton();
		button_1.setToolTipText("Upload file(s) to remote server");
		button_1.setEnabled(false);
		button_1.setIcon(ImageIcons.stop);
		panelControlsBottomCenter.add(button_1);
		
		button_2 = new JButton();
		button_2.setToolTipText("Upload file(s) to remote server");
		button_2.setEnabled(false);
		button_2.setIcon(ImageIcons.restart);
		panelControlsBottomCenter.add(button_2);
		
		panelCenter = new JSplitPane();
		panelCenter.setBorder(BorderFactory.createEmptyBorder());
		panelCenter.setOneTouchExpandable(true);
		panelCenter.setOrientation(JSplitPane.VERTICAL_SPLIT);
		panelCenter.setResizeWeight(0.5);
		contentPane.add(panelCenter, BorderLayout.CENTER);
		
		panelTrackedFiles = new JPanel();
		panelTrackedFiles.setLayout(new BorderLayout());
		panelCenter.add(panelTrackedFiles, JSplitPane.TOP);
		
		labelTrackedFiles = new JLabel("Tracked files (0)");
		UIUtilities.changeExistingFontSizeAndStyle(16, -1, labelTrackedFiles);
		panelTrackedFiles.add(labelTrackedFiles, BorderLayout.NORTH);
		
		scrollPaneTrackedFiles = new JScrollPane();
		scrollPaneTrackedFiles.setBorder(BorderFactory.createEmptyBorder());
		scrollPaneTrackedFiles.getViewport().setBackground(Color.WHITE);
		scrollPaneTrackedFiles.addComponentListener(this);
		panelTrackedFiles.add(scrollPaneTrackedFiles, BorderLayout.CENTER);
		
		tableTrackedFiles = new FileInformationTable();
		trackedFileSelectionModel = tableTrackedFiles.addListSelectionListener(this);
		scrollPaneTrackedFiles.setViewportView(tableTrackedFiles);
		
		panelAllModifiedFiles = new JPanel();
		panelCenter.add(panelAllModifiedFiles, JSplitPane.BOTTOM);
		panelAllModifiedFiles.setLayout(new BorderLayout(0, 0));
		
		labelAllModifiedFiles = new JLabel("All modified files (0)");
		UIUtilities.changeExistingFontSizeAndStyle(16, -1, labelAllModifiedFiles);
		panelAllModifiedFiles.add(labelAllModifiedFiles, BorderLayout.NORTH);
		
		scrollPaneAllModifiedFiles = new JScrollPane();
		scrollPaneAllModifiedFiles.setBorder(BorderFactory.createEmptyBorder());
		scrollPaneAllModifiedFiles.getViewport().setBackground(Color.WHITE);
		scrollPaneAllModifiedFiles.addComponentListener(this);
		panelAllModifiedFiles.add(scrollPaneAllModifiedFiles, BorderLayout.CENTER);
		
		tableAllModifiedFiles = new FileInformationTable();
		allModifiedFileSelectionModel = tableAllModifiedFiles.addListSelectionListener(this);
		scrollPaneAllModifiedFiles.setViewportView(tableAllModifiedFiles);
		
		JPanel panelStatus = new JPanel(new BorderLayout());
		panelStatus.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
		contentPane.add(panelStatus, BorderLayout.SOUTH);
		
		labelStatus = new JLabel("Ready");
		UIUtilities.changeExistingFontSizeAndStyle(13, -1, labelStatus);
		panelStatus.add(labelStatus, BorderLayout.WEST);
		
		progressBarStatus = new JProgressBar();
		UIUtilities.changeExistingFontSizeAndStyle(13, -1, progressBarStatus);
		progressBarStatus.setStringPainted(true);
		panelStatus.add(progressBarStatus, BorderLayout.EAST);
	}
	
	@Override
	public void trackedFilesChanged(List<FileInformation> fileInformationList) {
		if (fileInformationList.size() == 0) {
			return;
		}
		
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				synchronized (Application.MUTEX) {
					labelStatus.setText("Loading changed tracked files...");
					tableTrackedFiles.deleteRows();
					
					for (int i = 0; i < fileInformationList.size(); i++) {
						tableTrackedFiles.addRow(fileInformationList.get(i));
						progressBarStatus.setValue(((i + 1) / fileInformationList.size()) * 100);
					}
					
					labelTrackedFiles.setText("Tracked files (" + fileInformationList.size() + ")");
					
					UIUtilities.revalidateAndRepaint(tableTrackedFiles);
					labelStatus.setText("Successfully loaded changed tracked files");
				}
			}
		});
	}
	
	@Override
	public void filesChanged(List<FileInformation> fileInformationList) {
		if (fileInformationList.size() == 0) {
			return;
		}
		
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				synchronized (Application.MUTEX) {
					labelStatus.setText("Loading all changed files...");
					tableAllModifiedFiles.deleteRows();
					
					for (int i = 0; i < fileInformationList.size(); i++) {
						tableAllModifiedFiles.addRow(fileInformationList.get(i));
						
						progressBarStatus.setValue(((i + 1) / fileInformationList.size()) * 100);
					}
					
					labelAllModifiedFiles.setText("All modified files (" + fileInformationList.size() + ")");
					
					UIUtilities.revalidateAndRepaint(tableAllModifiedFiles);
					labelStatus.setText("Successfully loaded all changed files");
				}
			}
		});
	}
	
	@Override
	public void valueChanged(ListSelectionEvent event) {
		synchronized (Application.MUTEX) {
			ListSelectionModel selectionModel = (ListSelectionModel) event.getSource();
			
			boolean selectionNotEmpty = !selectionModel.isSelectionEmpty();
			
			buttonAddToTrackedFiles.setEnabled(selectionNotEmpty);
			buttonRemoveFromTrackedFiles.setEnabled(selectionNotEmpty);
			buttonCreateArchive.setEnabled(selectionNotEmpty);
			buttonUpload.setEnabled(selectionNotEmpty);
			
			if (selectionNotEmpty) {
				boolean flag = false;
				
				if (selectionModel.equals(trackedFileSelectionModel)) {
					activeTable = tableTrackedFiles;
					
					tableAllModifiedFiles.clearSelection();
				} else if (selectionModel.equals(allModifiedFileSelectionModel)) {
					flag = true;
					activeTable = tableAllModifiedFiles;
					
					tableTrackedFiles.clearSelection();
				}
				
				buttonAddToTrackedFiles.setEnabled(flag);
				buttonRemoveFromTrackedFiles.setEnabled(!flag);
			} else {
				activeTable = null;
			}
		}
	}
	
	@Override
	public void componentResized(ComponentEvent event) {
		Object source = event.getSource();
		
		if (source instanceof JScrollPane) {
			JScrollPane scrollPane = (JScrollPane) source;
			JTable table = (JTable) scrollPane.getViewport().getComponent(0);
			TableColumnModel columnModel = table.getColumnModel();
			
			UIUtilities.setJTableColumnWidth(columnModel, scrollPane.getWidth(), 5, 30, 30, 10, 25);
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		Object source = event.getSource();
		
		if (source.equals(menuItemCreateNewProfile)) {
			ProfileInformation profileInformation =
				UIUtilities.showProfileDialog(menuItemCreateNewProfile.getText(), null, this);
			
			if (profileInformation == null) {
				return;
			}
			
			currentProfileInformation = profileInformation;
			
			if (fileSystemTracker != null) {
				fileSystemTracker.close();
			}
			
			try {
				fileSystemTracker = new FileSystemTracker(currentProfileInformation);
				fileSystemTracker.addFileSystemListener(this);
				fileSystemTracker.start();
				
				int index = -1;
				
				if ((index = getTitle().indexOf(" - ")) != -1) {
					setTitle(getTitle().substring(0, index));
				}
				
				setTitle(getTitle() + " - " + currentProfileInformation.getName());
				
				textFieldProjectDirectory.setText(currentProfileInformation.getProjectDirectory());
				buttonBrowse.setEnabled(true);
			} catch (Exception exception) {
				exception.printStackTrace();
				
				UIUtilities.showErrorMessageDialog("An error occured while trying to monitor file system.", this);
			}
		} else if (source.equals(menuItemOpenExistingProfile)) {
			JFileChooser fileChooser = new JFileChooser(ProfileInformation.getDirectoryPath());
			fileChooser.setFileFilter(new FileNameExtensionFilter("VAutomation profile", ProfileInformation.getFileExtension()));
			fileChooser.setMultiSelectionEnabled(false);
			
			if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				ProfileInformation profileInformation = ProfileInformation.load(fileChooser.getSelectedFile());
				
				if (profileInformation == null) {
					return;
				}
				
				currentProfileInformation = profileInformation;
				
				/*ArrayList<FileInformation> trackedFileInformationList = currentProfileInformation.getTrackedFileInformationList();
				
				synchronized (Application.MUTEX) {
					FileInformationTableModel tableModel = (FileInformationTableModel) tableTrackedFiles.getModel();
					
					for (FileInformation fileInformation : trackedFileInformationList) {
						tableModel.addRow(fileInformation);
					}
				}
				
				ArrayList<FileInformation> allModifiedFileInformationList = currentProfileInformation.getAllModifiedFileInformationList();
				
				synchronized (Application.MUTEX) {
					FileInformationTableModel tableModel = (FileInformationTableModel) tableAllModifiedFiles.getModel();
					
					for (FileInformation fileInformation : allModifiedFileInformationList) {
						tableModel.addRow(fileInformation);
					}
				}*/
				
				if (fileSystemTracker != null) {
					fileSystemTracker.close();
				}
				
				try {
					fileSystemTracker = new FileSystemTracker(currentProfileInformation);
					fileSystemTracker.addFileSystemListener(this);
					fileSystemTracker.start();
					
					int index = -1;
					
					if ((index = getTitle().indexOf(" - ")) != -1) {
						setTitle(getTitle().substring(0, index));
					}
					
					setTitle(getTitle() + " - " + currentProfileInformation.getName());
					
					textFieldProjectDirectory.setText(currentProfileInformation.getProjectDirectory());
					buttonBrowse.setEnabled(true);
				} catch (Exception exception) {
					exception.printStackTrace();
					
					UIUtilities.showErrorMessageDialog("An error occured while trying to monitor file system.", this);
				}
			}
		} else if (source.equals(menuItemEditProfile)) {
			/*ProfileInformation profileInformation = UIUtilities.showProfileDialog(
				menuItemEditProfile.getText(),
				currentProfileInformation,
				this
			);
			
			if (profileInformation == null) {
				return;
			}
			
			currentProfileInformation = profileInformation;
			*/
			
			UIUtilities.showErrorMessageDialog("This functionality is not available yet.", this);
		} else if (source.equals(menuItemAddNewServer)) {
			// RemoteServerInformation remoteServerInformation = 
			UIUtilities.showRemoteServerDialog(
				menuItemAddNewServer.getText(), null, this
			);
			
			/*if (remoteServerInformation == null) {
				return;
			}
			
			this.currentRemoteServerInformation = remoteServerInformation;*/
		} else if (source.equals(menuItemExit)) {
			Application.isRunning = false;
			
			if (fileSystemTracker != null) {
				fileSystemTracker.close();
			}
			
			dispose();
			System.exit(0);
		} else if (source.equals(menuItemAbout)) {
			UIUtilities.showErrorMessageDialog("This functionality is not available yet.", this);
		} else if (currentProfileInformation != null) {
			if (source.equals(buttonBrowse)) {
				try {
					Desktop.getDesktop().open(new File(currentProfileInformation.getProjectDirectory()));
				} catch (Exception exception) {
					exception.printStackTrace();
				}
			} else if (source.equals(buttonAddToTrackedFiles)) {
				synchronized (Application.MUTEX) {
					int[] selectedRows = tableAllModifiedFiles.getSelectedRows();
					
					for (int i = 0; i < selectedRows.length; i++) {
						FileInformation fileInformation = tableAllModifiedFiles.getFileInformation(selectedRows[i]);
						
						if (!tableTrackedFiles.contains(fileInformation)) {
							currentProfileInformation.addToTrackedFileInformationList(fileInformation);
							tableTrackedFiles.addRow(fileInformation);
						}
						
						// tableAllModifiedFiles.getSelectionModel().addSelectionInterval(selectedRows[i], selectedRows[i]);
					}
					
					currentProfileInformation.save();
					labelTrackedFiles.setText("Tracked files (" + tableTrackedFiles.getRowCount() + ")");
				}
			} else if (source.equals(buttonRemoveFromTrackedFiles)) {
				synchronized (Application.MUTEX) {
					int[] selectedRows = tableTrackedFiles.getSelectedRows();
					
					for (int i = selectedRows.length - 1; i > -1; i--) {
						FileInformation fileInformation = tableTrackedFiles.deleteRow(selectedRows[i]);
						
						if (fileInformation != null) {
							currentProfileInformation.removeFromTrackedFileInformationList(fileInformation);
						}
					}
					
					currentProfileInformation.save();
					labelTrackedFiles.setText("Tracked files (" + tableTrackedFiles.getRowCount() + ")");
				}
			} else {
				synchronized (Application.MUTEX) {
					if (activeTable == null) {
						return;
					}
					
					List<FileInformation> selectedFileInformationList = activeTable.getSelectedFileInformation();
					
					labelStatus.setText("Please wait...");
					progressBarStatus.setIndeterminate(true);
					
					if (source.equals(buttonCreateArchive)) {
						ZipArchiver zipArchiver = new ZipArchiver(
							currentProfileInformation.getName(),
							selectedFileInformationList
						);
						
						if (zipArchiver.create()) {
							labelStatus.setText("Successfully created the ZIP archive");
							progressBarStatus.setValue(100);
							
							PlatformDependentUtilities.showInSystemExplorer(zipArchiver.getPath());
						} else {
							labelStatus.setText("An error occurred while creating the ZIP archive");
							progressBarStatus.setValue(0);
						}
						
						progressBarStatus.setIndeterminate(false);
					} else if (source.equals(buttonUpload)) {
						FTPClient ftpClient;
						
						try {
							ftpClient = new FTPClient(currentProfileInformation.getServerDetails());
						} catch (Exception exception) {
							labelStatus.setText(exception.getMessage());
							
							return;
						}
						
						ftpClient.upload(selectedFileInformationList);
						ftpClient.disconnect();
						
						// selectedFileInformationList.get(selectedFileInformationList.size() - 1).setUploaded(true);
						
						// SERIOUS OPTIMIZATION REQUIRED...
						for (int i = 0; i < selectedFileInformationList.size(); i++) {
							FileInformation selectedFileInformation = selectedFileInformationList.get(i);
							
							if (selectedFileInformation.isUploaded()) {
								for (int j = 0; j < tableTrackedFiles.getRowCount(); j++) {
									FileInformation trackedFileInformation = tableTrackedFiles.getFileInformation(j);
									
									if (selectedFileInformation.equals(trackedFileInformation)) {
										tableTrackedFiles.updateRow(j, selectedFileInformation);
										
										break;
									}
								}
								
								for (int j = 0; j < tableAllModifiedFiles.getRowCount(); j++) {
									FileInformation modifiedFileInformation = tableAllModifiedFiles.getFileInformation(j);
									
									if (selectedFileInformation.equals(modifiedFileInformation )) {
										tableAllModifiedFiles.updateRow(j, selectedFileInformation);
										
										break;
									}
								}
							}
						}
						
						labelStatus.setText("Operation completed (some files might not have uploaded due to unavailable mapping)");
					}
				}
			}
		}
	}
	
	@Override
	public void windowClosing(WindowEvent event) {
		Application.isRunning = false;
		
		if (fileSystemTracker != null) {
			fileSystemTracker.close();
		}
		
		dispose();
	}
	
	@Override
	public void windowActivated(WindowEvent event) { }
	
	@Override
	public void windowClosed(WindowEvent event) { }
	
	@Override
	public void windowDeactivated(WindowEvent event) { }
	
	@Override
	public void windowDeiconified(WindowEvent event) { }
	
	@Override
	public void windowIconified(WindowEvent event) { }
	
	@Override
	public void windowOpened(WindowEvent event) { }
	
	@Override
	public void componentMoved(ComponentEvent event) { }

	@Override
	public void componentShown(ComponentEvent event) { }

	@Override
	public void componentHidden(ComponentEvent event) { }
	
}