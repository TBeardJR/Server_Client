import java.util.*;
import java.io.IOException;
import java.net.*;
public class Packet 
{

	

	public static byte[] Segmentation(String message , DatagramSocket socket, String address , int port) throws IOException
	{
		InetAddress ip;
		byte[] split_message = new byte[128];    // Packet Segmentation 
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
		while ( i < buffer.length )
		{
			while (k < 128)
			{
				split_message[k] = buffer[k + i];
				l++;
				k++;
			}
			DatagramPacket pack = new DatagramPacket(split_message, split_message.length, ip , port);
			socket.send(pack);
			i = l;
			k = 0;
			
		}
		
		 
			 
		 
		 
		return split_message; 
		
	}
	
	public static String Assemble (byte[] message)
	{
		String buffer = new String(message);
				return buffer;
	}
	
	public static int checkSum (byte[] message)
	{
		
		return message.length;
	}

}
