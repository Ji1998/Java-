import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.UnicastRemoteObject;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;


public class BankServer extends UnicastRemoteObject 
implements TellerServerClientInterface
{

	ConcurrentHashMap<Integer,CashAccount> accounts = new ConcurrentHashMap<Integer,CashAccount>();



	public static void main(String[] args) throws Exception
	{
		new BankServer(); // load program object & call constructor

	}

	public BankServer() throws Exception // CONSTRUCTOR
	{
		super(); // call parent constructor
		LocateRegistry.createRegistry(1099); // start the rmiregistry 
		Naming.rebind("EchoTellerServer", this); // register with rmiregistry
		System.out.println("EchoTellerServer is up at " + InetAddress.getLocalHost().getHostAddress());
		try {	
			FileInputStream fis = new FileInputStream("accounts.ser");
			ObjectInputStream diskOIS = new ObjectInputStream(fis);
			accounts = (ConcurrentHashMap<Integer,CashAccount>) diskOIS.readObject();
			diskOIS.close();
			System.out.println(accounts); // show passwords to tester!

		}
		catch(FileNotFoundException fnfe) // File will not be there first time you test...
		{
			System.out.println("A passwords.ser file was not found on disk, so an empty passwords collection will be used.");
		}
		catch(Exception e)
		{
			System.out.println(e);
		}


	}

	public String createNewAccount(String accountType, String customerName)

	{	CashAccount ca;
	try {
		if (accountType.equals("CHECKING"))
		{
			ca = new CheckingAccount(customerName);
			accounts.put(ca.getAccountNumber(), ca);
			saveAccount();
		}

		else if (accountType.equals("SAVINGS"))
		{
			ca = new SavingsAccount(customerName);
			accounts.put(ca.getAccountNumber(),ca);
			saveAccount();

		}

		else return "ERROR: account type " + accountType
				+ " is not recognized by the server."
				+ " Call the IT department!";



		return ca.toString();
	}
	catch (Exception e)
	{

		return e.toString();

	}

	}

	public synchronized void saveAccount() throws IOException
	{


		FileOutputStream   fos     = new FileOutputStream("accounts.ser"); // .ser file type means serialized object(s)  
		ObjectOutputStream diskOOS = new ObjectOutputStream(fos);
		diskOOS.writeObject(accounts); // write the whole collection to disk!
		diskOOS.close(); // the first line of the method opens the file, this closes it.
		// (It was not opened in "append" mode, so this overwrites the existing file.)


	}





	public String showAccount(int accountNumber, String customerName)

	{
		String	upcus = customerName.toUpperCase();

		if(accountNumber == 0)
		{
			CashAccount[] accountList = accounts.values().toArray(new CashAccount[0]);
			TreeSet <String> hitList = new TreeSet<String>();
			
			for(CashAccount forhitlist: accountList)
			{
				String uplist = forhitlist.getCustomerName().toUpperCase();
				if(uplist.startsWith(upcus))
				{
					hitList.add(forhitlist.getCustomerName() + " " + forhitlist.toString());
				}
			}
			if(hitList.isEmpty())
			{
				return "no name found";
				
			}
			else 
			{
				
				String newLine = System.lineSeparator(); // new-line character
				String hitString = "The following accounts were found starting with the name " + customerName;
				for(String onestring: hitList)
				{
					hitString =	hitString.concat(newLine+onestring);
				
					
				}
				return hitString;
				
			}
			
			
		}
		else 
		{
		CashAccount ca = accounts.get(accountNumber);
		if(ca == null)
		{
			return "Account " + accountNumber + " not found" ;
		}

		if(customerName.equalsIgnoreCase(ca.getCustomerName()))
		{
			return ca.toString();
		}

		else
			return "customer name doesn't match, the name in file is : " + ca.getCustomerName();
		}


	}


	public String processAccount(String processType, int accountNumber,
			double amount, String customerName)  
	{
		CashAccount ca = accounts.get(accountNumber);
		if(ca == null)
		{
			return "Account " + accountNumber + " not found" ;
		}

		if(customerName.equalsIgnoreCase(ca.getCustomerName()))
		{

			try {
				if(processType.equals("DEPOSIT") )

				{
					ca.deposit(amount);
					saveAccount();
					return ca.toString();

				}
				if(processType.equals("WITHDRAW"))
				{
					try {
						ca.withdraw(amount);
						saveAccount();
						return ca.toString();
					}
					catch (OverdraftException oe)
					{

						return oe.getMessage();

					}
				}

				else return "ERROR: transaction type: " + processType
						+ " is not recognized by the server.";
			}


			catch(Exception ee)
			{
				return ee.toString();
			}

		}
		else
		{
			return "customer name doesn't match, the name in file is : " + ca.getCustomerName();
		}



	}


	public String closeOutAccount(int accountNumber, String customerName)
	{

		CashAccount ca = accounts.get(accountNumber);
		if(ca == null)
		{
			return "Account " + accountNumber + " not found" ;
		}

		if(customerName.equalsIgnoreCase(ca.getCustomerName()))
		{

			if(ca.getBalance() != 0)
			{
				return "balance has to be 0 to be closed";

			}
			else 
			{
				try 
				{
				accounts.remove(accountNumber);
				saveAccount();
				return "close account has been made successful";
				}
				catch(Exception e)
				{
				return e.toString();
				}

			}

		}

		else
		{
			return "customer name doesn't match, the name in file is : " + ca.getCustomerName();
		}





		//return "Account number " + accountNumber + " for " + customerName + " is being closed.";
	}


}
