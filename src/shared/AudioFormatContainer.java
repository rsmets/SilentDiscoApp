package shared;

import javax.sound.sampled.AudioFormat;

public class AudioFormatContainer {
	
	private float sampleRate;
	private int sampleSizeInBits; //16bits = 2 bytes
	private int channels; //1 for mono. 2 for stereo.
	private boolean signed = true;
	private boolean bigEndian = true;
	
	public AudioFormatContainer() {
		//making the values static for now
		//maybe in the future allow them to be dynamic
		
		sampleRate = 44100.0F;
		sampleSizeInBits = 8; 
		channels = 1; 
		signed = true;
		bigEndian = true;
	}
	
	public AudioFormat getAudioFormat(){
		return new AudioFormat(sampleRate, 
				sampleSizeInBits, 
				channels, 
				signed, 
				bigEndian);
	}
	
	public int getBufferSize(){
		return 512;//2700;
	}

}
