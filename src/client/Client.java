package client;

import java.net.*;
import java.util.concurrent.BlockingQueue;

import shared.ByteArrayContainer;

public class Client implements Runnable{


	private DatagramPacket receivePacket;
	final BlockingQueue<ByteArrayContainer> audioQ;
	
	public Client(BlockingQueue<ByteArrayContainer> queue){
		audioQ = queue;
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

		try {
			receiveMulticast();
		} catch (Exception e) {

			e.printStackTrace();
		}
		
	}

}
