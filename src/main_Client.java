import java.io.*;
public class main_Client
{

	 static String auburn_tux = "gate.eng.auburn.edu";
	 
	public static void main (String arg[])
	{
		try
		{
			ClientUDP client = new ClientUDP();
			client.createClientSocket(auburn_tux, arg[0]);
		}
		catch (IOException e)
		{
			System.out.print("Failed");
			e.printStackTrace();
		}
		
	
	}
}
