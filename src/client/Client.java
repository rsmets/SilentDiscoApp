package client;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.BlockingQueue;

import shared.AudioFormatContainer;
import shared.ByteArrayContainer;
import shared.RTPpacket;

public class Client implements Runnable{


	private DatagramPacket receivePacket;
	final BlockingQueue<ByteArrayContainer> audioQ;
	
	public Client(BlockingQueue<ByteArrayContainer> queue){
		audioQ = queue;
	}
	
	private void determineAudioFormat(byte[] receiveData, MulticastSocket socket) throws IOException{
		//setting up packet for received data
    	receivePacket = new DatagramPacket(receiveData, receiveData.length);
    	
    	//recieve the rtp packet from server
    	socket.receive(receivePacket);
    	
    	//create an RTPpacket object
    	RTPpacket rtp_packet = new RTPpacket(receivePacket.getData(), receivePacket.getLength());
    	
    	AudioFormatContainer audioFC;
    	if(rtp_packet.Encoding == 0){ //if default mic audio
    		audioFC = new AudioFormatContainer();
    	}else{ //if audio from file
    		audioFC = new AudioFormatContainer(rtp_packet.sampleRate,
    				rtp_packet.sampleSizeInBits,
    				rtp_packet.channels,
    				false, false);
    	}
    	
    	AudioPlayer audioPlayer = new AudioPlayer(audioQ, audioFC);
		new Thread(audioPlayer).start();
	}
	
	private void receiveMulticast() throws Exception{
		MulticastSocket socket = new MulticastSocket(4446);
        InetAddress address = InetAddress.getByName("230.0.0.1");
        socket.joinGroup(address);
        
        byte[] receiveData = new byte[525]; //1000 for server data size (from the AudioGrabber bufferSize)
        
        //figure out what audio format to use from header
        determineAudioFormat(receiveData, socket);
        
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
        	int seqNum = rtp_packet.getsequencenumber();
        	System.out.println("SeqNum recieved: " + seqNum);
        	
        	//if(seqNum )
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
