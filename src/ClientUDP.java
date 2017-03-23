import java.net.*;
import java.util.Random;
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
			 recPacket = gremlin(recPacket, .5);
			 newMessage = new String(recPacket.getData());
			 System.out.print(newMessage);
		 } while(!newMessage.contains(NULL_TERMINATOR));
		 System.out.println("done");
		 clientSocket.close();
		 
		 
		 
	 }
	 
	public static DatagramPacket gremlin (DatagramPacket packet, double prob) throws IOException{
			
			System.out.println("\nNOW INSIDE GREMLIN FUNCTION\n");
			byte[] message = packet.getData();
			
			if ((message.length == 1)){
				return packet;
			}
				
			
			Random rand = new Random();
			
			int chanceToDamage = rand.nextInt();
			
			if(chanceToDamage % 2 == 0){
			
				if ((0.0 <= prob) && (prob <= 1.0)){
				
					prob = prob * 10;
					
					int percent = (int) Math.round(prob);
					percent = percent * 10;
					System.out.printf("\nProbability to damage packet: %d %%\n", percent);
					
					Random rand2 = new Random();
					int damage = rand2.nextInt(10);
					
					if (damage < prob){
						
						Random rand3 = new Random();
						int byteCorrupt = rand3.nextInt(10);
						
						if(byteCorrupt < 5){
							System.out.println("\nDamaging 1 byte in packet\n");
							int oneByte = rand.nextInt(128);
							oneByte = rand.nextInt(128);
							message[oneByte] = message[oneByte-1];
						}
						else if((5 <= byteCorrupt) && (byteCorrupt < 8)){
							System.out.println("\nDamaging 2 bytes in packet\n");
							int firstByte = rand.nextInt(128);
							firstByte = rand.nextInt(128);
							message[firstByte] = message[firstByte-1];
							firstByte = rand.nextInt(128);
							message[firstByte] = message[firstByte-1];
						}
						else if ((8 <= byteCorrupt) && (byteCorrupt < 10)){
							System.out.println("\nDamaging 3 bytes in packet\n");
							int byteOne = rand.nextInt(128);
							message[byteOne] = message[byteOne-1];
							byteOne = rand.nextInt(128);
							message[byteOne] = message[byteOne-1];
							byteOne = rand.nextInt(128);
							message[byteOne] = message[byteOne-1];
						}
					}
					
				}	
			}
			
			DatagramPacket newPacket = new DatagramPacket(message,message.length);
			
			return newPacket;
		
		}
	 

}
