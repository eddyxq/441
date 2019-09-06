package cpsc441.a1;

import java.net.Socket;

/*
 * This class creates a connection
 */

public class concurrentConnection implements Runnable 
{
	String URL;
	int start;
	int offSet;
	String host;
	int port;
	String pathname;
	int contentLength;
	
	/*
	 * This constructor initializes all the fields
	 * @param host The host name
	 * @param port The port number
	 * @param pathname The path name
	 * @param s The URL 
	 * @param start The starting index
	 * @param offSet The index offset
	 * @param contentLength The size of object
	 */
	public concurrentConnection(String host, int port, String pathname, String s, int start, int offSet, int contentLength)
	{
		this.URL = s;
		this.start = start;
		this.offSet = offSet;
		this.host = host;
		this.port = port;
		this.pathname = pathname;
		this.contentLength = contentLength;
	}
	
	/*
	 * This method executes when a thread is started
	 */
	public void run()
	{
		//send range request
		Socket socket = new RangeRequest().send(pathname, port, host, contentLength, start, offSet);
		
		//download file
		FileDownloader http = new FileDownloader();
		http.download(pathname, host, contentLength, socket, start, offSet);
	}
}
