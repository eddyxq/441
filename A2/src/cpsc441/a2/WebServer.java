

/**
 * WebServer Class
 * 
 * CPSC 441
 * Assignment 2
 * 
 * @author 	Majid Ghaderi
 *
 */

package cpsc441.a2;

public class WebServer extends BasicWebServer {
	
	private boolean shutdown = false;
	
	// Call the parent constructor
	public WebServer(int port) 
	{
		super(port);
	}

	// Start the server
	public void run() 
	{
		while (!shutdown) Thread.yield();
	}
	
	// shutdown the server
	public void shutdown() 
	{
		shutdown = true;
	}
}
