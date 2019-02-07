package cpsc441.a1;

import java.util.ArrayList;

public class QuickUrl extends ConcurrentHttp 
{
	public void getObject(String s) 
	{
		//arraylist that stores all the threads
		ArrayList<Thread> threads = new ArrayList<Thread>();
		
		//get number of connections needed
		int numConnection = getConn();
		
		//if only one connection use a single thread
		if(numConnection == 1)
		{
			Thread t1 = new Thread(new concurrentConnection(s));
			t1.start();
		}
		//else create multiple threads
		else
		{
			//create one thread for each connection
			for(int i = 0; i < numConnection; i++)
			{
				Thread t = new Thread(new concurrentConnection(s));
				threads.add(t);
				//t.start();
			}
		}
		
		
	}
}
