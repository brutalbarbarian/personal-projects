package com.lwan.util.sound;

import java.io.InputStream;
import java.util.Vector;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.AudioDevice;
import javazoom.jl.player.FactoryRegistry;

/**
 * Audio clip for playing mp3 files
 * Allow playing, pausing, looping, seeking
 * Based off advanced player, using underlying JLayer 1.0.1 decoder
 * 
 * @author brutalbarbarian
 *
 */
public class Mp3Clip implements Runnable{
	private AudioDevice audio;	//output device
	private Vector<AudioBuffer> frames;	//storage of all frames pre-decoded prior to playback
	private Vector<AudioEventListener> playListeners;//storage of all listeners
	
	private int frame;	//current frame position
	private int totalFrames;
	private boolean isPaused;	//pause trigger
	private boolean isDecoding;	//boolean for isdecoding
	private boolean isLooping;	//if it is currently in loop mode
	
	private Executor threadpool;
	
	public Mp3Clip (InputStream stream) throws JavaLayerException {
		frames = new Vector<AudioBuffer>(2000);
		playListeners = new Vector<AudioEventListener>();
		
		final Bitstream bitstream = new Bitstream(stream);
		final Decoder decoder = new Decoder();
		//create audio output device
		audio = FactoryRegistry.systemRegistry().createAudioDevice();
		audio.open(decoder);
		
		//decodes all frame and stores them
		decodeFrames(bitstream, decoder);
		
		//set initial settings
		frame = 0;
		isPaused = true;
		
		//set executor for playback
		threadpool = Executors.newSingleThreadScheduledExecutor();
	}
	
	protected void decodeFrames (final Bitstream bitstream, final Decoder decoder) throws JavaLayerException {
		isDecoding = true;
		Executor decoderThread = Executors.newSingleThreadExecutor();
		decoderThread.execute(new Runnable() {
			public void run() {
				try {
					Header head;
					while ((head = bitstream.readFrame()) != null) {
						frames.add(decoder.decode(head, bitstream));
						totalFrames ++;
						bitstream.closeFrame();
						//System.out.println("decoded: " + frames.size());
					}
					bitstream.close();
					isDecoding = false;
				} catch (Exception ex) {}
			}
		});
	}
	
	/**
	 * Set if this clip when playing will loop
	 * 
	 * @param loop
	 */
	public void setLoop(boolean loop) {
		this.isLooping = loop;
	}
	
	/**
	 * Add an audioEventListener to this clip
	 * 
	 * @param listener
	 */
	public void addListener (AudioEventListener listener) {
		playListeners.add(listener);
	}
	
	protected void notifyListeners () {
		AudioEvent e = new AudioEvent (this, !isPaused);
		for (AudioEventListener listener : playListeners) {
			listener.playbackNotify(e);
		}
	}
	
	/**
	 * Call play instead
	 * 
	 */
	@Deprecated
	public void run() {
		for (;!isPaused; frame++) {
			if (frame >= totalFrames) {	//breaking condition
				if (isDecoding) {	//if currently decoding, let it decode for a little longer
					try {
						Thread.sleep(50);	//should be more then enough unless machine is very slow
					} catch (InterruptedException e) {}
					if (frame >= totalFrames) break;	//if still not enough frames, then stop
				} else if (isLooping) {
					frame = 0;	//reset frame back to 0
				} else {
					break;
				}
			}
			
			AudioBuffer output = frames.get(frame);
			try {
				audio.write(output.buffer, 0, output.bufferLength);
			} catch (JavaLayerException e) {}
		}
		if (!isPaused) {
			isPaused = true;
			frame = 0;	//reset back to initial frame
			notifyListeners();
		}
	}
	
	/**
	 * Plays from current frame position
	 * This is played on a seperate thread
	 * If this is called while it is already playing, a JavaLayerException will be thrown 
	 * 
	 * @throws JavaLayerException
	 */
	public void play () throws JavaLayerException {
		if (!isPaused) {	//is currently playing
			throw new JavaLayerException ("Clip already playing");
		}
		isPaused = false;
		threadpool.execute(this);
		notifyListeners();
	}
	
	/**
	 * Pause playback
	 * 
	 */
	public void pause () {
		isPaused = true;
		notifyListeners();
	}
	
	/**
	 * Stop playback and reset
	 * 
	 */
	public void reset () {
		isPaused = true;
		frame = 0;
		notifyListeners();
	}
	
	public void seek (int position) throws JavaLayerException {
		if (position < 0 || position >= totalFrames) {
			throw new JavaLayerException("Position out of bounds");
		}
		frame = position;
	}
	
	/**
	 * Get the number of frames
	 * 
	 * @return
	 */
	public int getFrames () {
		return totalFrames;
	}
}
