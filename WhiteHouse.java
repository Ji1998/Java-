
public class WhiteHouse
{
	String presidentsStatement;



	public synchronized void makeAstatement(String presidentsStatement)
	{
		 this.presidentsStatement = presidentsStatement; //save local method variable in program variable
		 notifyAll(); // wake up waiting Reporter threads
		 System.out.println("notified");
	}


	public synchronized String attendTheNewsConference(String topicOfInterest)
	   {
		
		
		while(true) // do forever
	     {
	    try { wait();} // enter WAIT queue.
	    catch(InterruptedException ie) {}
	    if (presidentsStatement.contains(topicOfInterest)) return presidentsStatement; // leave the news conference
	    if (presidentsStatement.contains("God bless America")) return presidentsStatement;
	   
	     }
		
		
		
	   }


}
