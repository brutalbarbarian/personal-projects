package com.lwan.musicsync.main;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

import com.lwan.util.ImageUtil;

import javafx.scene.image.Image;

public class CustomImageFX extends Image implements Runnable{
	// cached bufferedImage version of this image. 
	// will automatically be freed 10 seconds after being requested unless
	// requested again.
	private BufferedImage cachedBImage;
	private boolean cacheReset;

	public CustomImageFX(InputStream is, double requestedWidth,
			double requestedHeight, boolean preserveRatio, boolean smooth) {
		super(is, requestedWidth, requestedHeight, preserveRatio, smooth);
	}
	
	public CustomImageFX(ByteArrayInputStream is) {
		super(is);
	}

	public String toString() {
		return "Width: " + getWidth() + "; Height: " + getHeight(); 
	}
	
	protected BufferedImage GetTempBfImage (){
		cacheReset = true;
		if (cachedBImage == null) {
			cachedBImage = ImageUtil.imageFXToAWT(this);
			new Thread(this).start();	// start the clear cache thread
		}
		return cachedBImage;
	}
	
	public boolean equals(Object other) {
		BufferedImage oImg;
		if (other instanceof Image) {
			if (other instanceof CustomImageFX) {
				oImg = ((CustomImageFX)other).GetTempBfImage();
			} else {
				oImg = ImageUtil.imageFXToAWT((Image)other);
			}
		} else if (other instanceof BufferedImage) {
			oImg = (BufferedImage)other;
		} else {
			return false;
		}
		BufferedImage tImg = GetTempBfImage();
		
		// if width and height are different...obviously different
		if (tImg.getWidth() != oImg.getWidth() || tImg.getHeight() != oImg.getHeight()) {
			return false;
		}
		
		for (int x = 0; x < tImg.getWidth(); x++) {
			for (int y = 0; y < tImg.getHeight(); y++) {
				// if any pixel is different... return false
				if (tImg.getRGB(x, y) != tImg.getRGB(x, y)) {
					return false;
				}
			}
		}
		
		return true;
	}

	@Override
	public void run() {
		while (!cacheReset) {
			cacheReset = false;
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {}
		}
		// will get automatically freed if nothing else is used it
		cachedBImage = null;
	}
	
}
