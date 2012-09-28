package com.lwan.util.sound;

public class AudioEvent {
	private boolean isPlaying;
	private Mp3Clip src;
	
	public AudioEvent (Mp3Clip clip, boolean isPlaying) {
		src = clip;
		this.isPlaying = isPlaying;
	}
	
	public Mp3Clip getSource () {
		return src;
	}
	
	public boolean isPlaying () {
		return isPlaying;
	}
}
