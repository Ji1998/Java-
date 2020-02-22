
public class Reporter implements Runnable
{

	String     topicOfInterest;
	WhiteHouse whiteHouse;





	public Reporter(WhiteHouse whiteHouse, String topicOfInterest)
	{
		// TODO Auto-generated constructor stub

		this.whiteHouse      = whiteHouse; //copy local var from stack to program var
		this.topicOfInterest = topicOfInterest; 
		new Thread(this).start();





	}
	public void run()
	{

		String presidentsStatement = whiteHouse.attendTheNewsConference(topicOfInterest);
		System.out.println("A Reporter has returned from the news conference. Reporter's topic-of-interest was: "
				+ topicOfInterest + ". President's statement was: " + presidentsStatement);

	}






	

}
