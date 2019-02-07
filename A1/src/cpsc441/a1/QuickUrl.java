package cpsc441.a1;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

public class QuickUrl extends ConcurrentHttp 
{
	public void getObject(String s) 
	{
		//parse URL into host, port and path name
		String[] parsedURL = new Parser().parseURL(s);
		String host = parsedURL[0];
		int port = Integer.parseInt(parsedURL[1]);
		String pathname = parsedURL[2];
		
		//set up head request
		String requestLine_1 = "HEAD " + pathname + " HTTP/1.1\r\n";
		String requestLine_2 = "Host: " + host + "\r\n";
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
			System.out.println("error");
		}	
		
		//calculate start and end 
		String rangeStart = "0";
		String rangeEnd = (contentLength - 1) + "";
		
		//set up range request
		requestLine_1 = "GET " + pathname + " HTTP/1.1\r\n";
		requestLine_2 = "Host: " + host + "\r\n";
		String requestLine_3 = "Range: " + "bytes=" + rangeStart + "-" + rangeEnd + "\r\n";
		eoh_line = "\r\n"; 

		//use socket to communicate
		try 
		{
			Socket socket = new Socket(host, port);
			
			//send range request
			String http_header = requestLine_1 + requestLine_2 + requestLine_3 + eoh_line;
			byte[] http_header_in_bytes = http_header.getBytes("US-ASCII");
			socket.getOutputStream().write(http_header_in_bytes);
 			socket.getOutputStream().flush();
			
 			//read response
 			byte[] responseBytes = new byte[32*2048];
 			byte[] webObject = new byte[32*2048];
 			
 			int count = 0;
 			int num_byte_read = 0;
 			String response = "";
 			
 			//read response
 			while((num_byte_read!= -1) && !(response.contains("\r\n\r\n")))
 			{
				socket.getInputStream().read(responseBytes, count, 1);
				count +=1;
				response = new String(responseBytes, 0, count,"US-ASCII");
			}
 			
 			//create file directory
 			File file = new File(host + "/" + pathname);
			file.getParentFile().mkdirs();
			FileOutputStream outStream = new FileOutputStream(file);
 			
 			int counter = 0;
 			while((num_byte_read!= -1) && !(counter == 1999))
 			{
				num_byte_read = socket.getInputStream().read(webObject);
				outStream.write(webObject, 0, num_byte_read);
				counter = counter + num_byte_read;
				outStream.flush();
			}
 			

 			String responseString = new String(webObject, "UTF-8");
			System.out.println(responseString);
		} 
		catch (IOException e) 
		{
			
		}
	}
}
