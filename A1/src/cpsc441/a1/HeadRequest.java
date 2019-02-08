package cpsc441.a1;

import java.io.IOException;
import java.net.Socket;

public class HeadRequest 
{
	public int getContentLength(String pathname, int port, String host) 
	{
		//set up head request
		String requestLine_1 = "HEAD " + pathname + " HTTP/1.1\r\n";
		String requestLine_2 = "Host: " + host + ":" + port + "\r\n";
		String eoh_line = "\r\n";
		
		//initialize content length
		int contentLength = 0;
		
		//use socket to communicate
		try 
		{
			Socket socket = new Socket(host, port);
 		
			//send head request
			String http_header = requestLine_1 + requestLine_2 + eoh_line;
			byte[] http_header_in_bytes = http_header.getBytes("US-ASCII");
			socket.getOutputStream().write(http_header_in_bytes);
 			socket.getOutputStream().flush();
			
 			//read response
 			byte[] responseBytes= new byte[2048];
 			socket.getInputStream().read(responseBytes);
 			String responseString = new String(responseBytes, "UTF-8");
 			
 			//do nothing if file not found
 			if(responseString.contains("404 NOT FOUND"))
			{
 				System.out.println("NOT FOUND");
			}
 			//look for byte length
 			else if(responseString.contains("200 OK"))
 			{
	 			if (responseString.contains("Accept-Ranges: bytes"))
	 			{
	 				//parse response to get content length
	 				String[] responses = responseString.split("\n", 8);
	 				//loop through the response lines
		 			for (String line : responses) 
		 			{
			 			if (line.contains("Content-Length:"))
			 			{
			 				//Find the length of the file in bytes
			 				String[] content = line.split(": ", 2);
			 				//remove last character to get just the numbers
			 				String length = content[1].substring(0, content[1].length()-1);
			 				//convert from string to integer
			 				contentLength = Integer.parseInt(length);
			 			}
		 			}
	 			}
 			}
 			//does not support range request
 			else
 			{
	 			System.out.println("Server does not support range requests.");
	 			System.exit(0);
 			}
 			socket.close();
		} 
		catch (IOException e) 
		{
			System.out.println("Head Request Error");
		}	
		return contentLength;
	}
}
