package com.lwan.util.sound;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import javazoom.jl.decoder.JavaLayerException;

import org.farng.mp3.MP3File;
import org.farng.mp3.TagException;
import org.farng.mp3.id3.AbstractID3v2;

public class Mp3File {
	private MP3File file;
	
	public Mp3File(String url) throws IOException {
		try {
			file = new MP3File(url);
		} catch (TagException e) {}
	}
	
	public Mp3File(Path file) throws IOException {
		this(file.toString());
	}

	/**
	 * Construct a playable clip from this MP3File
	 * 
	 * @return
	 */
	public Mp3Clip createClip()  {
		try {
			FileInputStream in = new FileInputStream(file.getMp3file());
			return new Mp3Clip(in);
		} catch (FileNotFoundException | JavaLayerException e) {}
		 return null;
	}
	
	/**
	 * Get the ID3 tags for the mp3file
	 * 
	 * @return
	 */
	public MetaData getMetaData () {
		//if (file.getID3v2Tag() == null) System.out.println(getPath());
		return new Mp3MetaData(file.getID3v2Tag());
	}
	
	/**
	 * Get the Path object representing the file this MP3File is pointing at
	 * 
	 * @return
	 */
	public Path getPath() {
		return file.getMp3file().toPath();
	}
	
	public String toString() {
		return file.getMp3file().toString();
	}
	
	public class Mp3MetaData implements MetaData {
		AbstractID3v2 id3;
		
		Mp3MetaData (AbstractID3v2 abstractID3v2) {
			id3 = abstractID3v2;
		}
		
		public String getAlbumTitle() {
			
			return processString(id3.getAlbumTitle());
		}

		@Override
		public String getSongTitle() {
			return processString(id3.getSongTitle());
		}

		@Override
		public String getArtist() {
			return processString(id3.getLeadArtist());
		}
		
	}
	
	private String processString (String in) {
		//remove all non-ascii characters
		StringBuilder out = new StringBuilder(in.length());
		for (char c : in.toCharArray()) {
			if (c < 128 && c >= 32) {
//				System.out.print((int)c + " ");
				out.append(c);
			}
		}
		System.out.println();
		return out.toString();
	}
}
