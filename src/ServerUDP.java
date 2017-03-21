
import java.util.*;
import java.net.*;
import java.io.*;

public class ServerUDP
{
	private DatagramSocket serverSocket;
	byte[] recData = new byte[128];
	byte[] sendData = new byte[128];
	Packet pack = new Packet();
	
	public ServerUDP (int port ) throws IOException
	{

		serverSocket = new DatagramSocket(port);
		serverSocket.setSoTimeout(1000);
	}
	
	public void run ()
	{
		while (true)
		{
			try
			{
				DatagramPacket recPacket = new DatagramPacket(recData, recData.length);
				serverSocket.receive(recPacket);
				String message = new String(recPacket.getData());
				System.out.print(message + " from server side");
			}
				catch (SocketTimeoutException s)
			{
				
			}
			catch (IOException e)
			{
				
			}
		}
	}
	
	
}

