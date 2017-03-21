
import java.io.*;

public class main_Server 
{
	public static void main (String arg[])
	{
		try
		{
		ServerUDP server = new ServerUDP (10000);
		server.run();
		}
		catch (IOException e)
		{
			
		}
	}

}
