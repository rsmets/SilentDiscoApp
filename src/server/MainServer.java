package server;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.sound.sampled.UnsupportedAudioFileException;

import shared.AudioFormatContainer;
import shared.ByteArrayContainer;
 
public class MainServer {
	
	public static void main(String[] args) throws UnsupportedAudioFileException, IOException {
		
		Boolean loadFile = false; //false = mic. functionality for true (files) not fully implemented
		AudioFormatContainer audioFC;
		
		BlockingQueue<ByteArrayContainer> audioQ = 
					new LinkedBlockingQueue<ByteArrayContainer>(100);
		
		if(loadFile == true){
			final File file = new File("song.mp3");	
			audioFC = new AudioFormatContainer(file);
		}else{
			audioFC = new AudioFormatContainer();
		}
		
		Server server = new Server(audioQ, audioFC);
	    AudioGrabber audioGrabber = new AudioGrabber(audioQ, audioFC);
	    
	    new Thread(server).start();
	    new Thread(audioGrabber).start();
	}

}
