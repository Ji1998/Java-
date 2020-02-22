import java.io.*;
import java.net.*;
import java.util.*;

public class President implements Runnable
{
	WhiteHouse whiteHouse; 

	public President(WhiteHouse whiteHouse)
	{
		// TODO Auto-generated constructor stub

		this.whiteHouse = whiteHouse; 
		new Thread(this).start();



	}


	public void run()
	{
		InputStreamReader isr = new InputStreamReader(System.in);
		BufferedReader keyboard = new BufferedReader(isr);
		System.out.println("enter a statement @Guangsen Ji 2020");
		try {	
			while(true)
			{

				String item = keyboard.readLine();
				String ClearItem = item.trim();
				if (ClearItem.length() == 0) continue;
				else 
				{	//System.out.println(item);
					whiteHouse.makeAstatement(item);
					if(item.equalsIgnoreCase("God Bless American"))
					{
						break;
					}
				}


			}
			System.out.println("The News Conference is over");

		}
		catch(Exception e)
		{
		}
	}





	}
