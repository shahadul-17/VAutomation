package com.bjitgroup.vautomation.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.bjitgroup.vautomation.core.models.ProfileInformation;
import com.bjitgroup.vautomation.ui.utilities.UIUtilities;

public class ProfileDialog extends JDialog implements ActionListener {
	
	private static final long serialVersionUID = -614136600149650464L;
	
	private JLabel labelTitle;
	
	private JTextField profileName;
	private JTextField projectDirectory;
	
	private JButton buttonBrowse;
	private JButton buttonFinish;
	
	private ProfileInformation profileInformation;
	
	public ProfileDialog() {
		initialize();
	}
	
	public ProfileDialog(ProfileInformation profileInformation) {
		initialize();
		
		if (profileInformation == null) {
			return;
		}
		
		this.profileInformation = profileInformation;
		
		profileName.setText(profileInformation.getName());
		projectDirectory.setText(profileInformation.getProjectDirectory());
	}
	
	private void initialize() {
		setResizable(false);
		setSize(450, 240);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		
		JPanel contentPane = new JPanel();
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		labelTitle = new JLabel(getTitle());
		UIUtilities.changeExistingFontSizeAndStyle(16, Font.PLAIN, labelTitle);
		labelTitle.setBounds(10, 11, 250, 25);
		contentPane.add(labelTitle);
		
		JLabel labelProfileName = new JLabel("Profile name");
		UIUtilities.changeExistingFontSizeAndStyle(12, Font.PLAIN, labelProfileName);
		labelProfileName.setBounds(33, 63, 100, 25);
		contentPane.add(labelProfileName);
		
		profileName = new JTextField("VAutomation Profile 1");
		UIUtilities.changeExistingFontSizeAndStyle(12, Font.PLAIN, profileName);
		profileName.setBounds(143, 63, 291, 25);
		contentPane.add(profileName);
		
		JLabel labelProjectDirectory = new JLabel("Project directory");
		UIUtilities.changeExistingFontSizeAndStyle(12, Font.PLAIN, labelProjectDirectory);
		labelProjectDirectory.setBounds(33, 99, 100, 25);
		contentPane.add(labelProjectDirectory);
		
		projectDirectory = new JTextField();
		projectDirectory.setBackground(Color.WHITE);
		projectDirectory.setEditable(false);
		projectDirectory.setFont(new Font("Tahoma", Font.PLAIN, 13));
		projectDirectory.setBounds(143, 99, 201, 25);
		contentPane.add(projectDirectory);
		
		buttonBrowse = new JButton("Browse");
		buttonBrowse.setBounds(354, 99, 80, 25);
		buttonBrowse.addActionListener(this);
		contentPane.add(buttonBrowse);
		
		buttonFinish = new JButton("Finish");
		buttonFinish.setBounds(182, 160, 80, 25);
		buttonFinish.addActionListener(this);
		contentPane.add(buttonFinish);
	}
	
	public ProfileInformation getProfileInformation() {
		return profileInformation;
	}
	
	@Override
	public void setTitle(String title) {
		labelTitle.setText(title);
		
		if (profileInformation != null) {
			int indexOfHyphen;
			
			if ((indexOfHyphen = title.indexOf(" - ")) != -1) {
				title = title.substring(0, indexOfHyphen);
			}
			
			title += " - " + profileInformation.getName();
		}
		
		super.setTitle(title);
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getSource().equals(buttonBrowse)) {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			fileChooser.setAcceptAllFileFilterUsed(false);
			
			if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				projectDirectory.setText(fileChooser.getSelectedFile().getAbsolutePath());
			}
		} else if (event.getSource().equals(buttonFinish)) {
			String profileName = this.profileName.getText().trim();
			String projectDirectory = this.projectDirectory.getText().trim();
			
			if (profileName.isEmpty() ||
				projectDirectory.isEmpty()) {
				profileInformation = null;
				
				UIUtilities.showErrorMessageDialog("Please make sure to enter 'Profile name' and 'Project directory'.", this);
			} else {
				profileInformation = new ProfileInformation();
				profileInformation.setName(profileName);
				profileInformation.setProjectDirectory(projectDirectory);
				
				if (profileInformation.save()) {
					profileInformation.deleteAllFilePaths();
					dispose();
				} else {
					profileInformation = null;
					
					UIUtilities.showErrorMessageDialog("An error occured while creating profile.", this);
				}
			}
		}
	}
	
}