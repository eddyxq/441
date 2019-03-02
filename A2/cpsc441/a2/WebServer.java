package cpsc441.a2;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Scanner;

/**
 * This class is child worker thread that is spawned by the main thread
 */
public class WebServer extends Thread
{
	private Socket socket;
	private Scanner inputStream;
	private String start;
	private String end;
	private String[] parts;
	
	public WebServer(Socket socket)
	{
		this.socket = socket;
	}
	
	public void run()
	{
		try 
		{
			//create stream to get these requests
			inputStream = new Scanner(new InputStreamReader(socket.getInputStream()));
			//get request line
			String firstLine = inputStream.nextLine();
			//parse the request line
			parts = firstLine.split(" ", 3);
			//get host line
			String secondLine = inputStream.nextLine();
			
			if(!secondLine.contains("Host: "))
			{
				//go to proxy mode
			}
			//else web server mode
			
			//ensure well-formed request
			//this section handles head requests
			if (parts[0].equals("HEAD"))
			{
				//get the file name
				String pathname = parts[1];
				//extract file name from the end of the url
				String[] urlParts = pathname.split("/");
				String filename = urlParts[urlParts.length-1];
				//determine if requested object exists
				if(fileExist(pathname))
				{
					//get the header portion of the response to send 
					File file = new File(filename);
	 				String headerResponse = getHeadRequestResponse(file);
	 				//convert to bytes so it can be sent through the socket
	 				byte[] response_in_bytes = headerResponse.getBytes("US-ASCII");
	 				//send the response to client
	 				socket.getOutputStream().write(response_in_bytes);
				}
				else
				{
					//send not found
					String errorResponse = get404NotFoundResponse();
					byte[] response_in_bytes = errorResponse.getBytes("US-ASCII");
					socket.getOutputStream().write(response_in_bytes);
	 				socket.getOutputStream().flush();
				}
			}
			//this section handles range requests
			else if (parts[0].equals("GET"))
			{
				//get the byte range information
				String thirdLine = inputStream.nextLine();
				//parse to get the starting and ending byte indices
				String[] rangeParts = thirdLine.split("=");
				String[] byteRange = rangeParts[1].split("-");
				start = byteRange[0];
				end = byteRange[1];
				
				//extract file name from the end of the pathname/url
				String pathname = parts[1];
				String[] urlParts = pathname.split("/");
				String filename = urlParts[urlParts.length-1];
					
				if(fileExist(pathname))
				{
					//get file contents
					File file = new File(filename);
	 				byte[] fileContent = getFileBytes(file);
					
	 				//get the header portion of the response to send 
	 				String rangeResponse = getRangeRequestResponse(file);
	 				//convert to bytes so it can be sent through the socket
	 				byte[] response_in_bytes = rangeResponse.getBytes("US-ASCII");
	 				//create a sub array with the specified start and end range
	 				byte[] subarray = new byte[Integer.parseInt(end) - Integer.parseInt(start) + 1];
	 				System.arraycopy(fileContent, Integer.parseInt(start), subarray, 0, subarray.length);
	 				//send the header and object to the client
	 				socket.getOutputStream().write(response_in_bytes);
	 				socket.getOutputStream().write(subarray);
	 				socket.getOutputStream().flush();
				}
				else
				{
					//send not found
					String errorResponse = get404NotFoundResponse();
					byte[] response_in_bytes = errorResponse.getBytes("US-ASCII");
					socket.getOutputStream().write(response_in_bytes);
	 				socket.getOutputStream().flush();
				}
			}
			else
			{
				//send bad request
				String errorResponse = get400BadRequestResponse();
				byte[] response_in_bytes = errorResponse.getBytes("US-ASCII");
				socket.getOutputStream().write(response_in_bytes);
 				socket.getOutputStream().flush();
			}
		} 
		catch (IOException e) 
		{
			System.out.println("Server Error");
		}
		finally 
		{
			//close the socket
            try 
			{
            	socket.close();
            } 
			catch (IOException e) 
			{
                e.printStackTrace();
			}
		}
	}
	/*
	 * This method returns true if the specified file already exists
	 * @param pathname The path name
	 */
	private boolean fileExist(String pathname)
	{
		try
		{
			//extract file name from the end of the url
			String[] urlParts = pathname.split("/");
			String filename = urlParts[urlParts.length-1];
			File file = new File(filename);
			return file.exists();
		}
		catch (Exception e)
		{
			System.out.println("URL Error");
			System.out.println("Enter a valid URL");
		}
		return true;
	}
	/*
	 * This method returns a head response
	 * @param file The file
	 */
	private String getHeadRequestResponse(File file)
	{
		String line1 = "HTTP/1.1 200 OK\r\n";
		String line2 = "Date: " + Utils.getCurrentDate() + "\r\n";
		String line3 = "Server: Apache/2.4.6 (Red Hat Enterprise Linux) OpenSSL/1.0.2k-fips\r\n";
		String line4 = "Last-Modified: " + Utils.getLastModified(file) + "\r\n";
		String line5 = "ETag: \"7cf-57fb4e433e3c0\"\r\n";
		String line6 = "Accept-Ranges: bytes\r\n";
		String line7 = "Content-Length: " + file.length() + "\r\n";
		String line8 = "\r\n";
		try 
		{
			line8 = "Content-Type: " + Utils.getContentType(file) + "\r\n";
		} 
		catch (IOException e1) 
		{
			System.out.println("error determining content type");
		}
		String end = "Connection: close\r\n\r\n";	 				
		String headerResponse = line1 + line2 + line3 + line4 + line5 + line6 + line7 + line8 + end;
		
		System.out.println(headerResponse);
		return headerResponse;
	}
	/*
	 * This method returns a range response
	 * @param file The file
	 */
	private String getRangeRequestResponse(File file)
	{
		long fileLength = file.length();
		long l = fileLength - 1;
		
		String line1 = "HTTP/1.1 206 Partial Content\r\n";
		String line2 = "Date: " + Utils.getCurrentDate() + "\r\n";
		String line3 = "Server: Apache/2.4.6 (Red Hat Enterprise Linux) OpenSSL/1.0.2k-fips\r\n";
		String line4 = "Last-Modified: " + Utils.getLastModified(file) + "\r\n";
		String line5 = "ETag: \"7cf-57fb4e433e3c0\"\r\n";
		String line6 = "Accept-Ranges: bytes\r\n";
		String line7 = "Content-Length: " + file.length() + "\r\n";
		String line8 = "\r\n";
		try 
		{
			line8 = "Content-Type: " + Utils.getContentType(file) + "\r\n";
		} 
		catch (IOException e1) 
		{
			System.out.println("error determining content type");
		}
		String line9 = "Content-Range: bytes 0-" + l + "/" + file.length() + "\r\n";
		String end = "Connection: close\r\n\r\n";	 				
		String rangeResponse = line1 + line2 + line3 + line4 + line5 + line6 + line7 + line8 + line9 + end;
		
		System.out.println(rangeResponse);
		return rangeResponse;
	}
	/*
	 * This method returns a bad request response
	 */
	private String get400BadRequestResponse()
	{
		String line1 = "HTTP/1.1 400 Bad Request Found\r\n";
		String line2 = "Connection: close\r\n";
		String errorResponse = line1 + line2;
		return errorResponse;
	}
	/*
	 * This method returns a not found response
	 */
	private String get404NotFoundResponse()
	{
		String line1 = "HTTP/1.1 404 Not Found\r\n";
		String line2 = "Connection: close\r\n";
		String errorResponse = line1 + line2;
		return errorResponse;
	}
	/*
	 * This method returns the file size
	 * @param file The file
	 */
	private byte[] getFileBytes(File file)
	{
		byte[] fileContent = null;
		//open the file
		FileInputStream fin;
		try 
		{
			fin = new FileInputStream(file);
			long length = file.length();
			fileContent = new byte[(int)file.length()];
			//read file into array
			fin.read(fileContent);
			fin.close();
		} 
		catch (IOException e) 
		{
			System.out.println("File Error");
		}
		return fileContent;
	}
}
