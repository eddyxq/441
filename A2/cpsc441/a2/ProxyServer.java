package cpsc441.a2;

import java.net.Socket;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * This class is child proxy thread that is spawned by the main thread
 */
public class ProxyServer extends Thread 
{
    private Socket clientSocket;

    public ProxyServer(Socket clientSocket) 
    {
        this.clientSocket = clientSocket;
    }

    public void run() 
    {
        try 
        {
        	//create a socket to connect with host
        	Socket hostSocket = new Socket("people.ucalgary.ca", 80);
            //initialize array
            byte[] byteContent = new byte[2048*2048];
         	//set up streams for client
            InputStream clientInput = clientSocket.getInputStream();
            OutputStream clientOutput = clientSocket.getOutputStream();
            //set up stream for host
            OutputStream hostOutput = hostSocket.getOutputStream();     
            InputStream hostInput = hostSocket.getInputStream();
            
            int count = clientInput.read(byteContent);
            //check if something is read
            if (count != -1) 
            {
                hostOutput.write(byteContent, 0, count);
                //relay the response
                for (int length; (length = hostInput.read(byteContent)) != -1;) 
                {
                    clientOutput.write(byteContent, 0, length);
                }
            } 
            //close all the streams and sockets when finished
            clientOutput.close();
            clientInput.close();
            hostInput.close();
            hostOutput.close();
            hostSocket.close();
            hostSocket.close();
            clientSocket.close();
        } 
        catch (Exception e) 
        {
            System.out.println("Proxy Error");
        } 
    }
}