package cpsc441.a1;

import java.io.IOException;
import java.net.Socket;

public class RangeRequest 
{
	public Socket send(String pathname, int port, String host, int contentLength, int start, int offSet)
	{		
		Socket socket = null;
		
		//set up range request
		String requestLine_1 = "GET " + pathname + " HTTP/1.1\r\n";
		String requestLine_2 = "Host: " + host + ":" + port + "\r\n";
		String requestLine_3 = "Range: " + "bytes=" + start + "-" + offSet + "\r\n";
		String eoh_line = "\r\n"; 

		try 
		{
			//open socket for communication
			socket = new Socket(host, port);
			//send range request
			String http_header = requestLine_1 + requestLine_2 + requestLine_3 + eoh_line;
			byte[] http_header_in_bytes = http_header.getBytes("US-ASCII");
			socket.getOutputStream().write(http_header_in_bytes);
 			socket.getOutputStream().flush();
		} 
		catch (IOException e) 
		{
			System.out.println("Range Request Error");
		}
		return socket;
	}
}

