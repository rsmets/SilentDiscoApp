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
	
	public AudioPlayer(BlockingQueue<ByteArrayContainer> queue) {
		// TODO Auto-generated constructor stub
		audioQ = queue;
	}
	
	public void playAudio(){
	      try {
	         outputLine = (SourceDataLine)AudioSystem.getLine(outInfo);
	         outputLine.open(format);
	         outputLine.start();

	         int bufferSize = 500; //TODO need to change this to be dynamically set
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
		// TODO Auto-generated method stub
		playAudio();
	}

}
