package client;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import shared.ByteArrayContainer;

public class MainClient {

	public static void main(String[] args) {
		
		BlockingQueue<ByteArrayContainer> audioQ = 
				new LinkedBlockingQueue<ByteArrayContainer>(100);
	
		Client client = new Client(audioQ);
		AudioPlayer audioPlayer = new AudioPlayer(audioQ);
    
		new Thread(client).start();
		new Thread(audioPlayer).start();
	}

}
