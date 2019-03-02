package cpsc441.a2;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * This class is the main server thread
 */
public class MainServer extends BasicWebServer 
{
	private boolean shutdown = false;
	private ServerSocket serverSocket;
	private Socket socket;

	//call the parent constructor
	public MainServer(int port) 
	{
		super(port);
	}

	//start the server
	public void run() 
	{
		//listen for connection requests
		//default server port is set to 2525
		try 
		{
			serverSocket = new ServerSocket(2525);
		} 
		catch (IOException e) 
		{
			System.out.println("socket error");
		}
		//loop to keep the server online
		while(!shutdown)
	 	{		
			try 
			{
				//accept a new connection request from a client
				socket = serverSocket.accept();
				//spawn and start a new worker thread to handle the new connection
				WebServer w = new WebServer(socket);
				w.start();
				
//				ProxyServer p = new ProxyServer(socket);
//				p.start();
				
				
			} 
			catch (IOException e) 
			{
				System.out.println("socket error");
			}
	 	}
	}
	//shuts down the server
	public void shutdown() 
	{
		shutdown = true;
	}
}
