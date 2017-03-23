import java.util.*;
import java.io.*;
import java.net.*;


public class Gremlin {	
	
	public static DatagramPacket gremlin (DatagramPacket packet, float prob, String address, int port) throws IOException{
	
		byte[] message = packet.getData();
		InetAddress ip;
		
		
		if (address == "local")
		 {
			  ip = InetAddress.getLocalHost(); 
		 }
		 else 
		 {
			  ip = InetAddress.getByName(address);
		 }
		
		if (message.length == 1){
			return packet;
		}
		
		Random rand = new Random();
		
		int chanceToDamage = rand.nextInt();
		
		if(chanceToDamage % 2 == 0){
		
			if ((0.0 <= prob) && (prob <= 1.0)){
		
				prob = prob * 10;
				
				int percent = Math.round(prob);
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
						message[oneByte] = message[oneByte-1];
					}
					else if((5 <= byteCorrupt) && (byteCorrupt < 8)){
						System.out.println("\nDamaging 2 bytes in packet\n");
						int firstByte = rand.nextInt(128);
						message[firstByte] = message[firstByte-1];
						int secondByte = rand.nextInt(128);
						message[secondByte] = message[secondByte-1];
					}
					else if ((8 <= byteCorrupt) && (byteCorrupt < 10)){
						System.out.println("\nDamaging 3 bytes in packet\n");
						int byteOne = rand.nextInt(128);
						int byteTwo = rand.nextInt(128);
						int byteThree = rand.nextInt(128);
						message[byteOne] = message[byteOne-1];
						message[byteTwo] = message[byteTwo-1];
						message[byteThree] = message[byteThree-1];
					}
					
				}
				
			}	
		}
		
		DatagramPacket newPacket = new DatagramPacket(message,message.length,ip,port);
		
		return newPacket;
	
	}

}
