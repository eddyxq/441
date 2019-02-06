package cpsc441.a1;

import java.io.IOException;
import java.net.Socket;

public class QuickUrl extends ConcurrentHttp 
{

	public void getObject(String s) 
	{
		String[] parsedURL = new Parser().parseURL(s);
		String host = parsedURL[0];
		int port = Integer.parseInt(parsedURL[1]);
		String pathname = parsedURL[2];
		

		String requestLine_1 = "HEAD " + pathname + " HTTP/1.1\r\n";
		String requestLine_2 = "Host: " + host + "\r\n";
		String eoh_line = "\r\n";
		
		int contentLength;
		
		try 
		{
			Socket socket = new Socket(host, port);
 		
			String http_header = requestLine_1 + requestLine_2 + eoh_line;
			byte[] http_header_in_bytes = http_header.getBytes("US-ASCII");
			socket.getOutputStream().write(http_header_in_bytes);
 			socket.getOutputStream().flush();
			
 			
 			byte[] responseBytes= new byte[2048];
 			int count;
 			while((count = socket.getInputStream().read(responseBytes)) > -1)
 			{
 				
 			}
 			String responseString = new String(responseBytes, "UTF-8");
			System.out.println(responseString);
 			
 			
 			if(responseString.contains("404 NOT FOUND"))
			{
 				// add logic to handle
			}
 			else if(responseString.contains("200 OK"))
 			{
	 			if (responseString.contains("Accept-Ranges: bytes"))// || !range)
	 			{
	 				String [] responses = responseString.split("\n", 8);
	 				
		 			for (String line : responses) 
		 			{
			 			if (line.contains("Content-Length:"))
			 			{
			 				//Find the length of the file in bytes
			 				String [] content = line.split(": ", 2);
			 				//remove last character to get just the numbers
			 				String length = content[1].substring(0, content[1].length()-1);
			 				//convert from string to integer
			 				contentLength = Integer.parseInt(length);
			 				//System.out.println(contentLength);
			 			}
		 			}
	 			}
 			}
 			else
 			{
	 			//Does not support range request
	 			System.out.println("Error: The server does not support range request!");
	 			System.exit(0);
 			}
 			socket.close();
		} 
		catch (IOException e) 
		{
			System.out.println("error");
		}	
	}
}
