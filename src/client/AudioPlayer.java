package client;

import java.util.concurrent.BlockingQueue;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import shared.ByteArrayContainer;

public class AudioPlayer implements Runnable{

	private SourceDataLine outputLine;
	private AudioFormat format;
	private DataLine.Info outInfo;
	final BlockingQueue<ByteArrayContainer> audioQ;
	private int bufferSize;
	
	public AudioPlayer(BlockingQueue<ByteArrayContainer> queue) {
		this.setup();
		audioQ = queue;
	}
	
	private void setup(){
		//make format depending on input audio type
	    format = new AudioFormat(8000, 16, 1, true, true);   
	    outInfo = new DataLine.Info(SourceDataLine.class, format);
	    bufferSize = (int) format.getSampleRate() * format.getFrameSize();
	    //bufferSize = bufferSize / 16; //8000/16 = 500
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
