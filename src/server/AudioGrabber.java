package server;
import java.util.concurrent.BlockingQueue;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import shared.ByteArrayContainer;

public class AudioGrabber implements Runnable{
	   private DataLine.Info inInfo;
	   private int bufferSize;
	   private javax.sound.sampled.Mixer.Info[] lines;
	   private TargetDataLine inputLine;
	   private AudioFormat format;
	   final BlockingQueue<ByteArrayContainer> audioQ;

	public AudioGrabber(BlockingQueue<ByteArrayContainer> queue) {
		audioQ = queue;
		this.setup();
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
	    format = new AudioFormat(8000, 16, 1, true, true); 
	    lines = AudioSystem.getMixerInfo();    
	    inInfo = new DataLine.Info(TargetDataLine.class, format);
	    bufferSize = (int) format.getSampleRate() * format.getFrameSize();
	    bufferSize = bufferSize / 16; //8000/16 = 500
	    printLineInfo();
	   }

	   public void getAudio(){
	      /*
	      The Mixer interface provides methods for 
	      obtaining a mixer's lines. These include 
	      target lines, to which the mixer delivers its mixed audio.  
	      The source lines are input ports such as the microphone input, and the target lines are 
	      TargetDataLines, which deliver audio to the application program.
	      */

	      //grabing audio form line-in port
	      //if (AudioSystem.isLineSupported(Port.Info.LINE_IN)) {
	      if (!AudioSystem.isLineSupported(inInfo)) {
	         System.out.println("line in not supported");
	      }

	      try {
	         //inputLine = (TargetDataLine)AudioSystem.getLine(Port.Info.LINE_IN);
	         inputLine = (TargetDataLine)AudioSystem.getMixer(lines[1]).getLine(inInfo);
	         inputLine.open(format, bufferSize);
	         inputLine.start(); 

	         byte[] buffer = new byte[bufferSize];

	         System.out.println("Listening on line " + lines[1].getName() + "...");

	         //listening to linein
	         while(true){
	           int sample = inputLine.read(buffer,0,buffer.length);
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

	   public void printLineInfo(){
	    //for (int i = 0; i < lines.length; i++){
	    //  System.out.println(i+": "+lines[i].getName()+"\n"+lines[i].getDescription());
	    //}
		   System.out.println("audio format encoding " + format.getEncoding());
	   }

	@Override
	public void run() {
		System.out.println("grabber starting");
		getAudio();
	}
}
