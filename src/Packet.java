import java.util.*;
import java.util.zip.Adler32;
import java.util.zip.CheckedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;

public class Packet {

	private static final String NULL_TERMINATOR = "0";

	public static void Segmentation(String message , DatagramSocket socket, String address , int port) throws IOException
	{
		byte[] recData = new byte[128];
		InetAddress ip;
		    // Packet Segmentation 
		byte[] buffer = message.getBytes();
		int i = 0;
		int k = 0;
		int l = 0;
		if (address == "local")
		 {
			  ip = InetAddress.getLocalHost(); 
		 }
		 else 
		 {
			  ip = InetAddress.getByName(address);
		 }
		try {
			long sequenceNumber = 64;
			int waitCounter = 0;
			while ( i < buffer.length )
			{
				byte[] split_message = new byte[496];
				while (k < 496 && l < buffer.length)
				{
						split_message[k] = buffer[k + i];
						l++;
						k++;					
				}
				long checkSum = checkSum(split_message);
				byte[] checksumBytes = longToBytes(checkSum);
				byte[] sequenceNumberBytes = longToBytes(sequenceNumber);
				final byte[] finalMessage = joinArray(checksumBytes, sequenceNumberBytes, split_message);
				DatagramPacket pack = new DatagramPacket(finalMessage, finalMessage.length, ip , port);
				socket.send(pack);
				if(waitCounter == 31) {
					DatagramPacket recPacket = new DatagramPacket(recData, recData.length);
					socket.setSoTimeout(10000);
					socket.receive(recPacket);
					System.out.println("ACK RECEIVED");
					waitCounter = 0;
				}				
				i = l;
				k = 0;
				sequenceNumber+=64;
				waitCounter++;
			}
		}catch(ArrayIndexOutOfBoundsException e) {
			System.out.println("k: " + k);
			System.out.println("i: " + i);
			System.out.println("l: " + l);
			System.out.println("Buffer length: " + buffer.length);
		//	System.out.println("Split Message length: " + split_message.length);
		}
		
		DatagramPacket pack = new DatagramPacket(NULL_TERMINATOR.getBytes(), NULL_TERMINATOR.getBytes().length, ip , port);
		socket.send(pack);		 
		 
		
	}
	
	public static String Assemble (byte[] message)
	{
		String buffer = new String(message);
				return buffer;
	}
	
	public static long checkSum (byte[] message)
	{	
		long checkSum = 0;
	    try {
	    	ByteArrayInputStream bais = new ByteArrayInputStream(message);
			CheckedInputStream cis = new CheckedInputStream(bais, new Adler32());
			byte readBuffer[] = new byte[128];
			if(cis.read(readBuffer) >= 0) {
				checkSum = cis.getChecksum().getValue();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		  
		return checkSum;
	}
	
	public static boolean errorDetection(byte[] pre, byte[] post){
		
		long preChecksum = checkSum(pre);
		long postChecksum = checkSum(post);
		
		if (preChecksum == postChecksum){
			return false;
		}
		else{
			return true;
		}
	}
	
	
	private static byte[] longToBytes(long value) {
	    ByteBuffer buffer = ByteBuffer.allocate(8);
	    buffer.putLong(value);
	    return buffer.array();
	}
	
	public static long bytesToLong(byte[] bytes){
		ByteBuffer buffer = ByteBuffer.allocate(8);
		buffer.put(bytes, 0, bytes.length);
		buffer.flip();
		return buffer.getLong();
	}
	
	public static byte[] joinArray(byte[]... arrays) {
        int length = 0;
        for (byte[] array : arrays) {
            length += array.length;
        }

        final byte[] result = new byte[length];

        int offset = 0;
        for (byte[] array : arrays) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }

        return result;
    }


}