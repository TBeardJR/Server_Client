import java.util.*;
import java.net.*;
import java.io.*;

public class ServerUDP
{
	private DatagramSocket serverSocket;
	byte[] recData = new byte[128];
	byte[] sendData = new byte[128];
	Packet pack = new Packet();
	String response = "HTTP/1.0 200 TestFile.html \r\nContent-Type: text/plain\r\nContent-Length: ";
	
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
				System.out.println("Received request from Client: ");
				String request = new String(recPacket.getData());
				System.out.println(request);
				StringBuilder builder = new StringBuilder();
				File file = new File("TestFile.html");
				try(Scanner scanner = new Scanner(file)) {
					while(scanner.hasNextLine()) {
						builder.append(scanner.nextLine());
					}
				}
				String message = new String(builder.toString());
				int contentLength = message.getBytes().length;
				response = response + contentLength + "\r\n\r\n";
				System.out.println("Sending response object to client.");
				DatagramPacket pack = new DatagramPacket(response.getBytes(), response.getBytes().length, InetAddress.getLocalHost(), 9999);
				serverSocket.send(pack);	
				Packet.Segmentation(message, serverSocket, "local", 9999);
				System.out.println("Response object sent to client.");
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