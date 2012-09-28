package com.lwan.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;

/**
 * Class dedicated for convenience methods for fetching resources from the classloader
 * This is in particular - for fetching resources from within a packaged jar file 
 * 
 * @author brutalbarbarian
 *
 */
public final class ResourceUtil {
	private ResourceUtil(){};
	private static ClassLoader class_loader = new ResourceUtil().getClass().getClassLoader();
	
	public static ImageInputStream getImageStream (String url) throws IOException {
		return ImageIO.createImageInputStream(getInputStream(url));
	}
	
	public static InputStream getInputStream (String url) throws IOException {
		return class_loader.getResourceAsStream(url);
	}
	
	public static InputStream getFileInputStream (String url) throws IOException {
		return new FileInputStream(new File(url));
	}
}
