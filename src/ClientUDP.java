import java.net.*;
import java.util.*;
import java.util.Random;
import java.io.*;


public class ClientUDP 
{
	public String request = "GET TestFile.html HTTP/1.0";
	private InetAddress ip;
	private static final String NULL_TERMINATOR = "/0";
	
	 public void createClientSocket(String address, String prob) throws IOException
	 {
		 double chance = Double.parseDouble(prob);
		 int port = 10000;
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
		 DatagramPacket sendPacket = new DatagramPacket(sendData , sendData.length, ip, 10001);
		 System.out.println("Message Sending to Server: " + request);
		 clientSocket.send(sendPacket);
		 
		 String newMessage = "";
		 do {
			 DatagramPacket recPacket = new DatagramPacket(recData, recData.length );
			 clientSocket.receive(recPacket);
			 byte[] pre = recPacket.getData();
			 recPacket = gremlin(recPacket, chance);
			 byte[] post = recPacket.getData();
			 newMessage = new String(recPacket.getData());
			 System.out.print("Message Received from Server: " + newMessage);
			 boolean error = Packet.errorDetection(pre,post);
			 if(error == true){
				 byte[] seqNum = Arrays.copyOfRange(recPacket.getData(), 8, 16);
				 String sNum = new String (seqNum);
				 //int sequenceNum = Integer.parseInt(sNum);
				 System.out.println("\nThere was an ERROR DETECTED in packet %d\n" + sNum);
				 
			 }
		 } while(!newMessage.contains(NULL_TERMINATOR));
		 System.out.println("done");
		 clientSocket.close();
		 
		 
		 
	 }
	 
	public static DatagramPacket gremlin (DatagramPacket packet, double prob) throws IOException{
			
			
			byte[] message = packet.getData();
			byte[] damageArray = new byte[128];
			
			
			byte[] message1 = Arrays.copyOfRange(message, 16,128);
			byte[] header = Arrays.copyOfRange(message, 0, 16);
			
			if ((message.length == 1)){
				System.out.println("\nLAST PACKET\n");
				System.out.println("\nFILE CLOSED\n");
				return packet;
			}
				
			
			Random rand = new Random();
			
			rand.nextBytes(damageArray);
			
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
							int oneByte = rand.nextInt(112);
							oneByte = rand.nextInt(112);
							message1[oneByte] = damageArray[oneByte];
						}
						else if((5 <= byteCorrupt) && (byteCorrupt < 8)){
							System.out.println("\nDamaging 2 bytes in packet\n");
							int firstByte = rand.nextInt(112);
							firstByte = rand.nextInt(112);
							message1[firstByte] = damageArray[firstByte];
							firstByte = rand.nextInt(112);
							message1[firstByte] = damageArray[firstByte];
						}
						else if ((8 <= byteCorrupt) && (byteCorrupt < 10)){
							System.out.println("\nDamaging 3 bytes in packet\n");
							int byteOne = rand.nextInt(112);
							message1[byteOne] = damageArray[byteOne];
							byteOne = rand.nextInt(112);
							message1[byteOne] = damageArray[byteOne];
							byteOne = rand.nextInt(112);
							message1[byteOne] = damageArray[byteOne];
						}
					}
					
				}	
			}
			
	
			

			byte newMessage[] = Packet.joinArray(header,message1);
			
			DatagramPacket newPacket = new DatagramPacket(newMessage, newMessage.length);
			
			return newPacket;
		
		}
	 

}
