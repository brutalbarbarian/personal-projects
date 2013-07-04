package com.lwan.eaproj.app;

import com.lwan.javafx.controls.other.BOGrid;

public class EAConstants {
	public static final String[] CONTACT_FIELDS = {BOGrid.PREFIX_CALCULATED + "Address", 
		"City", "Country", "PostCode", "Phone", "Mobile", "Fax", "Site"};
	public static final String[] CONTACT_DISPLAY_STRINGS = {"Address",
		"City", "Country", "PostCode", "Phone", "Mobile", "Fax", "Site"};
	
	public static final String[] CDT_SOURCE_TYPES = {"<Unknown>", "School", "Customer", "Company"};
	
	public static final int CDT_SOURCE_TYPE_MISC = 0;	// ???
	public static final int CDT_SOURCE_TYPE_SCHOOL = 1;
	public static final int CDT_SOURCE_TYPE_CUSTOMER = 2;
	public static final int CDT_SOURCE_TYPE_COMPANY = 3;
	
	public static final int CTY_DEFAULT = 0;
	public static final int CTY_STUDENT = 1;
	
	public static final int FADE_DURATION = 500;
	
	public static final int INI_WIDTH = 350;
	public static final int INI_HEIGHT = 200;
	public static final int MAIN_WIDTH = 1000;
	public static final int MAIN_HEIGHT = 700;
}
