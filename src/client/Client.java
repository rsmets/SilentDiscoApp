package client;
import java.io.IOException;
import java.net.*;
import java.util.concurrent.BlockingQueue;

import server.AudioGrabber;
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
	
	@SuppressWarnings("unused")
	private void setup(){ //not used due to Multicasting now
		//setting up socket
	    try {
			clientSocket = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
		}

	    //setting the IPaddress to send data to
	    try {
			IPAddress = InetAddress.getByName("localhost");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unused")
	private void establishConnnection() throws Exception{ //not used due to Multicasting now
		
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
	
	@SuppressWarnings("unused") //DO NOT USE
	private void receive_from_server() throws IOException{ //not used due to Multicasting now
		
		  byte[] receiveData = new byte[1024];
		
	      //setting up packet for received data
	      receivePacket = new DatagramPacket(receiveData, receiveData.length);
	      
	      //receive data
	      while(true){
	      clientSocket.receive(receivePacket);
	        String received = new String(receivePacket.getData(), 0, receivePacket.getLength());
	        System.out.println("Quote of the Moment: " + received);
	      //doing something recieved packet
	      System.out.println("recieved data from server");
	      //audioQ.offer(new ByteArrayContainer(receivePacket.getData()));

	      }
	      //clientSocket.close();
	      
	   }
	
	private void receiveMulticast() throws Exception{
		MulticastSocket socket = new MulticastSocket(4446);
        InetAddress address = InetAddress.getByName("230.0.0.1");
        socket.joinGroup(address);
        
        byte[] receiveData = new byte[1000]; //1000 for server data size (from the AudioGrabber bufferSize)
        
        while(true){
        	//setting up packet for received data
        	receivePacket = new DatagramPacket(receiveData, receiveData.length);
        	
        	socket.receive(receivePacket);
        	audioQ.offer(new ByteArrayContainer(receivePacket.getData()));
        	/*
            socket.receive(receivePacket);
	        String received = new String(receivePacket.getData(), 0, receivePacket.getLength());
	        System.out.println("Quote of the Moment: " + received);
        	*/
        	//System.out.println("recieved: " + receivePacket.getData());
        	//if exit condition
            //socket.leaveGroup(address);
            //socket.close();
        }
	}

	@Override
	public void run() {
		System.out.println("client starting");
		/*setup();
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
		}*/
		try {
			receiveMulticast();
		} catch (Exception e) {

			e.printStackTrace();
		}
		
	}

}
