package com.lwan.musicsync.audioinfo;

import com.lwan.util.IOUtil;

public class AudioFileInfo {
	public String rootDir;
	public String origPath;
	
	public AudioFileInfo(String rt, String path) {
		rootDir = rt;
		origPath = path;
	}
	
	public String getFullPath() {
		return IOUtil.getAbsolutePath(rootDir, origPath);
	}
	
	public int hashCode() {
		return getFullPath().hashCode();
	}
}
