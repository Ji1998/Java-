import java.math.BigDecimal;
import java.math.MathContext;

public abstract class CashAccount extends Account
{

	private double balance;

	public CashAccount() throws Exception
	{
		super(); // call the parent constructor with no argument
	}

	public CashAccount(String customerName) throws Exception
	{
		super(customerName);
		// call the parent constructor that takes a String argument
	}

	public  synchronized  void deposit(double amount) throws IllegalArgumentException
	{
		if (amount < 0)
			throw new IllegalArgumentException("Deposit amount must be positive.");

		balance += amount; // add to the balance	
	}


	public synchronized  void withdraw(double amount) throws IllegalArgumentException, OverdraftException
	  {
	  if (amount < 0) 
	      throw new IllegalArgumentException("withdraw amount must be positive");
	  if (amount > balance) 
	      throw new OverdraftException("Withdraw of $" + amount 
	    		                 + " denied in account #" + getAccountNumber()
	    		                 + " insufficient funds.");
	  balance -= amount;	
	  
	  }
	@Override
	public String toString()
	  {
	  BigDecimal balanceBD = new BigDecimal(balance, MathContext.DECIMAL32);//set PRECISION to 7 digits
	  balanceBD = balanceBD.setScale(2,BigDecimal.ROUND_DOWN); //SCALE is # of digits after the period
	  String dajiba = "Balance is" + balanceBD;
	  return super.toString() + dajiba; // tack my field values onto parent field values!
	  }


	public double getBalance()
	{
		return balance;
	}

}
