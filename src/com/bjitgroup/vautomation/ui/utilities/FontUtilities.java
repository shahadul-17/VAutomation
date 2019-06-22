package com.bjitgroup.vautomation.ui.utilities;

import java.awt.Font;

public final class FontUtilities {
	
	private static final int defaultSize = 13;
	
	public static final int PLAIN = 0;
	public static final int BOLD = 1;
	public static final int ITALIC = 2;
	public static final int BOLD_ITALIC = 3;
	
	private static Font[] segoeUI;
	
	public static Font getDefaultFont() {
		return getFont(defaultSize, PLAIN);
	}
	
	public static Font getFont(int size, int style) {
		if (segoeUI == null) {
			segoeUI = new Font[4];
		}
		
		if (segoeUI[style] == null ) {
			try {
				segoeUI[style] = Font.createFont(
					Font.TRUETYPE_FONT,
					FontUtilities.class.getResourceAsStream("/fonts/segoe-ui/segoe-ui-" + style + ".ttf")
				);
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}
		
		return segoeUI[style].deriveFont(segoeUI[style].getStyle(), size);
	}
	
}