package client;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.concurrent.BlockingQueue;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import net.beadsproject.beads.core.AudioContext;
import shared.AudioFormatContainer;
import shared.ByteArrayContainer;

public class AudioPlayer implements Runnable{

	private SourceDataLine outputLine;
	private AudioFormat format;
	private DataLine.Info outInfo;
	final BlockingQueue<ByteArrayContainer> audioQ;
	final AudioFormatContainer audioFC;
	int bufferSize;
	AudioContext audioContext;
	//private static AudioFormat.Encoding ULAW;
	
	public AudioPlayer(BlockingQueue<ByteArrayContainer> queue, AudioFormatContainer AFC) {
		audioFC = AFC;
		audioQ = queue;
		this.setup();
	}
	
	public AudioPlayer(BlockingQueue<ByteArrayContainer> queue) {
		audioFC = new AudioFormatContainer();
		audioQ = queue;
		this.setup();
	}
	
	private void setup(){
		//for getting MixerFormat
		//AudioFormatContainer AFC = new AudioFormatContainer();
		//format = AFC.getAudioFormat();
		//outInfo = new DataLine.Info(SourceDataLine.class, format);
		
		//for getting FileFormat
		format = audioFC.getAudioFormat();
		outInfo = new DataLine.Info(SourceDataLine.class, format);
		
		//converting to audioContext to handle audio details
		audioContext = new AudioContext(format);
	    bufferSize = audioContext.getBufferSize();

	}
	
	public void playAudio(){
	      try {
	         outputLine = (SourceDataLine)AudioSystem.getLine(outInfo);
	         outputLine.open(format);
	         outputLine.start();

	         byte[] buffer = new byte[bufferSize];
	         ByteArrayContainer byteContainer;
	         
	         while(true){  
	           //just play the audio in audioQ
	           byteContainer = audioQ.take();
	           buffer = byteContainer.getPrimative();
	           
	           System.out.print("client: just grabbed audio with delay from creation of " );
	           System.out.println((new Date().getTime() - byteContainer.getTimeCreated()));
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

	//never used...
	public void playMp3() throws InterruptedException {
        //String song = "http://www.ntonyx.com/mp3files/Morning_Flower.mp3";
        Player mp3player = null;
        BufferedInputStream in = null;
        
        byte[] buffer = new byte[bufferSize];
        ByteArrayContainer byteContainer;
        
        while(true){
        byteContainer = audioQ.take();

        buffer = byteContainer.getPrimative();
        
        InputStream is = new ByteArrayInputStream(buffer);
        
        
          in = new BufferedInputStream(is);
          try {
        	  
			mp3player = new Player(in);
			mp3player.play();
			
			
		} catch (JavaLayerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        }
         

}
	
	@Override
	public void run() {
		System.out.println("player starting");
		playAudio();
		
		/* Used when tinkering with sending and playing MP3s... never finished.
		 * try {
			playMp3();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
	}

}
