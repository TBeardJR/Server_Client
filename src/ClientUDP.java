import java.net.*;
import java.util.*;
import java.util.Random;
import java.io.*;


public class ClientUDP 
{
	public String request = "GET TestFile.html HTTP/1.0";
	private InetAddress ip;
	private static final String NULL_TERMINATOR = "/0";
	
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
						 recPacket = gremlin(recPacket, chance, loseChance, delayChance, delayTime);
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
	 
	public static DatagramPacket gremlin (DatagramPacket packet, Double prob, Double loseProb, Double delayProb, int delayTime) throws IOException{
			
			
			byte[] message = packet.getData();
			byte[] damageArray = new byte[512];
			
			
			byte[] message1 = Arrays.copyOfRange(message, 16, 512);
			byte[] header = Arrays.copyOfRange(message, 0, 16);
			
			if ((message.length == 1)){
				System.out.println("\nLAST PACKET\n");
				System.out.println("\nFILE CLOSED\n");
				return packet;
			}
				
			
			Random rand = new Random();
			Random option = new Random();
			
			rand.nextBytes(damageArray);
			
			int chanceToDamage = rand.nextInt();
			int optionToChoose = option.nextInt(3);
			
			if(chanceToDamage % 2 == 0){
			
				if ((0.0 <= prob) && (prob <= 1.0) && (0.0 <= loseProb) && (loseProb <= 1.0) && (0.0 <= delayProb) && (delayProb <= 1.0)){
				
					prob = prob * 10;
					loseProb = loseProb * 10;
					delayProb = delayProb * 10;
					
					Random rand2 = new Random();
					int damage = rand2.nextInt(10);
					if(optionToChoose == 0){
						if (damage < prob){
							
							Random rand3 = new Random();
							int byteCorrupt = rand3.nextInt(10);
						
							if(byteCorrupt < 5){
								System.out.println("\nDamaging 1 byte in packet!\n");
								int oneByte = rand.nextInt(496);
								oneByte = rand.nextInt(496);
								message1[oneByte] = damageArray[oneByte];
							}
							else if((5 <= byteCorrupt) && (byteCorrupt < 8)){
								System.out.println("\nDamaging 2 bytes in packet!\n");
								int firstByte = rand.nextInt(496);
								firstByte = rand.nextInt(496);
								message1[firstByte] = damageArray[firstByte];
								firstByte = rand.nextInt(496);
								message1[firstByte] = damageArray[firstByte];
							}
							else if ((8 <= byteCorrupt) && (byteCorrupt < 10)){
								System.out.println("\nDamaging 3 bytes in packet!\n");
								int byteOne = rand.nextInt(496);
								message1[byteOne] = damageArray[byteOne];
								byteOne = rand.nextInt(496);
								message1[byteOne] = damageArray[byteOne];
								byteOne = rand.nextInt(496);
								message1[byteOne] = damageArray[byteOne];
							}
						}
					}
					else if (optionToChoose == 1){
							int loseIt = rand.nextInt(10);
							//when packet is lost, all bytes are made 0
							if (loseIt < loseProb){
							System.out.println("\nPACKET LOST!\n");
							byte newMessage[] = Packet.joinArray(header,message1);
							for(int i = 16; i < 512; i++){
									newMessage[i] = 0;
							}
							DatagramPacket newPacket = new DatagramPacket(newMessage, newMessage.length);
							return newPacket;
						}
					}
					else if (optionToChoose == 2){
							int delayIt = rand.nextInt(10);
							
							try{
								if(delayIt < delayProb){	
								long delaytime = (long)delayTime;
								Thread.sleep(delaytime);
								System.out.println("\nPACKET DELAYED FOR " + delayTime + " MILLISECONDS!\n");
								}
							}
							catch (InterruptedException e){
								e.printStackTrace();
							}
					}
					}
			
				}	
			else{
				return packet;
			}
			
	
			

			byte newMessage[] = Packet.joinArray(header,message1);
			
			DatagramPacket newPacket = new DatagramPacket(newMessage, newMessage.length);
			
			return newPacket;
		
		}
	 

}
