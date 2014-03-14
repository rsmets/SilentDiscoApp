package client;

import java.util.concurrent.BlockingQueue;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import net.beadsproject.beads.core.AudioContext;
import shared.AudioFormatContainer;
import shared.ByteArrayContainer;

public class AudioPlayer implements Runnable{

	private SourceDataLine outputLine;
	private AudioFormat format;
	private DataLine.Info outInfo;
	final BlockingQueue<ByteArrayContainer> audioQ;
	int bufferSize;
	AudioContext audioContext;
	//private static AudioFormat.Encoding ULAW;
	
	public AudioPlayer(BlockingQueue<ByteArrayContainer> queue) {
		this.setup();
		audioQ = queue;
	}
	
	private void setup(){
		//make format depending on input audio type
		AudioFormatContainer AFC = new AudioFormatContainer();
		format = AFC.getAudioFormat();
		//format = new AudioFormat(ULAW, sampleRate, sampleSizeInBits, 1, 1, sampleRate, true);
		
		//converting to audioContext to handle audio details
		audioContext = new AudioContext(format); 
		
	    outInfo = new DataLine.Info(SourceDataLine.class, format);
	    bufferSize = (int) format.getSampleRate() * format.getFrameSize();
	    bufferSize = audioContext.getBufferSize();
	    //bufferSize = bufferSize / 16; //8000*2/16 = 1000
	    //Why does it sound so much better when the bufferSize is large here
	    //then in the AudioGrabber??
	}
	
	public void playAudio(){
	      try {
	         outputLine = (SourceDataLine)AudioSystem.getLine(outInfo);
	         outputLine.open(format);
	         outputLine.start();

	         byte[] buffer = new byte[bufferSize];

	         while(true){  
	           //just play the audio in audioQ
	           buffer = audioQ.take().getPrimative();
	           System.out.println("client: just grabbed audio");
	           outputLine.write(buffer, 0, buffer.length);
	           
	         }
	      } catch (LineUnavailableException ex) {
	         // Handle the error.
	         System.out.println("error grabbing line in");
	         ex.printStackTrace();
	      } catch (InterruptedException e) {
	    	  System.out.println("interrupted exception apperently...");
			e.printStackTrace();
		} 

	   }

	@Override
	public void run() {
		System.out.println("player starting");
		playAudio();
	}

}
