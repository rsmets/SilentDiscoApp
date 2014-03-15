package server;
import java.util.concurrent.BlockingQueue;

import javax.sound.sampled.AudioFileFormat.Type;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import net.beadsproject.beads.core.AudioContext;
import shared.AudioFormatContainer;
import shared.ByteArrayContainer;

public class AudioGrabber implements Runnable{
	   private DataLine.Info inInfo;
	   public static int bufferSize;
	   private javax.sound.sampled.Mixer.Info[] lines;
	   private TargetDataLine inputLine;
	   private AudioFormat format;
	   final BlockingQueue<ByteArrayContainer> audioQ;
	   private Type[] types;
	   private AudioInputStream audioStream;
	   //private static AudioFormat.Encoding ULAW;
	   AudioContext audioContext;

	public AudioGrabber(BlockingQueue<ByteArrayContainer> queue) {
		audioQ = queue;
		this.setup();
		types = (Type[]) AudioSystem.getAudioFileTypes();
		for(int i = 0; i < types.length; i++)
			System.out.println(i+ " " + types[i]);

	}
	
	@SuppressWarnings("unused")
	private void examineBytes(ByteArrayContainer holder){
		byte[] test = holder.getPrimative();
		System.out.println("audio: test[1]: " + test[1]);
		test[1] = 1;
		System.out.println("audio2: test[1]: " + test[1]);
	}

	private void setup(){
	    //make format depending on input audio type
		AudioFormatContainer AFC = new AudioFormatContainer();
		format = AFC.getAudioFormat();
	    //format = new AudioFormat(ULAW, AudioPlayer.sampleRate, AudioPlayer.sampleSizeInBits, 1, 1, AudioPlayer.sampleFrameRate, true);
		
		//converting ot audioContext to handle audio details
		audioContext = new AudioContext(format); 
		System.out.println("AC buff size: " + audioContext.getBufferSize());
		
		lines = AudioSystem.getMixerInfo();    
	    inInfo = new DataLine.Info(TargetDataLine.class, format);
	    bufferSize = (int) format.getSampleRate() * format.getFrameSize();
	    bufferSize = bufferSize / 16; //44100*2/16 = 5512
	    bufferSize = audioContext.getBufferSize();
	    printAudioInfo();
	   }

	   public void getAudio(){
		   
	      //grabing audio form line-in port
	      //if (AudioSystem.isLineSupported(Port.Info.LINE_IN)) {
	      if (!AudioSystem.isLineSupported(inInfo)) {
	         System.out.println("line in not supported");
	      }

	      try {
	         //inputLine = (TargetDataLine)AudioSystem.getLine(Port.Info.LINE_IN);
	         inputLine = (TargetDataLine)AudioSystem.getMixer(lines[1]).getLine(inInfo);
	         
	         //converting from lineIn to AudioInputStream
	         audioStream = new AudioInputStream(inputLine);
	        
	         
	         //doing it the raw byte way
	         inputLine.open(format, bufferSize);
	         inputLine.start(); 

	         byte[] buffer = new byte[bufferSize];

	         System.out.println("Listening on line " + lines[1].getName() + "...");

	         //listening to linein
	         while(true){
	           
	           //reading bytes of audio stream
	           audioStream.read(buffer);	 
	        	 
	           //reading raw bytes	 
	           //inputLine.read(buffer,0,buffer.length);
	           //System.out.println("read sample size of: " + sample);

	           //add to queue for Server to grab from and send
	           audioQ.offer(new ByteArrayContainer(buffer));
	           //System.out.println("audioGrabber: just offered audio");
	           
	         }
	      } catch (LineUnavailableException ex) {
	         System.out.println("error grabbing line in");
	         ex.printStackTrace();
	      } catch (Exception e) {
	    	System.out.println("error with socket");
			e.printStackTrace();
		}

	   }

	   public void printAudioInfo(){

		   System.out.println("audio format encoding " + format.getEncoding());
		   System.out.println("frame size: " + format.getFrameSize());
		   System.out.println("frame rate: " + format.getFrameRate());
		   System.out.println("bufferSize = " + bufferSize);
		   System.out.println("sample rate: " + format.getSampleRate());
	   }

	@Override
	public void run() {
		System.out.println("grabber starting");
		getAudio();
	}
}
