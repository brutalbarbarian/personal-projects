package com.lwan.musicsync.enums;

/**
 * Non-editable properties
 * 
 * @author Brutalbarbarian
 *
 */
public enum FileAdvancedInfo {
	/* Public Info */
	LENGTH,	// Length of track
	BITRATE,
	EXTENSION,
	
	/* Private info */
	PRIMARY_DIRECTORY;	// primary directory used for an AudioInfo
	
//	ROOT_DIR;	// The original absolute path to the root
//	ORIG_DIR;	// The original relative path from root_dir
//	
//	/**
//	 * Check if an enum is private
//	 * 
//	 * @param e
//	 * @return
//	 */
//	public static boolean isPrivate(Enum<?> e) {
//		return e == ROOT_DIR || e == ORIG_DIR;
//	}
}
