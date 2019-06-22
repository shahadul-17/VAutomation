package com.bjitgroup.vautomation;

import java.io.File;
import java.util.Enumeration;

import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

import com.bjitgroup.vautomation.core.utilities.PlatformDependentUtilities;
import com.bjitgroup.vautomation.ui.Frame;
import com.bjitgroup.vautomation.ui.utilities.FontUtilities;

public final class Application {
	
	// a flag variable which indicates if this application is running...
	public static volatile boolean isRunning;
	
	// a mutex for thread synchronization...
	public static final Object MUTEX = new Object();
	
	// title of this application...
	public static final String TITLE = "VAutomation v0.0.1 (Alpha)";
	
	// data folder path of this application...
	public static final String DATA_DIRECTORY_PATH = PlatformDependentUtilities.getWorkingDirectoryPath() + File.separator + "data" + File.separator;
	
	// entry point of this application...
	public static void main(String[] args) {
		isRunning = true;
		
		Enumeration<Object> keys = UIManager.getDefaults().keys();
		
		while (keys.hasMoreElements()) {
			Object key = keys.nextElement();
			Object value = UIManager.getDefaults().get(key);
			
			if (value instanceof FontUIResource) {
				UIManager.getDefaults().put(key, new FontUIResource(FontUtilities.getDefaultFont()));
			}
		}
		
		keys = UIManager.getLookAndFeelDefaults().keys();
		
		while (keys.hasMoreElements()) {
			Object key = keys.nextElement();
			Object value = UIManager.getLookAndFeelDefaults().get(key);
			
			if (value instanceof FontUIResource) {
				UIManager.getLookAndFeelDefaults().put(key, new FontUIResource(FontUtilities.getDefaultFont()));
			}
		}
		
		Frame frame = new Frame();
		frame.setVisible(true);
	}
	
}