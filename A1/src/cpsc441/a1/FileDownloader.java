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
			//extract file name from the end of the url
			String[] urlParts = pathname.split("/");
			String filename = urlParts[urlParts.length-1];
			
			//create file 
			FileOutputStream outStream = new FileOutputStream(new File(filename));
			
			//initialize response string
			String response = "";
			int totalHeaderBytes = 0;
			//read the header
			while(!response.contains("\r\n\r\n"))
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
			//read the payload
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
}
