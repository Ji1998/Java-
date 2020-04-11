public class AccountTester // The TEST program for the ACCOUNT class
{
public static void main(String[] args) throws Exception
  {
  CheckingAccount ca1 = new CheckingAccount();
  ca1.setCustomerName("Smith,Bubba");
  System.out.println(ca1.getClass().getName() + " " + ca1.getAccountNumber() + " " + ca1.getCustomerName());
 // ca1.deposit(2000);

  // Now create a 2nd account, but this time use a constructor
  // to set the customerName field and toString() to get it.
  CashAccount ca2 = new CheckingAccount("Boop,Betty");
  System.out.println(ca2); // call toString() on a2
  System.out.println(ca2.getAccountNumber());
  //ca2.addInterest(1);
  }
}

