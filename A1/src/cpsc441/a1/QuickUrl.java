package cpsc441.a1;

import java.io.File;

/*
 * This class will get objects from a given url
 */

public class QuickUrl extends ConcurrentHttp 
{
	/*
	 * This method will get the object
	 * @param url The url
	 */
	public void getObject(String url) 
	{
		String[] parsedURL = null;
		String host = null;
		int port = 0;
		String pathname = null;
		
		try
		{
			//parse URL into host, port, and pathname
			parsedURL = new Parser().parseURL(url);
			host = parsedURL[0];
			port = Integer.parseInt(parsedURL[1]);
			pathname = parsedURL[2];
		}
		catch (Exception e)
		{
			System.out.println("Invalid URL");
		}
		
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
			System.out.println("File Already Exist or URL is invalid");
			System.out.println("Remove any existing file and run program again");
		}
	}
	
	/*
	 * This method returns true if the specified file already exists
	 * @param pathname The path name
	 */
	private boolean fileExist(String pathname)
	{
		try
		{
			//extract file name from the end of the url
			String[] urlParts = pathname.split("/");
			String filename = urlParts[urlParts.length-1];
			File file = new File(filename);
			return file.exists();
		}
		catch (Exception e)
		{
			System.out.println("URL Error");
			System.out.println("Enter a valid URL");
		}
		return true;
	}
}
