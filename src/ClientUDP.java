import java.net.*;
import java.util.*;
import java.util.Random;
import java.io.*;


public class ClientUDP 
{
	public String request = "GET TestFile.html HTTP/1.0";
	private InetAddress ip;
	private static final String NULL_TERMINATOR = "/0";
	private int message_length = 0 ;
	
	 public void createClientSocket(String address, String prob, String lose, String delay, String time) throws IOException
	 {
		 Double chance = Double.parseDouble(prob);
		 Double loseChance = Double.parseDouble(lose);
		 Double delayChance = Double.parseDouble(delay);
		 Integer delayTime = Integer.parseInt(time);
		 int waitCounter = 0;
		 int packetcount = 0;
		 
		 int port = 10000;
		 DatagramSocket clientSocket = new DatagramSocket(port);
		 byte[] sendData = new byte[512];
		 byte[] recData = new byte[512];
		 byte[] ACK = new byte[48];
		// byte[] NAK = new byte[49];
		 
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
		 Double percent = Double.parseDouble(prob);
		 Double percent1 = Double.parseDouble(lose);
		 Double percent2 = Double.parseDouble(delay);
		 percent1 = percent1 * 100;
		 percent2 = percent2 * 100;
		 percent = percent * 100;
		 System.out.printf("\nProbability to damage packet: " + percent + " %%\n");
		 System.out.printf("\nProbability to lose packet: " + percent1 + " %%\n");
		 System.out.printf("\nProbability to delay packet: " + percent2 + " %% with delay time of " + delayTime + " milliseconds\n");
		 System.out.println("\nMessage Sending to Server: " + request + "\n");
		 clientSocket.send(sendPacket);
		 
		 String newMessage = "";
		 boolean isDone = false;
		 try{
			    PrintWriter writer = new PrintWriter("FinalFile.html", "UTF-8");			    
			    do {
					 DatagramPacket recPacket = new DatagramPacket(recData, recData.length );
					 clientSocket.receive(recPacket);
					 byte[] pre = recPacket.getData();
					 if(pre[0] == 0){
						 recPacket = Packet.gremlin(recPacket, chance, loseChance, delayChance, delayTime);
					 }
					 else{
						 System.out.print("\nNull Terminator Reached: END OF FILE\n");
						 isDone = true;
					 }
					 if(!isDone) {
						 byte[] post = recPacket.getData();
						 newMessage = new String(recPacket.getData());
						 System.out.println("Message Received from Server: " + newMessage.substring(16,512));
						 packetcount++;
						 writer.println(newMessage.substring(16,512));
						 if(waitCounter == 31){
							 DatagramPacket ACKPacket = new DatagramPacket(ACK , ACK.length, ip, 10002);
							 clientSocket.send(ACKPacket);
							 waitCounter = 0;
							 System.out.println("ACK SENT");
						 }
						 waitCounter++;
						 boolean error = Packet.errorDetection(pre,post);
						 if(error == true){
							 byte[] seqNum = Arrays.copyOfRange(recPacket.getData(), 8, 16);
							 long sNum = Packet.bytesToLong(seqNum);
							 System.out.println("There was an ERROR DETECTED in packet " + sNum + "\n");
							 
						 }
					 }
					
				 } while(!isDone);
			     writer.close();
				 System.out.println("FinalFile.html was created and written to in the current directory.");
				 System.out.println("\n" + packetcount);
				 clientSocket.close();
			} catch (IOException e) {
			   // do something
			}
		 
		 
		 
		 
		 
	 }
	 
	
	 

}
