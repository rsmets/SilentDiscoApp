package client;

import java.net.*;
import java.util.concurrent.BlockingQueue;

import shared.ByteArrayContainer;
import shared.RTPpacket;

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
        	
        	//recieve the rtp packet from server
        	socket.receive(receivePacket);
        	
        	//create an RTPpacket object
        	RTPpacket rtp_packet = new RTPpacket(receivePacket.getData(), receivePacket.getLength());
        	
        	//get the payload bitstream from the RTPpacket object
        	int payload_length = rtp_packet.getpayload_length();
        	byte [] payload = new byte[payload_length];
        	rtp_packet.getpayload(payload);
        	
        	//FOR DEBUGGING
        	System.out.println("SeqNum recieved: " + rtp_packet.getsequencenumber());
        	
        	audioQ.offer(new ByteArrayContainer(payload));

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
