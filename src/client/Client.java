package client;
import java.io.IOException;
import java.net.*;
import java.util.concurrent.BlockingQueue;

import shared.ByteArrayContainer;

public class Client implements Runnable{

	private DatagramSocket clientSocket;
	private DatagramPacket sendPacket;
	private DatagramPacket receivePacket;
	private InetAddress IPAddress;
	final BlockingQueue<ByteArrayContainer> audioQ;
	
	public Client(BlockingQueue<ByteArrayContainer> queue){
		audioQ = queue;
	}
	
	private void setup(){
		//setting up socket
	    try {
			clientSocket = new DatagramSocket();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	    //setting the IPaddress to send data to
	    try {
			IPAddress = InetAddress.getByName("localhost");
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void establishConnnection() throws Exception{
		
		byte[] sendData = new byte[1024];
		byte[] receiveData = new byte[1024];
		
		//setting initialization message to send
	    String connect_message = new String("I want to Silent Disco, Baby");
	    sendData = connect_message.getBytes();

	    //setting up packet to send to IPaddress and sending
	    sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);
	    clientSocket.send(sendPacket);
	    
	    receivePacket = new DatagramPacket(receiveData, receiveData.length);
	    clientSocket.receive(receivePacket);
	    
	    //String modifiedSentence = new String(receivePacket.getData());
	    System.out.println("From server: " + receivePacket.getData());
	}
	
	private void receive_from_server() throws IOException{
		
		  byte[] receiveData = new byte[1024];
		
	      //setting up packet for received data
	      receivePacket = new DatagramPacket(receiveData, receiveData.length);
	      
	      //receive data
	      while(true){
	      clientSocket.receive(receivePacket);

	      //doing something recieved packet
	      System.out.println("recieved data from server");
	      audioQ.offer(new ByteArrayContainer(receivePacket.getData()));

	      }
	      //clientSocket.close();
	      
	   }

	@Override
	public void run() {
		setup();
		try {
			establishConnnection();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			receive_from_server();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
