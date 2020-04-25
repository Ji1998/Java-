
public class SavingsAccount extends CashAccount
{
private double interestReceived;

public SavingsAccount() throws Exception
	{
	super();
	}

public SavingsAccount(String customerName) throws Exception
	{
	super(customerName);
	}

private SavingsAccount(String customerName, int accountNumber, double balance) // added for DB
   {
   super(customerName, accountNumber, balance);	
   }

public static SavingsAccount restoreFromDataBase(String customerName, int accountNumber, double balance) // added for DB
   {
   return new SavingsAccount(customerName, accountNumber, balance);	
   }

public double getInterestReceived()
	{
	return interestReceived;
	}

public void addInterest(double interest)
   {
   if (interest < 0) throw new IllegalArgumentException("Interest amount must be positive.");
   deposit(interest);
   interestReceived += interest; // count it only if deposit was successful.
   }

@Override
public String toString()
  {
  return super.toString() + " interestReceived $" + getInterestReceived();	
  }
}
