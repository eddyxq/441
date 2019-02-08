package cpsc441.a1;

public class Parser 
{
	public String[] parseURL(String s) 
	{
		//remove http://
		String url = s.replace("http://", "");
		
		//split url and its file path
		String [] parts = url.split("/", 2);
		
		
		
		String [] hostandport;
		String host;
		String port;
		String filepath;
		
		//if the url has a specified a port number
		if(parts[0].contains(":"))
		{ 
			//split host and port number
			hostandport = parts[0].split(":", 2);
			
			//first part is host
			host = hostandport[0];
			
			//second part is port number
			port = hostandport[1];
			
			//file path is the second part in url
			filepath = "/" + parts[1];
		}
		//if the url has not a specified a port number
		else
		{ 
			//first part is host
			host = parts[0];
			
			// set default port number to 80
			port = "80"; 
			
			//file path is the second part in url
			filepath = "/" + parts[1];
		}
		
		String[] parsedResults = {host, port, filepath};
		
		return parsedResults;
	}
}
