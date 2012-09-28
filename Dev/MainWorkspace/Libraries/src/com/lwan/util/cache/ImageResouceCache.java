package com.lwan.util.cache;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import com.lwan.util.ResourceUtil;

/**
 * A singleton cache for storing images used by an application.</br>
 * Usage of this cache means images that will often be fetched from multiple sources
 * will only be fetched once, saving valuable memory.
 * 
 * @author brutalbarbarian
 *
 */
public class ImageResouceCache {
	private static Map <String, BufferedImage> cache;
	
	protected static Map<String, BufferedImage> getCache() {
		if (cache == null) {
			cache = new HashMap<>();
		}
		return cache;
	}
	
	public static void clearCache() {
		getCache().clear();
	}
	
	public static BufferedImage get(String url) throws IOException {
		BufferedImage ret;
		if ((ret=getCache().get(url))==null) {
			ret = ImageIO.read(ResourceUtil.getImageStream(url));
			getCache().put(url, ret);
		}
		return ret;
	}
}