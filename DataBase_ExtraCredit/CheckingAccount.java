
public class CheckingAccount extends CashAccount
{
private double feesPaid;

public CheckingAccount() throws Exception
	{
	super();
	}

public CheckingAccount(String customerName) throws Exception
	{
	super(customerName);
	}

private CheckingAccount(String customerName, int accountNumber, double balance) // added for DB
   {
   super(customerName, accountNumber, balance);	
   }

public static CheckingAccount restoreFromDataBase(String customerName, int accountNumber, double balance) // added for DB
  {
  return new CheckingAccount(customerName, accountNumber, balance);	
  }

@Override
public void deposit(double amount)
  {
  if (amount >= 1000) System.out.println("Calling CrimeWatch.gov $" + amount + " deposit on account #" + getAccountNumber());	
  super.deposit(amount);
  }

public double getFeesPaid()
	{
	return feesPaid;
	}

public void payFee(double fee) throws OverdraftException 
   {
   if (fee < 0) throw new IllegalArgumentException("fee amount must be positive.");
   withdraw(fee);
   feesPaid += fee; // count it only if withdraw was successful!
   }

@Override
public String toString()
  {
  return "Checking" + super.toString() + " fees paid: $" + getFeesPaid();	
  }
}
