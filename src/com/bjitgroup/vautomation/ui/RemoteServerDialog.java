package com.bjitgroup.vautomation.ui;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.bjitgroup.vautomation.core.models.ServerDetails;
import com.bjitgroup.vautomation.ui.utilities.UIUtilities;

public class RemoteServerDialog extends JDialog implements ActionListener {
	
	private static final long serialVersionUID = -614136600149650464L;
	
	private JLabel labelTitle;
	
	private JTextField serverAddress;
	private JTextField userName;
	
	private JPasswordField password;
	
	private JButton buttonFinish;
	
	private ServerDetails serverDetails;
	
	public RemoteServerDialog() {
		initialize();
	}
	
	public RemoteServerDialog(ServerDetails serverDetails) {
		initialize();
		
		if (serverDetails == null) {
			return;
		}
		
		this.serverDetails = serverDetails;
		
		serverAddress.setText(serverDetails.getAddress());
		userName.setText(serverDetails.getUserName());
		password.setText(new String(serverDetails.getPassword()));
	}
	
	private void initialize() {
		setResizable(false);
		setSize(450, 280);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		
		JPanel contentPane = new JPanel();
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		labelTitle = new JLabel(getTitle());
		UIUtilities.changeExistingFontSizeAndStyle(16, Font.PLAIN, labelTitle);
		labelTitle.setBounds(10, 11, 250, 25);
		contentPane.add(labelTitle);
		
		JLabel labelServerAddress = new JLabel("Server address");
		UIUtilities.changeExistingFontSizeAndStyle(12, Font.PLAIN, labelServerAddress);
		labelServerAddress.setBounds(33, 63, 100, 25);
		contentPane.add(labelServerAddress);
		
		serverAddress = new JTextField();
		serverAddress.setFont(new Font("Tahoma", Font.PLAIN, 13));
		serverAddress.setBounds(143, 63, 291, 25);
		contentPane.add(serverAddress);
		
		JLabel labelUsername = new JLabel("Username");
		UIUtilities.changeExistingFontSizeAndStyle(12, Font.PLAIN, labelUsername);
		labelUsername.setBounds(33, 99, 100, 25);
		contentPane.add(labelUsername);
		
		userName = new JTextField();
		userName.setFont(new Font("Tahoma", Font.PLAIN, 13));
		userName.setColumns(10);
		userName.setBounds(143, 99, 291, 25);
		contentPane.add(userName);
		
		JLabel labelPassword = new JLabel("Password");
		UIUtilities.changeExistingFontSizeAndStyle(12, Font.PLAIN, labelPassword);
		labelPassword.setBounds(33, 135, 100, 25);
		contentPane.add(labelPassword);
		
		password = new JPasswordField();
		password.setFont(new Font("Tahoma", Font.PLAIN, 13));
		password.setBounds(143, 135, 291, 25);
		contentPane.add(password);
		
		buttonFinish = new JButton("Finish");
		buttonFinish.setBounds(182, 200, 80, 25);
		buttonFinish.addActionListener(this);
		contentPane.add(buttonFinish);
	}
	
	public ServerDetails getServerDetails() {
		return serverDetails;
	}
	
	@Override
	public void setTitle(String title) {
		labelTitle.setText(title);
		
		super.setTitle(title);
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getSource().equals(buttonFinish)) {
			String serverAddress = this.serverAddress.getText().trim();
			String userName = this.userName.getText().trim();
			
			if (serverAddress.isEmpty() ||
				userName.isEmpty()) {
				serverDetails = null;
				
				UIUtilities.showErrorMessageDialog("Please make sure to enter correct values for all the specified fields.", this);
			} else {
				serverDetails = new ServerDetails();
				serverDetails.setAddress(serverAddress);
				serverDetails.setUserName(userName);
				serverDetails.setPassword(password.getPassword());
				
				if (serverDetails.save()) {
					dispose();
				} else {
					serverDetails = null;
					
					UIUtilities.showErrorMessageDialog("An error occured while saving server information.", this);
				}
			}
		}
	}
	
}