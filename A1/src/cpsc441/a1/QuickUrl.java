package cpsc441.a1;

import java.net.Socket;

public class QuickUrl extends ConcurrentHttp 
{
	public void getObject(String s) 
	{
		//parse URL into host, port, and pathname
		String[] parsedURL = new Parser().parseURL(s);
		String host = parsedURL[0];
		int port = Integer.parseInt(parsedURL[1]);
		String pathname = parsedURL[2];
		
		//send head request
		int contentLength = new HeadRequest().getContentLength(pathname, port, host);
		
		//send range request
		Socket socket = new RangeRequest().send(pathname, port, host, contentLength);
		
		//download file
		FileDownloader http = new FileDownloader();
		http.download(pathname, host, contentLength, socket);
	}
}
