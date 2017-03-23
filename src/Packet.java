import java.util.*;
import java.util.zip.Adler32;
import java.util.zip.CheckedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;

public class Packet {

	private static final String NULL_TERMINATOR = "0";

	public static byte[] Segmentation(String message , DatagramSocket socket, String address , int port) throws IOException
	{
		InetAddress ip;
		byte[] split_message = new byte[112];    // Packet Segmentation 
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
			long sequenceNumber = 0;
			while ( i < buffer.length )
			{
				while (k < 112 && l < buffer.length)
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
				i = l;
				k = 0;
				
			}
		} catch(ArrayIndexOutOfBoundsException e) {
			System.out.println("k: " + k);
			System.out.println("i: " + i);
			System.out.println("l: " + l);
			System.out.println("Buffer length: " + buffer.length);
			System.out.println("Split Message length: " + split_message.length);
		}
		
		DatagramPacket pack = new DatagramPacket(NULL_TERMINATOR.getBytes(), NULL_TERMINATOR.getBytes().length, ip , port);
		socket.send(pack);		 
		 
		return split_message; 
		
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
	
	private static byte[] longToBytes(long value) {
	    ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
	    buffer.putLong(value);
	    return buffer.array();
	}
	
	private static byte[] joinArray(byte[]... arrays) {
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