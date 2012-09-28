package com.lwan.util.sound;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.DecoderException;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.SampleBuffer;

class Decoder extends javazoom.jl.decoder.Decoder {
	public AudioBuffer decode (Header header, Bitstream stream) throws DecoderException {
		return new AudioBuffer((SampleBuffer)super.decodeFrame(header, stream));
	}
}
