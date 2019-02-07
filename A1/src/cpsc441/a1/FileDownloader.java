package cpsc441.a1;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

public class FileDownloader 
{
	public void download(String pathname, String host, int contentLength, Socket socket)
	{
		//initialize arrays to store bytes
		byte[] responseBytes = new byte[32*2048];
		byte[] webObject = new byte[32*2048];
		
		try 
		{
			//create file directory
			File file = new File(host + "/" + pathname);
			file.getParentFile().mkdirs();
			FileOutputStream outStream = new FileOutputStream(file);
			
			String response = "";
			//read response
			int totalHeaderBytes = 0;
			//first read the header
			while(!reachedEndOfHeader(response))
			{
				//keep reading in bytes until you reached the end of the header
				socket.getInputStream().read(responseBytes, totalHeaderBytes, 1);
				//keep track of how many bytes read thus far
				totalHeaderBytes += 1;
				//response is converted to a string so we can look for the end of the header
				response = new String(responseBytes, 0, totalHeaderBytes,"US-ASCII");
			}
	
			int totalObjectBytes = 0;
			int numberOfBytes = 0;
			//next write the object to file
			while((totalObjectBytes != contentLength))
			{
				//keep reading until all bytes have been read
				numberOfBytes = socket.getInputStream().read(webObject);
				//write bytes to file
				outStream.write(webObject, 0, numberOfBytes);
				//keep track of how many bytes read thus far
				totalObjectBytes += numberOfBytes;
			}
			outStream.flush();
			outStream.close();
		}
		catch (IOException e) 
		{
			System.out.println("Download Error");
		}
		
	}
	//this method returns true if you have reached end of header
	private boolean reachedEndOfHeader(String response) 
	{
		return response.contains("\r\n\r\n") ? true : false;
	}
}
