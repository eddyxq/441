package cpsc441.a1;

import java.io.IOException;
import java.net.Socket;

public class RangeRequest 
{
	public Socket send(String pathname, int port, String host, int contentLength)
	{
		//calculate start and end 
		String rangeStart = "0";
		String rangeEnd = (contentLength - 1) + "";
		
		//set up range request
		String requestLine_1 = "GET " + pathname + " HTTP/1.1\r\n";
		String requestLine_2 = "Host: " + host + "\r\n";
		String requestLine_3 = "Range: " + "bytes=" + rangeStart + "-" + rangeEnd + "\r\n";
		String eoh_line = "\r\n"; 

		//use socket to communicate
		Socket socket = null;
		try 
		{
			socket = new Socket(host, port);
			//send range request
			String http_header = requestLine_1 + requestLine_2 + requestLine_3 + eoh_line;
			byte[] http_header_in_bytes = http_header.getBytes("US-ASCII");
			socket.getOutputStream().write(http_header_in_bytes);
 			socket.getOutputStream().flush();
		} 
		catch (IOException e) 
		{
			System.out.println("Error");
		}
		return socket;
	}
}

