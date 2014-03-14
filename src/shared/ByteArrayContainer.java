package shared;

import java.util.Date;

public class ByteArrayContainer {
	private byte[] byte_array;
	long time;
	
	public ByteArrayContainer(byte[] a) {
		byte_array = a;
		time = new Date().getTime();
	}
	 
	public byte[] getPrimative(){
		return byte_array;
	}
	
	public long getTimeCreated(){
		return time;
	}

}
