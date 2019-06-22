package com.bjitgroup.vautomation.ui.utilities;

import java.awt.Component;
import java.awt.Container;
import java.awt.Font;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.bjitgroup.vautomation.core.models.ProfileInformation;
import com.bjitgroup.vautomation.core.models.ServerDetails;
import com.bjitgroup.vautomation.ui.ProfileDialog;
import com.bjitgroup.vautomation.ui.RemoteServerDialog;

public final class UIUtilities {
	
	public static void changeExistingFontSizeAndStyle(
		int fontSize, int fontStyle, JComponent component) {
		Font existingFont = component.getFont();
		
		if (fontStyle == -1) {
			fontStyle = existingFont.getStyle();
		}
		
		component.setFont(new Font(existingFont.getFontName(), fontStyle, fontSize));
	}
	
	public static void revalidateAndRepaint(Container container) {
		do {
			container.revalidate();
			container.repaint();
		} while ((container = container.getParent()) != null);
	}
	
	public static void showSuccessMessageDialog(String message, Component parentComponent) {
		JOptionPane.showMessageDialog(
			parentComponent, message,
			"Success", JOptionPane.INFORMATION_MESSAGE
		);
	}
	
	public static void showErrorMessageDialog(String message, Component parentComponent) {
		JOptionPane.showMessageDialog(
			parentComponent, message,
			"Error", JOptionPane.ERROR_MESSAGE
		);
	}
	
	public static void setJTableColumnWidth(TableColumnModel columnModel, int tableWidth, double... percentages) {
	    double total = 0;
	    
	    for (int i = 0; i < columnModel.getColumnCount(); i++) {
	        total += percentages[i];
	    }
	    
	    for (int i = 0; i < columnModel.getColumnCount(); i++) {
	        TableColumn column = columnModel.getColumn(i);
	        
	        column.setPreferredWidth((int) (tableWidth * (percentages[i] / total)));
	    }
	}
	
	public static ProfileInformation showProfileDialog(String title,
		ProfileInformation profileInformation, Component parentComponent) {
		ProfileDialog profileDialog = new ProfileDialog(profileInformation);
		profileDialog.setTitle(title);
		profileDialog.setLocationRelativeTo(parentComponent);
		profileDialog.setModal(true);
		profileDialog.setVisible(true);
		
		return profileDialog.getProfileInformation();
	}
	
	public static ServerDetails showRemoteServerDialog(String title,
		ServerDetails serverDetails, Component parentComponent) {
		RemoteServerDialog serverDialog = new RemoteServerDialog(serverDetails);
		serverDialog.setTitle(title);
		serverDialog.setLocationRelativeTo(parentComponent);
		serverDialog.setModal(true);
		serverDialog.setVisible(true);
		
		return serverDialog.getServerDetails();
	}
	
}