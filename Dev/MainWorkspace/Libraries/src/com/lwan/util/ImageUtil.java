package com.lwan.util;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javafx.scene.image.Image;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

public final class ImageUtil {
	
	/**
	 * Get a buffered image from a javaFX image.
	 * Will return null if the passed in image is null, or if
	 * img.impl_getPlatformImage() returned null.
	 * 
	 * @param img
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static BufferedImage imageFXToAWT (Image img) {
		if (img == null) {
			return null;
		}
		com.sun.prism.Image imgP = (com.sun.prism.Image) img.impl_getPlatformImage();
		if (imgP != null) {
			return com.sun.prism.BufferedImageTools.exportBufferedImage(imgP, "");
		} else {
			return null;	
		}
	}
	
	public static byte[] imageToByteArray (BufferedImage img, String format) throws IOException {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		ImageIO.write(img, format, stream);
		return stream.toByteArray();
	}

	/**
	 * Get the width and height of an image by reading the header only.
	 * This is much faster then trying to load up an image using
	 * ImageIO.read()
	 */
	public static Dimension getImageSize(InputStream input) throws IOException {
		ImageInputStream stream = ImageIO.createImageInputStream(input);
		if (stream != null) {
			return getImageSize(stream);
		} else {
			return new Dimension(0,0);
		}
	}
	
	/**
	 * Get the width and height of an image by reading the header only.
	 * This is much faster then trying to load up an image using
	 * ImageIO.read()</br>
	 * </br>
	 * http://stackoverflow.com/questions/1559253/java-imageio-getting-image-dimension-without-reading-the-entire-file\
	 * 
	 * @param stream
	 * @return
	 * @throws IOException 
	 */
	public static Dimension getImageSize(ImageInputStream in) throws IOException {
//		ImageInputStream in = ImageIO.createImageInputStream(stream);
		try {
			final Iterator<ImageReader> readers = ImageIO.getImageReaders(in);
			if (readers.hasNext()) {
				ImageReader reader = (ImageReader) readers.next();
				try {
					reader.setInput(in);
					return new Dimension(reader.getWidth(0), reader.getHeight(0));
				} finally {
					reader.dispose();
				}
			}
		} finally {
			if (in != null) in.close();
		}
		return new Dimension(0, 0); 
	}
	
	/**
	 * Convinence Method for reading in an image from url
	 * 
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public static BufferedImage getImage (String url) throws IOException {
		return ImageIO.read(new File(url));
	}
	
	/**
	 * Convinence Method for reading in an image from a stream
	 * 
	 * @param stream
	 * @return
	 * @throws IOException
	 */
	public static BufferedImage getImage (InputStream stream) throws IOException {
		return ImageIO.read(stream);
	}
	
	/**
	 * Construct a blank image with dimensions: width, height.
	 * Each pixel will simply be of ARGB type, with color black.
	 * 
	 * @param width
	 * @param height
	 * @return
	 */
	public static BufferedImage getBlankImage (int width, int height) {
		return new BufferedImage (width, height, BufferedImage.TYPE_INT_ARGB);
	}
	
	/**
	 * Transforms the passed in image grey and applies a tint to it such that it is brighter.
	 * 
	 * @param orig
	 * 		The original image
	 * @param brightness
	 * 		This value must be >= 1. Any lower will result in exception being thrown </br>
	 * 		If this value is 1, the image will become pure white
	 * 		The higher this value is, the closer this image will resemble defualt greyscale
	 * @return
	 */
	public static BufferedImage applyGreyTint (BufferedImage orig, double brightness) {
		if (brightness < 1) {
			throw new IllegalArgumentException();
		}
		int width = orig.getWidth();
		int height = orig.getHeight();
		BufferedImage newImg = ImageUtil.getBlankImage(width, height);
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				int argb = orig.getRGB(x, y);
				int c = ColorUtil.getGray(argb);
				c = c + (int)((255-c)/brightness);
				int a = ColorUtil.getAlpha(argb);
				newImg.setRGB(x, y, ColorUtil.getGrayARGB(c, a));
			}
		}
		return newImg;
	}
	
	/**
	 * Convert an image to grayscale
	 * 
	 * @param orig
	 * @return
	 */
	public static BufferedImage convertToGray (BufferedImage orig) {
		int width = orig.getWidth();
		int height = orig.getHeight();
		
		BufferedImage newImage = getBlankImage(width, height);
		
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				int argb = orig.getRGB(x,y);
				int c = ColorUtil.getGray(argb);
				int a = ColorUtil.getAlpha(argb);
				newImage.setRGB(x, y, ColorUtil.getGrayARGB(c, a));
			}
		}
		return newImage;
	}
	
	/**
	 * Convenience method that returns a scaled instance of the
	 * provided {@code BufferedImage}.
	 *
	 * @param img the original image to be scaled
	 * @param targetWidth the desired width of the scaled instance,
	 *    in pixels
	 * @param targetHeight the desired height of the scaled instance,
	 *    in pixels
	 * @param hint one of the rendering hints that corresponds to
	 *    {@code RenderingHints.KEY_INTERPOLATION} (e.g.
	 *    {@code RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR},
	 *    {@code RenderingHints.VALUE_INTERPOLATION_BILINEAR},
	 *    {@code RenderingHints.VALUE_INTERPOLATION_BICUBIC})
	 * @param higherQuality if true, this method will use a multi-step
	 *    scaling technique that provides higher quality than the usual
	 *    one-step technique (only useful in down-scaling cases, where
	 *    {@code targetWidth} or {@code targetHeight} is
	 *    smaller than the original dimensions, and generally only when
	 *    the {@code BILINEAR} hint is specified)
	 * @return a scaled version of the original {@codey BufferedImage}
	 */
	public static BufferedImage getScaledInstance(BufferedImage img,
			int targetWidth,
			int targetHeight,
			Object hint,
			boolean higherQuality)
	{
		int type = (img.getTransparency() == Transparency.OPAQUE) ?
				BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
		BufferedImage ret = (BufferedImage)img;
		int w, h;
		if (higherQuality) {
			w = img.getWidth();
			h = img.getHeight();
		} else {
			w = targetWidth;
			h = targetHeight;
		}

		do {
			if (higherQuality && w > targetWidth) {
				w /= 2;
				if (w < targetWidth) {
					w = targetWidth;
				}
			}

			if (higherQuality && h > targetHeight) {
				h /= 2;
				if (h < targetHeight) {
					h = targetHeight;
				}
			}

			BufferedImage tmp = new BufferedImage(w, h, type);
			Graphics2D g2 = tmp.createGraphics();
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, hint);
			g2.drawImage(ret, 0, 0, w, h, null);
			g2.dispose();

			ret = tmp;
		} while (w != targetWidth || h != targetHeight);

		return ret;
	}
	
	/**
	 * Parse a gif file as a list of BufferedImage's.
	 * Each of these images are independent and represent a frame within the animation Gif.
	 * 
	 * @param stream
	 * @return
	 * @throws IOException
	 */
	public static List<BufferedImage> parseGif(ImageInputStream stream) throws IOException {
		ImageReader reader = ImageIO.getImageReadersBySuffix("gif").next();
		Vector <BufferedImage> list = new Vector<BufferedImage>();
		reader.setInput(stream);
		Iterator<IIOImage> it = reader.readAll(null);
		BufferedImage pre = (BufferedImage) it.next().getRenderedImage();
		BufferedImage nxt = null;
		int width = pre.getWidth();
		int height = pre.getHeight();
		list.add(pre);
		while (it.hasNext()) {
			IIOImage iomg = it.next();
			nxt = new BufferedImage (width, height, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = nxt.createGraphics();
			g.drawImage(pre, 0, 0, null);
			g.drawImage((BufferedImage)iomg.getRenderedImage(), 0, 0, null);
			g.dispose();
			list.add(nxt);
			pre = nxt;
		}
		return list;
	}
}