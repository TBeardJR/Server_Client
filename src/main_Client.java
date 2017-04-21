import java.io.*;
public class main_Client
{

	 static String auburn_tux = "gate.eng.auburn.edu";
	 
	public static void main (String arg[])
	{
		try
		{
			ClientUDP client = new ClientUDP();
			
			//Corrupt Probability - Packet Loss Probability - Packet Delay Probability - Packet Delay Time
			client.createClientSocket("local", arg[0], arg[1], arg[2], arg[3]);
		}
		catch (IOException e)
		{
			System.out.print("Failed");
			e.printStackTrace();
		}
		
	
	}
}
