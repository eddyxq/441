package cpsc441.a1;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

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
		
		int contentLength = 0;
		
		try 
		{
			Socket socket = new Socket(host, port);
 		
			//send header request
			String http_header = requestLine_1 + requestLine_2 + eoh_line;
			byte[] http_header_in_bytes = http_header.getBytes("US-ASCII");
			socket.getOutputStream().write(http_header_in_bytes);
 			socket.getOutputStream().flush();
			
 			//read response
 			byte[] responseBytes= new byte[2048];
 			int count;
 			socket.getInputStream().read(responseBytes);
 			
 			String responseString = new String(responseBytes, "UTF-8");
			System.out.println(responseString);
			System.out.println("--------------------------------------------");
 			
 			if(responseString.contains("404 NOT FOUND"))
			{
 				//add logic to handle
			}
 			else if(responseString.contains("200 OK"))
 			{
	 			if (responseString.contains("Accept-Ranges: bytes"))
	 			{
	 				//parse response to get content length
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
		
		
		
		String rangeStart = "0";
		String rangeOffSet = (contentLength - 1) + "";
		
		requestLine_1 = "GET " + pathname + " HTTP/1.1\r\n";
		requestLine_2 = "Host: " + host + "\r\n";
		String requestLine_3 = "Range: " + "bytes=" + rangeStart + "-" + rangeOffSet + "\r\n";
		eoh_line = "\r\n"; 

//		System.out.println(requestLine_1);
//		System.out.println(requestLine_2);
//		System.out.println(requestLine_3);
		
		//send range request
		
		try 
		{
			Socket socket = new Socket(host, port);
			
			String http_header = requestLine_1 + requestLine_2 + requestLine_3 + eoh_line;
			byte[] http_header_in_bytes = http_header.getBytes("US-ASCII");
			socket.getOutputStream().write(http_header_in_bytes);
 			socket.getOutputStream().flush();
			
 			//read response
 			byte[] http_response_header_bytes= new byte[2048];
 			byte[] http_object_byte = new byte[1024];
 			int count;
 			
 			//socket.getInputStream().read(responseBytes);
 			
// 			while((count = socket.getInputStream().read(responseBytes)) > -1)
// 			{
// 				//not sure what to do here
// 			}
 			int off = 0;
 			int num_byte_read = 0;
 			String http_response_header_string = null;
 			while(num_byte_read!= -1)
 			{

				socket.getInputStream().read(http_response_header_bytes, off, 1);
				off++;
				http_response_header_string = new String(http_response_header_bytes, 0, off,"US-ASCII");
				if(http_response_header_string.contains("\r\n\r\n")){
					break;
				}
			}
 			
 			File file = new File(host+"/" +pathname);
			file.getParentFile().mkdirs();
			FileOutputStream outStream = new FileOutputStream(file);
 			
 			int counter = 0;
 			while(num_byte_read!= -1){
				if(counter == 1999){
					break;
				}
				num_byte_read = socket.getInputStream().read(http_object_byte);
				outStream.write(http_object_byte, 0, num_byte_read);
				counter = counter + num_byte_read;
				outStream.flush();
			}
 			
 			
 			
 			String responseString = new String(http_object_byte, "UTF-8");
			System.out.println(responseString);
			
			int headerIndex = responseString.indexOf("\r\n\r\n");
			
		    FileOutputStream outfile = new FileOutputStream("download.txt");
		   // outfile.write(http_response_header_bytes, headerIndex, 1999);
		    outfile.close();
			
			
			
			
			
		} 
		catch (IOException e) 
		{
			
		}
		
	}
}
