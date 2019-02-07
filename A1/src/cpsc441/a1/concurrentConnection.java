package cpsc441.a1;

import java.net.Socket;

public class concurrentConnection implements Runnable 
{	
	String pathname;
	String host;
	int contentLength;
	Socket socket;
	
	public concurrentConnection(String pathname, String host, int contentLength, Socket socket)
	{
		this.pathname = pathname;
		this.host = host;
		this.contentLength = contentLength;
		this.socket = socket;
	}
	
	public void run()
	{
		//download file
		FileDownloader http = new FileDownloader();
		http.download(pathname, host, contentLength, socket);
	}
}
