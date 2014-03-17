package server;
import java.util.concurrent.BlockingQueue;

import javax.sound.sampled.AudioFileFormat.Type;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.Port;
import javax.sound.sampled.TargetDataLine;

import static javax.sound.sampled.AudioSystem.getAudioInputStream;
import net.beadsproject.beads.core.AudioContext;
import shared.AudioFormatContainer;
import shared.ByteArrayContainer;

public class AudioGrabber implements Runnable{
	   private DataLine.Info inInfo;
	   public static int bufferSize;
	   private javax.sound.sampled.Mixer.Info[] lines;
	   private javax.sound.sampled.Mixer.Info mic;
	   private TargetDataLine inputLine;
	   private AudioFormat format;
	   final BlockingQueue<ByteArrayContainer> audioQ;
	   final AudioFormatContainer audioFC;
	   private Type[] types;
	   private AudioInputStream audioStream;
	   //private static AudioFormat.Encoding ULAW;
	   AudioContext audioContext;

	public AudioGrabber(BlockingQueue<ByteArrayContainer> queue, 
			AudioFormatContainer audioFormatContainer) {
		audioQ = queue;
		audioFC = audioFormatContainer;
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
		format = audioFC.getAudioFormat();
	    //format = new AudioFormat(ULAW, AudioPlayer.sampleRate, AudioPlayer.sampleSizeInBits, 1, 1, AudioPlayer.sampleFrameRate, true);
		
		//converting to audioContext to handle audio details
		audioContext = new AudioContext(format); 
		System.out.println("AC buff size: " + audioContext.getBufferSize());
		  
	    inInfo = new DataLine.Info(TargetDataLine.class, format);
	    bufferSize = audioContext.getBufferSize();
	    printAudioInfo();
	    
	    lines = AudioSystem.getMixerInfo();  
	    setMicLine();
	   }
	
	public void setMicLine(){
	    for (int i = 0; i < lines.length; i++){
	    	System.out.println(i+": "+lines[i].getName()+"\n"+lines[i].getDescription());
	    	if(lines[i].getName().toLowerCase().contains("mic")){
	    		mic = lines[i]; return;
	    	}
	    }
    }

	public void getMixerAudio(){
		   this.setup();
	      //grabing audio form line-in port
	      //if (AudioSystem.isLineSupported(Port.Info.LINE_IN)) {
	      if (!AudioSystem.isLineSupported(inInfo)) {
	         System.out.println("line in not supported");
	      }

	      try {
	         //inputLine = (TargetDataLine)AudioSystem.getLine(Port.Info.LINE_IN);
	    	 //inputLine = (TargetDataLine)AudioSystem.getLine(inInfo); //for file implementation that was never finished
	         //inputLine = (TargetDataLine)AudioSystem.getMixer(lines[1]).getLine(inInfo);
	    	  inputLine = (TargetDataLine)AudioSystem.getMixer(mic).getLine(inInfo);
	         
	         //converting from lineIn to AudioInputStream
	         audioStream = new AudioInputStream(inputLine);
	        
	         
	         //doing it the raw byte way
	         inputLine.open(format, bufferSize);
	         inputLine.start(); 

	         byte[] buffer = new byte[bufferSize];

	         System.out.println("Listening on line " + mic.getName() + "...");

	         //listening to linein
	         while(true){
	           
	           //reading bytes of audio stream
	           audioStream.read(buffer);	 

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
	   
	 public void getFileAudio(){	
		   this.setup();
		      try {
		         
		         //converting from lineIn to AudioInputStream
		         audioStream = getAudioInputStream(audioFC.file);

		         byte[] buffer = new byte[bufferSize];

		         System.out.println("Pulling from file " + audioFC.file.getName() + "...");

		         //listening to file
		         while(true){
		           
		           //reading bytes of audio stream
		           audioStream.read(buffer);	 

		           //add to queue for Server to grab from and send
		           audioQ.offer(new ByteArrayContainer(buffer));
		           //System.out.println("audioGrabber: just offered audio");
		           
		         }
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
		   System.out.println("channels: " + format.getChannels());
	   }
	   
	   

	@Override
	public void run() {
		System.out.println("grabber starting");
		
		if(audioFC.file == null)
			getMixerAudio();
		else
			getFileAudio();
	}
}
