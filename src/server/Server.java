package server;
import java.net.*;
import java.util.concurrent.BlockingQueue;

import javax.sound.sampled.*;

public class Server implements Runnable{

	   private DataLine.Info outInfo;
	   private int bufferSize;
	   private javax.sound.sampled.Mixer.Info[] lines;
	   private SourceDataLine outputLine;
	   private AudioFormat format;
	   private byte[] sendData = new byte[1024];
	   private boolean send_flag = false;
	   final BlockingQueue<ByteArrayContainer> audioQ;

	   public Server(BlockingQueue<ByteArrayContainer> queue){
		  audioQ = queue;
	      this.setup();
	   }
	   
	   @SuppressWarnings("unused")
	private void examineBytes(ByteArrayContainer holder){
			byte[] test = holder.getPrimative();
			System.out.println("server: test[1]: " + test[1]);
			//test[1] = 1;
			System.out.println("server2: test[1]: " + test[1]);
		}

	   private void setup(){
	      //make format depending on input audio type
	    format = new AudioFormat(8000, 8, 1, true, true); 
	    lines = AudioSystem.getMixerInfo();    
	    outInfo = new DataLine.Info(SourceDataLine.class, format);
	    bufferSize = (int) format.getSampleRate() * format.getFrameSize();
	    bufferSize = bufferSize / 16; //8000/16 = 500
	    //printLineInfo();
	   }

	   public void playAudio(){
	      try {
	         outputLine = (SourceDataLine)AudioSystem.getLine(outInfo);
	         outputLine.open(format);
	         outputLine.start();

	         byte[] buffer = new byte[bufferSize];

	         System.out.println("Listening on line " + lines[1].getName() + "...");

	         while(true){  
	           //just play the audio in audioQ
	           buffer = audioQ.take().getPrimative();
	           System.out.println("server: just grabbed audio");
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

	   public void printLineInfo(){
	    for (int i = 0; i < lines.length; i++){
	      System.out.println(i+": "+lines[i].getName()+"\n"+lines[i].getDescription());
	    }
	   }

	   public void send_over_network() throws Exception{
	      //setting up socket
	      DatagramSocket serverSocket = new DatagramSocket(9876);
	   
	      //setting up temporary data holders
	      byte[] receiveData = new byte[1024];
	      //byte[] sendData = new byte[1024];

	      //while(true){
	      while(send_flag){
	         //setting up packet to receive data and receiving data
	         DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
	         serverSocket.receive(receivePacket);

	         //converting received packet's data to a different data type
	         String sentence = new String( receivePacket.getData());

	         //doing something with data received
	         System.out.println("RECEIVED: " + sentence);

	         //grabbing IPaddress and port that the client was talking from
	         InetAddress IPAddress = receivePacket.getAddress();
	         int port = receivePacket.getPort();

	         //doing something with data before sending
	         String capitalizedSentence = sentence.toUpperCase();
	         sendData = capitalizedSentence.getBytes();
	         

	         //setting up packet to send and sending
	         DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
	         serverSocket.send(sendPacket);
	         send_flag = false;
	      }
	   }

	@Override
	public void run() {
		//playAudio();
	}

}
