package com.lwan.util.sound;

import javazoom.jl.decoder.SampleBuffer;

class AudioBuffer {
	public final short [] buffer;
	public final int bufferLength;
	
	protected AudioBuffer (SampleBuffer data) {
		bufferLength = data.getBufferLength();
		buffer = new short [data.getBufferLength()];
		System.arraycopy(data.getBuffer(), 0, buffer, 0, bufferLength);
	}
}
