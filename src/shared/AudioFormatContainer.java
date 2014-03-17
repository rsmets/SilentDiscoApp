package shared;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.UnsupportedAudioFileException;

import static javax.sound.sampled.AudioFormat.Encoding.PCM_SIGNED;
import static javax.sound.sampled.AudioSystem.getAudioInputStream;

public class AudioFormatContainer {
	
	public float sampleRate;
	public int sampleSizeInBits; //16bits = 2 bytes
	public int channels; //1 for mono. 2 for stereo.
	public boolean signed = true;
	public boolean bigEndian = true;
	//private static AudioFormat.Encoding ULAW;
	public AudioFormat fileFormat;
	public File file = null;	
	public int mp3 = 0;
	
	
	public AudioFormatContainer() {
		//default for VOIP from mic
		
		sampleRate = 8000.0F;
		sampleSizeInBits = 8; 
		channels = 1; 
		signed = true;
		bigEndian = true;
	}
	
	public AudioFormatContainer(float sr, int ssb, int ch, boolean s, boolean bigE) {
		//making the values static for now
		//maybe in the future allow them to be dynamic
		
		sampleRate = sr;
		sampleSizeInBits = ssb; 
		channels = ch; 
		signed = s;
		bigEndian = bigE;
	}
	
	public AudioFormatContainer(File fileIn) throws UnsupportedAudioFileException, IOException {
		
		file = fileIn;
		fileFormat = getAudioInputStream(file).getFormat();
		mp3 = 1;
		
		sampleRate = fileFormat.getSampleRate();
		sampleSizeInBits = 16; 
		channels = fileFormat.getChannels(); 
		bigEndian = false;
	}
	
	private AudioFormat getMixerAudioFormat(){
		return new AudioFormat(sampleRate, 
				sampleSizeInBits, 
				channels, 
				signed, 
				bigEndian);
	}
	
	private AudioFormat getFileAudioFormat(){
		
		return new AudioFormat(PCM_SIGNED,
				sampleRate,
				sampleSizeInBits,
				channels,
				channels * 2,
				sampleRate,
				bigEndian);
	}
	
	public AudioFormat getAudioFormat(){
		//return getFileAudioFormat();
		return getMixerAudioFormat();
	}

}
