package cpsc441.a1;

import java.net.Socket;
import java.util.ArrayList;

public class QuickUrl extends ConcurrentHttp 
{
	public void getObject(String s) 
	{
		//parse URL into host, port, and pathname
		String[] parsedURL = new Parser().parseURL(s);
		String host = parsedURL[0];
		int port = Integer.parseInt(parsedURL[1]);
		String pathname = parsedURL[2];
		
		//send head request
		int contentLength = new HeadRequest().getContentLength(pathname, port, host);
		
		//send range request
		Socket socket = new RangeRequest().send(pathname, port, host, contentLength);
		
		//create a arraylist that stores all the threads
		ArrayList<Thread> threads = new ArrayList<Thread>();
		
		//get number of connections needed
		int numConnection = getConn();
		
		//if only one connection use a single thread
		if(numConnection == 1)
		{
			Thread t1 = new Thread(new concurrentConnection(pathname, host, contentLength, socket));
			t1.start();
		}
		//else create multiple threads
		else
		{
			//create one thread for each connection
			for(int i = 0; i < numConnection; i++)
			{
				Thread t = new Thread(new concurrentConnection(pathname, host, contentLength, socket));
				threads.add(t);
				//t.start();
			}
		}
		
		
	}
}
