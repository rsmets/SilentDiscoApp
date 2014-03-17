package client;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.sound.sampled.UnsupportedAudioFileException;

import shared.AudioFormatContainer;
import shared.ByteArrayContainer;

public class MainClient {

	public static void main(String[] args) throws UnsupportedAudioFileException, IOException {
		
		BlockingQueue<ByteArrayContainer> audioQ = 
				new LinkedBlockingQueue<ByteArrayContainer>(100);
	
		Client client = new Client(audioQ);
		
		/* USED WHEN TRYING TO IMPLEMENT SENDING OF FILES INSTEAD OF MIC AUIDO... 
		 * NEVER FULLY IMPLEMENTED - BUILT MOST OF CUSTOM RTP HEADER
		final File file = new File("song.mp3");	
		AudioFormatContainer audioFC;
		audioFC = new AudioFormatContainer();
		AudioPlayer audioPlayer = new AudioPlayer(audioQ, audioFC);*/
    
		//would use below audioPlayer and start its thread if knew type of audio before hand
		//it would decrease delay but doing this later allows for dynamic audio formats.
		//AudioPlayer audioPlayer = new AudioPlayer(audioQ);
		
		new Thread(client).start();
		//new Thread(audioPlayer).start();
	}

}
