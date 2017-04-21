import java.util.*;
import java.util.zip.Adler32;
import java.util.zip.CheckedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;

public class Packet {

	private static final String NULL_TERMINATOR = "0";

	public static void Segmentation(String message , DatagramSocket socket, String address , int port) throws IOException
	{
		byte[] recData = new byte[48];
		InetAddress ip;
		DatagramSocket ACKSocket;
		//DatagramSocket NAKSocket;
		
		ACKSocket = new DatagramSocket(10002);
		//NAKSocket = new DatagramSocket(10000);
		
		    // Packet Segmentation 
		byte[] buffer = message.getBytes();
		int i = 0;
		int k = 0;
		int l = 0;
		if (address == "local")
		 {
			  ip = InetAddress.getLocalHost(); 
		 }
		 else 
		 {
			  ip = InetAddress.getByName(address);
		 }
		try {
			long sequenceNumber = 64;
			int waitCounter = 0;
			while ( i < buffer.length )
			{
				byte[] split_message = new byte[496];
				while (k < 496 && l < buffer.length)
				{
						split_message[k] = buffer[k + i];
						l++;
						k++;					
				}
				long checkSum = checkSum(split_message);
				byte[] checksumBytes = longToBytes(checkSum);
				byte[] sequenceNumberBytes = longToBytes(sequenceNumber);
				final byte[] finalMessage = joinArray(checksumBytes, sequenceNumberBytes, split_message);
				DatagramPacket pack = new DatagramPacket(finalMessage, finalMessage.length, ip , port);
				socket.send(pack);
				if(waitCounter == 31) {
					DatagramPacket recPacket = new DatagramPacket(recData, recData.length, ip, 10002);
					ACKSocket.setSoTimeout(10000);
					ACKSocket.receive(recPacket);
					//socket.setSoTimeout(10000);
					//socket.receive(recPacket);
					System.out.println("ACK RECEIVED");
					waitCounter = 0;
				}				
				i = l;
				k = 0;
				sequenceNumber+=64;
				waitCounter++;
			}
		}catch(ArrayIndexOutOfBoundsException e) {
			System.out.println("k: " + k);
			System.out.println("i: " + i);
			System.out.println("l: " + l);
			System.out.println("Buffer length: " + buffer.length);
		//	System.out.println("Split Message length: " + split_message.length);
		}
		
		DatagramPacket pack = new DatagramPacket(NULL_TERMINATOR.getBytes(), NULL_TERMINATOR.getBytes().length, ip , port);
		socket.send(pack);		 
		//ACKSocket.close();
		//NAKSocket.close();
		 
		
	}
	
	public static String Assemble (byte[] message)
	{
		String buffer = new String(message);
				return buffer;
	}
	
	public static long checkSum (byte[] message)
	{	
		long checkSum = 0;
	    try {
	    	ByteArrayInputStream bais = new ByteArrayInputStream(message);
			CheckedInputStream cis = new CheckedInputStream(bais, new Adler32());
			byte readBuffer[] = new byte[128];
			if(cis.read(readBuffer) >= 0) {
				checkSum = cis.getChecksum().getValue();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		  
		return checkSum;
	}
	
	public static boolean errorDetection(byte[] pre, byte[] post){
		
		long preChecksum = checkSum(pre);
		long postChecksum = checkSum(post);
		
		if (preChecksum == postChecksum){
			return false;
		}
		else{
			return true;
		}
	}
	
	
	private static byte[] longToBytes(long value) {
	    ByteBuffer buffer = ByteBuffer.allocate(8);
	    buffer.putLong(value);
	    return buffer.array();
	}
	
	public static long bytesToLong(byte[] bytes){
		ByteBuffer buffer = ByteBuffer.allocate(8);
		buffer.put(bytes, 0, bytes.length);
		buffer.flip();
		return buffer.getLong();
	}
	
	public static byte[] joinArray(byte[]... arrays) {
        int length = 0;
        for (byte[] array : arrays) {
            length += array.length;
        }

        final byte[] result = new byte[length];

        int offset = 0;
        for (byte[] array : arrays) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }

        return result;
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