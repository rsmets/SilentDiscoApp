package server;
import java.io.IOException;
import java.net.*;
import java.util.concurrent.BlockingQueue;

import javax.sound.sampled.*;

import shared.ByteArrayContainer;

public class Server implements Runnable{

	   private DataLine.Info outInfo;
	   private int bufferSize;
	   private javax.sound.sampled.Mixer.Info[] lines;
	   private SourceDataLine outputLine;
	   private AudioFormat format;
	   private byte[] sendData = new byte[1024];
	   private int port;
	   private DatagramSocket serverSocket;
	   private DatagramPacket sendPacket;
	   private DatagramPacket receivePacket;
	   private InetAddress IPAddress;
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

	   public void printLineInfo(){
	    for (int i = 0; i < lines.length; i++){
	      System.out.println(i+": "+lines[i].getName()+"\n"+lines[i].getDescription());
	    }
	   }

	   public void send_to_client() throws Exception{
		 
		  //setting up socket
		  serverSocket = new DatagramSocket(9876);
		  
		  //Initialize connection
		  clientWelcome();
		   
	      while(true){	    	  
	         //grab audio to send from queue
	         sendData = audioQ.take().getPrimative();
	         
	         //setting up packet to send and sending
	         sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
	         serverSocket.send(sendPacket);
	         //send_flag = false;
	      }
	   }
	   
	private void clientWelcome() throws Exception{
		
		  //setting up temporary data holders
		  byte[] recievedData = new byte[1024];
		  
	      //setting up packet to receive data and receiving data
	      receivePacket = new DatagramPacket(recievedData, recievedData.length);
	      serverSocket.receive(receivePacket);
	      System.out.println("Recieved from a client: " + receivePacket.getData());

	      //grabbing IPaddress and port that the client was talking from
	      IPAddress = receivePacket.getAddress();
	      port = receivePacket.getPort();
	      
	      //sending welcome message
	      String welcome = new String("You've successfully connected to the server");
	      sendPacket = new DatagramPacket(welcome.getBytes(), welcome.getBytes().length, IPAddress, port);
	      serverSocket.send(sendPacket);
	}
	
	private void sendMulticast() throws Exception{
		
		byte[] buf = new byte[5512]; //1000 for the AudioGrabber bufferSize;
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
		//playAudio();
		/*try {
			send_to_client();
		} catch (Exception e) {
			System.out.println("something went wrong sending audio");
			e.printStackTrace();
		}*/
		try {
			sendMulticast();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
