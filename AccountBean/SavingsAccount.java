
public class SavingsAccount extends CashAccount
{

	private double interestReceived;
	
	
	public SavingsAccount() throws Exception
	{
		// TODO Auto-generated constructor stub
	}

	public SavingsAccount(String customerName) throws Exception
	{
		super(customerName);
		// TODO Auto-generated constructor stub
	}

	public double getInterestReceived()
	{
		return interestReceived;
	}
	public void addInterest(double interest) throws IllegalArgumentException
	{
		if (interest < 0)
			throw new IllegalArgumentException("Deposit amount must be positive.");
		
		deposit(interest);
		
	}
	
	public String toString ()
	{
		return super.toString()+"with interest added of " + interestReceived;
	}
	
	
}
