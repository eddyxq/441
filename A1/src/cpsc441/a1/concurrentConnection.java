package cpsc441.a1;

import java.net.Socket;

public class concurrentConnection implements Runnable 
{
	String URL;
	int start;
	int offSet;
	String host;
	int port;
	String pathname;
	int contentLength;
	
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
	
	public void run()
	{
		//send range request
		Socket socket = new RangeRequest().send(pathname, port, host, contentLength);
		
		//download file
		FileDownloader http = new FileDownloader();
		http.download(pathname, host, contentLength, socket, start, offSet);
	}
}
