package com.bjitgroup.vautomation.core.utilities;

import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import com.bjitgroup.vautomation.core.models.filesystem.FileInformation;

public final class StringUtilities {
	
	private static final String[] sizeUnits = {
		"B", "kB", "MB", "GB", "TB"
	};
	
	public static String getFormattedSize(long size) {
		if (size <= 0L) {
			return size + " " + sizeUnits[0];
		}
		
		int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
		
		return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + sizeUnits[digitGroups];
	}
	
	public static String getFormattedDate(long date) {
		DateFormatSymbols dateFormatSymbols = new DateFormatSymbols(Locale.US);
		dateFormatSymbols.setAmPmStrings(new String[] { "AM", "PM" });
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss a dd-MMM-yyyy");
		dateFormat.setDateFormatSymbols(dateFormatSymbols);
		
		return dateFormat.format(date);
	}
	
	public static String getFormattedFileInformationList(List<FileInformation> fileInformationList) {
		StringBuilder fileInformationListBuilder = new StringBuilder();
		
		for (int i = 0; i < fileInformationList.size(); i++) {
			FileInformation fileInformation = fileInformationList.get(i);
			
			fileInformationListBuilder.append(fileInformation.toStorableString());
			
			if (i != fileInformationList.size() - 1) {
				fileInformationListBuilder.append("::::");
			}
		}
		
		return fileInformationListBuilder.toString();
	}
	
}