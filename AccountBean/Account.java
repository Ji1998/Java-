import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Account implements Serializable
{
	//Things to do when writing BEAN class
	//1. declare the data fields (all private)
	//2. provide a getter and setter method for each field.
	//3. The bean will be used by ALL application programs
	
		
	 private String customerName;  
	 private int accountNumber;
	 private static int lastAccountNumber; // memory "cache" of the next account # to be assigned to an Account. 	
	
	 
		private static synchronized int getNextAccountNumber() throws Exception
		{
		   if (lastAccountNumber == 0)
		      {
		      // initialize lastAccountNumber from "DB" (***COMMENT OUT CODE BELOW FOR FIRST EXECUTION ONLY***)
		      ObjectInputStream ois = new ObjectInputStream(new FileInputStream("LastAccountNumber.ser"));
		       lastAccountNumber = (int) ois.readObject();
		       ois.close();
		      }
		   lastAccountNumber++; // bump last number for new account
		   // write the updated lastAccountNumber to the "DB":
		   ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("LastAccountNumber.ser"));
		   oos.writeObject(lastAccountNumber);
		   oos.close();
		   return lastAccountNumber;
		
		}
	
	public Account() throws Exception // the "default" (no-parms) constructor
	  {
		 System.out.println("Executing code in the Account default constructor to assign an account number.");
		
		            // the JVM has just been restarted!s
		      // initialize lastAccountNumber in STATIC from lastAccountNumber on data base (only done once)
			accountNumber = getNextAccountNumber(); 
		// call static method
		System.out.println(accountNumber);
		System.out.println(lastAccountNumber);
	  }
	
	
	
	

	
	
	
	public Account(String customerName) throws Exception // another "overloaded" constructor
	  {
		this();
	  setCustomerName(customerName);    // call our own setter method! 	
	  }

	
	public String getCustomerName() // And these are the STARTER versions of the
	   {                            // getter & setter methods for the customerName field.
	   return customerName;	        // These initial versions don't add any function, they just provide access 
	   }                            // to the field. We will ADD EDIT/AUTHORIZATION code as appropriate!  
	public void setCustomerName(String customerName)
	   {
	   this.customerName = customerName; // Note an instance variable (a program object field) and a local variable	
	   } 
	
	
	public int getAccountNumber()
	  {
	  return accountNumber;
	  }
	  
	
	
	public String toString() // the "introduce yourself" method
	  {
		return getClass().getName() + " #" + getAccountNumber() + " for " + getCustomerName(); 
	  }  
	
	
	

}
