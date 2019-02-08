package cpsc441.a1;

import java.io.File;

public class QuickUrl extends ConcurrentHttp 
{
	public void getObject(String url) 
	{
		//parse URL into host, port, and pathname
		String[] parsedURL = new Parser().parseURL(url);
		String host = parsedURL[0];
		int port = Integer.parseInt(parsedURL[1]);
		String pathname = parsedURL[2];
		
		//send head request to get content length
		int contentLength = new HeadRequest().getContentLength(pathname, port, host);
		
		//get number of connections specified
		int numConnection = getConn();
		
		//calculate the number of bytes per thread
		int rangeLen = contentLength/numConnection;
		
		//starting and offset values for bytes
		int start = 0; 
		int offSet = rangeLen -1;
		
		//start downloading the file if it does not already exist
		if(!fileExist(pathname))
		{
			//create a thread for each connection
			for(int i = 0; i < numConnection; i++)
			{
				//initialize and run the thread
				Thread t = new Thread(new concurrentConnection(host, port, pathname, url, start, offSet, rangeLen));
				t.start();
				//ensure bytes are written in correct order
				try 
				{
					t.join();
				} 
				catch (InterruptedException e) 
				{
					System.out.println("Thread Error");
				}
				//update values for the next thread
				start += rangeLen;
				offSet += rangeLen;
			}
		}
		else
		{
			System.out.println("File Already Exist");
			System.out.println("Remove existing file and run program again");
		}
	}
	
	//this method returns true if the specified file already exists
	private boolean fileExist(String pathname)
	{
		//extract file name from the end of the url
		String[] urlParts = pathname.split("/");
		String filename = urlParts[urlParts.length-1];
		File file = new File(filename);
		return file.exists();
	}
}
