package cpsc441.a1;

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
		
		//send head request to get content length
		int contentLength = new HeadRequest().getContentLength(pathname, port, host);
		
		//arraylist that stores all the threads
		ArrayList<Thread> threads = new ArrayList<Thread>();
		
		//get number of connections needed
		int numConnection = getConn();
		
		//calculate number of bytes per thread
		int rangeLen = contentLength/numConnection;
		
		//starting and offset values for bytes
		int start = 0; 
		int offSet = rangeLen -1;
		
		//if only one connection use a single thread
		if(numConnection == 1)
		{
			Thread t1 = new Thread(new concurrentConnection(host, port, pathname, s, start, offSet, contentLength));
			t1.start();
		}
		//else create multiple threads
		else
		{
			//create one thread for each connection
			for(int i = 0; i < numConnection; i++)
			{
				Thread t = new Thread(new concurrentConnection(host, port, pathname, s, start, offSet, contentLength));
				threads.add(t);
				t.start();

				try 
				{
					t.join();
				} 
				catch (InterruptedException e) 
				{
					e.printStackTrace();
				}
				
				start += rangeLen;
				offSet += rangeLen;
			}
		}
	}
}
