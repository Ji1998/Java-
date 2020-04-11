
public class CheckingAccount extends CashAccount
{


	private double feesPaid;

	public CheckingAccount() throws Exception
	{
		// TODO Auto-generated constructor stub
	}

	public CheckingAccount(String customerName) throws Exception
	{
		super(customerName);
		// TODO Auto-generated constructor stub
	}

	public double getFeesPaid()
	{
		return feesPaid;
	}

	public void chargeFee(double fee) throws IllegalArgumentException, OverdraftException
	{

		if (fee < 0)
			throw new IllegalArgumentException("fee amount must be positive.");

		withdraw(fee);


	}

	public String toString ()
	{
		return super.toString()+"with fees deduction of " + feesPaid ;
	}

	
	@Override
	public  synchronized  void deposit(double amount) throws IllegalArgumentException
	{
		if (amount > 1000)
			throw new IllegalArgumentException("calling CrimeWatch.gov");
		super.deposit(amount);
		
	}


	
	
	
}
