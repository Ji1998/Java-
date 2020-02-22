
public class StartTheNewsConference
{

	
	public static void main(String[] args)
	{
		WhiteHouse WH = new WhiteHouse();
		
		
		if(args.length == 0)
		{
			System.out.println("ENTER INTERESTS @Guansgen Ji 2020");
			return;
		}
		for(String item:args)
		{
			 new Reporter(WH, item);
		}
		
		 new President(WH);
		
		
	}

}
