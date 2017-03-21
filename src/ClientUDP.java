import java.net.*;
import java.io.*;


public class ClientUDP 
{
	public String request = "GET TestFile.html HTTP/1.0";
	private InetAddress ip;
	
	 public void createClientSocket(String address ) throws IOException
	 {
		 DatagramSocket clientSocket = new DatagramSocket();
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
		 
		 System.out.print(ip +" is the ip address for " + address);
		 DatagramPacket sendPacket = new DatagramPacket(sendData , sendData.length, ip, 10000);
		 clientSocket.send(sendPacket);
		 DatagramPacket recPacket = new DatagramPacket(recData, recData.length );
		 //clientSocket.receive(recPacket);
		// String newMessage = new String(recPacket.getData());
		// System.out.println(newMessage);
		 clientSocket.close();
		 
		 
		 
	 }
	 

}
