package com.lwan.eaproj.app;

public class EAUtils {
	
	
	public static String[] getContactDetailFields(String prefix) {
		String[] res = new String[EAConstants.CONTACT_FIELDS.length];
		res[0] = EAConstants.CONTACT_FIELDS[0];
		for (int i = 1; i < EAConstants.CONTACT_FIELDS.length; i++) {
			res[i] = prefix + EAConstants.CONTACT_FIELDS[i];
		}
		return res;
	}
}
