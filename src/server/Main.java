package server;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import shared.ByteArrayContainer;
 
public class Main {
	
	public static void main(String[] args) {

		BlockingQueue<ByteArrayContainer> audioQ = 
					new LinkedBlockingQueue<ByteArrayContainer>(100);
		
		Server server = new Server(audioQ);
	    AudioGrabber audioGrabber = new AudioGrabber(audioQ);
	    
	    new Thread(server).start();
	    new Thread(audioGrabber).start();
	}

}
