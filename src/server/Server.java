package server;
import java.net.*;
import java.util.concurrent.BlockingQueue;

import javax.sound.sampled.*;

import shared.AudioFormatContainer;
import shared.ByteArrayContainer;

public class Server implements Runnable{

	   private DataLine.Info outInfo;
	   private int bufferSize;
	   private javax.sound.sampled.Mixer.Info[] lines;
	   private SourceDataLine outputLine;
	   private AudioFormat format;
	   final BlockingQueue<ByteArrayContainer> audioQ;
	   //private static AudioFormat.Encoding ULAW;

	   public Server(BlockingQueue<ByteArrayContainer> queue){
		  audioQ = queue;
	      this.setup();
	   }
	   
	@SuppressWarnings("unused")
	private void examineBytes(ByteArrayContainer holder){ //FOR DEBUGGING
			byte[] test = holder.getPrimative();
			System.out.println("server: test[1]: " + test[1]);
			//test[1] = 1;
			System.out.println("server2: test[1]: " + test[1]);
		}

	 private void setup(){
	    //make format depending on input audio type
		format = new AudioFormatContainer().getAudioFormat();
		//format = new AudioFormat(ULAW, AudioPlayer.sampleRate, AudioPlayer.sampleSizeInBits, 1, 1, AudioPlayer.sampleFrameRate, true);
		
		lines = AudioSystem.getMixerInfo(); //for printLineInfo debugging
	    outInfo = new DataLine.Info(SourceDataLine.class, format); //for playaudio debugging
	    
	    bufferSize = (int) format.getSampleRate() * format.getFrameSize();
	    bufferSize = bufferSize / 16; //44100*2/16 = 5512
	    //printLineInfo();
	   }

	 public void playAudio(){ //FOR DEBUGGING
	      try {
	         outputLine = (SourceDataLine)AudioSystem.getLine(outInfo);
	         outputLine.open(format);
	         outputLine.start();

	         byte[] buffer = new byte[AudioGrabber.bufferSize];

	         System.out.println("Listening on line " + lines[1].getName() + "...");

	         while(true){  
	           //just play the audio in audioQ
	           buffer = audioQ.take().getPrimative();
	           //System.out.println("server: just grabbed audio");
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

	 public void printLineInfo(){ //FOR DEBUGGING
	    for (int i = 0; i < lines.length; i++){
	      System.out.println(i+": "+lines[i].getName()+"\n"+lines[i].getDescription());
	    }
	   }
	
	private void sendMulticast() throws Exception{
		
		byte[] buf = new byte[bufferSize]; //1000 for the AudioGrabber bufferSize;
		MulticastSocket socket = new MulticastSocket(4447);
		InetAddress group = InetAddress.getByName("230.0.0.1");
		
		// send it
		while(true){
			buf = audioQ.take().getPrimative();

			DatagramPacket packet = new DatagramPacket(buf, buf.length, group, 4446);
			socket.send(packet);
		}
		
	}

	@Override
	public void run() {
		System.out.println("server starting");
		playAudio();

		try {
		//	sendMulticast();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
