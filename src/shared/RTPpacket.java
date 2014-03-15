package shared;

public class RTPpacket{

//size of the RTP header:
static int HEADER_SIZE = 2;

//Fields that compose the RTP header
public int SequenceNumber;

//Bitstream of the RTP header
public byte[] header;

//size of the RTP payload
public int payload_size;
//Bitstream of the RTP payload
public byte[] payload;



//--------------------------
//Constructor of an RTPpacket object from header fields and payload bitstream
//--------------------------
public RTPpacket(int seqNum, byte[] data, int data_length){
  //fill by default header fields:



  //fill changing header fields:
  SequenceNumber = seqNum;
  
  //build the header bistream:
  //--------------------------
  header = new byte[HEADER_SIZE];

  //.............
  //TO COMPLETE
  //.............
  //fill the header array of byte with RTP header fields

  header[0] = (byte)SequenceNumber;
  header[1] = (byte)0;


  //fill the payload bitstream:
  //--------------------------
  payload_size = data_length;
  payload = new byte[data_length];

  //fill payload array of byte from data (given in parameter of the constructor)
  payload = data;

  // ! Do not forget to uncomment method printheader() below !

}
  
//--------------------------
//Constructor of an RTPpacket object from the packet bistream 
//--------------------------
public RTPpacket(byte[] packet, int packet_size)
{

  //check if total packet size is lower than the header size
  if (packet_size >= HEADER_SIZE) 
    {
	//get the header bitsream:
	header = new byte[HEADER_SIZE];
	for (int i=0; i < HEADER_SIZE; i++)
	  header[i] = packet[i];

	//get the payload bitstream:
	payload_size = packet_size - HEADER_SIZE;
	payload = new byte[payload_size];
	for (int i=HEADER_SIZE; i < packet_size; i++)
	  payload[i-HEADER_SIZE] = packet[i];

	//interpret the changing fields of the header:
	SequenceNumber = header[0];
	
    }
}

//--------------------------
//getpayload: return the payload bistream of the RTPpacket and its size
//--------------------------
public int getpayload(byte[] data) {

  for (int i=0; i < payload_size; i++)
    data[i] = payload[i];

  return(payload_size);
}

//--------------------------
//getpayload_length: return the length of the payload
//--------------------------
public int getpayload_length() {
  return(payload_size);
}

//--------------------------
//getlength: return the total length of the RTP packet
//--------------------------
public int getlength() {
  return(payload_size + HEADER_SIZE);
}

//--------------------------
//getpacket: returns the packet bitstream and its length
//--------------------------
public int getpacket(byte[] packet)
{
  //construct the packet = header + payload
  for (int i=0; i < HEADER_SIZE; i++)
	packet[i] = header[i];
  for (int i=0; i < payload_size; i++)
	packet[i+HEADER_SIZE] = payload[i];

  //return total size of the packet
  return(payload_size + HEADER_SIZE);
}

//--------------------------
//getsequencenumber
//--------------------------
public int getsequencenumber() {
  return(SequenceNumber);
}

//return the unsigned value of 8-bit integer nb
static int unsigned_int(int nb) {
  if (nb >= 0)
    return(nb);
  else
    return(256+nb);
}

}
