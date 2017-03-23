import java.net.*;
import java.io.*;


public class ClientUDP 
{
	public String request = "GET TestFile.html HTTP/1.0";
	private InetAddress ip;
	private static final String NULL_TERMINATOR = "/0";
	
	 public void createClientSocket(String address ) throws IOException
	 {
		 int port = 9999;
		 DatagramSocket clientSocket = new DatagramSocket(port);
		 byte[] sendData = new byte[128];
		 byte[] recData = new byte[128];
		 sendData = request.getBytes();
		 if (address == "local")
		 {
			  ip = InetAddress.getLocalHost(); 
		 }
		 else 
		 {
			  ip = InetAddress.getByName(address);
		 }
		 
		 System.out.println(ip +" is the ip address for " + address);
		 DatagramPacket sendPacket = new DatagramPacket(sendData , sendData.length, ip, 10000);
		 clientSocket.send(sendPacket);
		 
		 String newMessage = "";
		 do {
			 DatagramPacket recPacket = new DatagramPacket(recData, recData.length );
			 clientSocket.receive(recPacket);
			 newMessage = new String(recPacket.getData());
			 System.out.print(newMessage);
		 } while(!newMessage.contains(NULL_TERMINATOR));
		 System.out.println("done");
		
		 clientSocket.close();
		 
		 
		 
	 }
	 

}
