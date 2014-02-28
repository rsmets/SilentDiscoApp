package client;
import java.io.*;
import java.net.*;

public class Client {

	public static void main(String args[]) throws Exception
	   {
	      //grabbing input form user
	      BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
	      String sentence = inFromUser.readLine();

	      //setting up socket
	      DatagramSocket clientSocket = new DatagramSocket();

	      //setting the IPaddress to send data to
	      InetAddress IPAddress = InetAddress.getByName("localhost");

	      //setting up the temporary holders for data
	      byte[] sendData = new byte[1024];
	      byte[] receiveData = new byte[1024];
	      
	      //setting the Data to send
	      sendData = sentence.getBytes();

	      //setting up packet to send to IPaddress and sending
	      DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);
	      clientSocket.send(sendPacket);

	      //setting up packet for recieved data and receiving it
	      DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
	      clientSocket.receive(receivePacket);

	      //converting recieved packet to string
	      String modifiedSentence = new String(receivePacket.getData());

	      //doing something recieved packet
	      System.out.println("FROM SERVER:" + modifiedSentence);


	      
	      clientSocket.close();
	   }

}
