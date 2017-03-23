import java.util.*;
import java.io.*;
import java.net.*;


public class Gremlin {	
	

	public static void main(String[] args){
		
		double prob = Float.valueOf(args[0]);
		
		prob = prob * 10;
		int percent = (int) Math.round(prob);
		percent = percent * 10;
		System.out.printf("\nProbability to damage packet: %d %%\n", percent);
	
		return;
		
	}

	public static DatagramPacket gremlin (DatagramPacket packet, double prob) throws IOException{
	
		byte[] message = packet.getData();
		byte[] damageArray = new byte[128];
		
		
		if (message.length == 1){
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
						int oneByte = rand.nextInt(128);
						oneByte = rand.nextInt(128);
						message[oneByte] = damageArray[oneByte];
					}
					else if((5 <= byteCorrupt) && (byteCorrupt < 8)){
						System.out.println("\nDamaging 2 bytes in packet\n");
						int firstByte = rand.nextInt(128);
						firstByte = rand.nextInt(128);
						message[firstByte] = damageArray[firstByte];
						firstByte = rand.nextInt(128);
						message[firstByte] = damageArray[firstByte];
					}
					else if ((8 <= byteCorrupt) && (byteCorrupt < 10)){
						System.out.println("\nDamaging 3 bytes in packet\n");
						int byteOne = rand.nextInt(128);
						message[byteOne] = damageArray[byteOne];
						byteOne = rand.nextInt(128);
						message[byteOne] = damageArray[byteOne];
						byteOne = rand.nextInt(128);
						message[byteOne] = damageArray[byteOne];
					}
					
				}
				
			}	
		}
		
		DatagramPacket newPacket = new DatagramPacket(message,message.length);
		
		return newPacket;
	
	}

}

