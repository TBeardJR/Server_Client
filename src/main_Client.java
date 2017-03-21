import java.io.*;
public class main_Client
{

	 String auburn_tux = "gate.eng.auburn.edu";
	 
	public static void main (String arg[])
	{
		try
		{
			ClientUDP client = new ClientUDP();
			client.createClientSocket("local");
		}
		catch (IOException e)
		{
			System.out.print("Failed");
		}
		
	
	}
}
