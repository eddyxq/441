package cpsc441.a1;

import java.io.IOException;
import java.net.Socket;

/*
 * This class is used to send range requests
 */

public class RangeRequest 
{
	/*
	 * This method will send a range request and return the socket
	 * @param pathname The path name
	 * @param port The port number
	 * @param host The host name
	 * @param contentLength The size of the object
	 * @param start The starting index
	 * @param offSet The index offset
	 */
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
