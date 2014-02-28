package server;

public class ByteArrayContainer {
	private byte[] byte_array;
	
	public ByteArrayContainer(byte[] a) {
		byte_array = a;
	}
	 
	public byte[] getPrimative(){
		return byte_array;
	}

}
