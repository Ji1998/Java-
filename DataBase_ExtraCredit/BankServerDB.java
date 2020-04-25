//Guangsen Ji
//04/12/2020 
//A server for database 
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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;


public class BankServerDB extends UnicastRemoteObject 
implements TellerServerClientInterface
{
	private Connection connection;
	private PreparedStatement insertStatement;
	private PreparedStatement updateStatement;
	private PreparedStatement deleteStatement;
	private Statement selectAllStatement;
	ConcurrentHashMap<Integer,CashAccount> accounts = new ConcurrentHashMap<Integer,CashAccount>();




	public static void main(String[] args) throws Exception
	{
		new BankServerDB(); // load program object & call constructor

	}

	public BankServerDB() throws Exception // CONSTRUCTOR
	{
		super(); // call parent constructor
		LocateRegistry.createRegistry(1099); // start the rmiregistry 
		Naming.rebind("BankServer", this); // register with rmiregistry
		System.out.println("EchoTellerServer is up at " + InetAddress.getLocalHost().getHostAddress());
		Class.forName("com.ibm.db2j.jdbc.DB2jDriver");
		System.out.println("DB driver loaded!");
		connection = DriverManager.getConnection("jdbc:db2j://Users//Steven//Documents//eclipse-workspace//DataBase_ExtraCredit//Database//QuoteDB");
		System.out.println("DB opened!");
		insertStatement = connection.prepareStatement(
				"INSERT INTO BANK_ACCOUNTS "
						+ "(ACCOUNT_NUMBER, ACCOUNT_TYPE, CUSTOMER_NAME, BALANCE) "
						+ "VALUES (?,?,?,?)");
		
		 updateStatement = connection.prepareStatement(
                 "UPDATE BANK_ACCOUNTS "
               + "SET BALANCE = ? "
               + "WHERE ACCOUNT_NUMBER = ?");
		 deleteStatement = connection.prepareStatement(
                 "DELETE FROM BANK_ACCOUNTS "
               + "WHERE ACCOUNT_NUMBER = ?");
		 selectAllStatement = connection.createStatement();
		 ResultSet rs = selectAllStatement.executeQuery("SELECT * FROM BANK_ACCOUNTS");
		 while (rs.next()) // read the next row of the ResultSet
		  {
		  // get the column values for this row
		  int    accountNumber = rs.getInt   ("ACCOUNT_NUMBER");
		  String accountType   = rs.getString("ACCOUNT_TYPE");
		  String customerName  = rs.getString("CUSTOMER_NAME");
		  double balance       = rs.getDouble("BALANCE");
		  System.out.println(" acct#="    + accountNumber
	                 + " acctType=" + accountType
	                 + " custName=" + customerName
	                 + " balance="  + balance);
		  CashAccount ca;
		     if (accountType.equals("CHECKING"))
		         ca = CheckingAccount.restoreFromDataBase(customerName, accountNumber, balance);
		else if (accountType.equals("SAVINGS"))
		         ca = SavingsAccount.restoreFromDataBase(customerName, accountNumber, balance);
		else    { 
		        System.out.println("SYSTEM ERROR: account type " + accountType + " is not recognized when reading DB table BANK_ACCOUNTS in server constructor.");
		        continue; // skip unrecognized account
		        }
		     accounts.put(accountNumber, ca);
		     System.out.println(accounts); // EASY!
		  }
		 
		 
		 

	}

	public String createNewAccount(String accountType, String customerName)

	{	CashAccount ca;
	try {
		if (accountType.equals("CHECKING"))
		{
			ca = new CheckingAccount(customerName);
			accounts.put(ca.getAccountNumber(), ca);
			   // Add a new row to the DB table for this new account
			try {
			    insertStatement.setInt   (1, ca.getAccountNumber());
			    insertStatement.setString(2, accountType);
			    insertStatement.setString(3, customerName);
			    insertStatement.setDouble(4, 0); // initial balance for a new account
			    insertStatement.executeUpdate();
			    }
			catch(SQLException sqle)
			    {
			    return "ERROR: Unable to add new account to the data base."
			          + sqle.toString();
			    }
		}

		else if (accountType.equals("SAVINGS"))
		{
			ca = new SavingsAccount(customerName);
			accounts.put(ca.getAccountNumber(),ca);
			//insertion to DB
			   // Add a new row to the DB table for this new account
			try {
			    insertStatement.setInt   (1, ca.getAccountNumber());
			    insertStatement.setString(2, accountType);
			    insertStatement.setString(3, customerName);
			    insertStatement.setDouble(4, 0); // initial balance for a new account
			    insertStatement.executeUpdate();
			    }
			catch(SQLException sqle)
			    {
			    return "ERROR: Unable to add new account to the data base."
			          + sqle.toString();
			    }

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
					//update to DB
					try {
					    updateStatement.setDouble (1, ca.getBalance());
					    updateStatement.setInt    (2, ca.getAccountNumber());
					    updateStatement.executeUpdate();
					    }
					catch(SQLException sqle)
					    {
					    return "ERROR: Server is unable to update account in the data base."
					         + sqle.toString();
					    }
					
					
					
					return ca.toString();

				}
				if(processType.equals("WITHDRAW"))
				{
					try {
						ca.withdraw(amount);
						//update to DB
						try {
						    updateStatement.setDouble (1, ca.getBalance());
						    updateStatement.setInt    (2, ca.getAccountNumber());
						    updateStatement.executeUpdate();
						    }
						catch(SQLException sqle)
						    {
						    return "ERROR: Server is unable to update account in the data base."
						         + sqle.toString();
						    }
						
						
						
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
					//delate table row in DB
					try {
					    deleteStatement.setInt(1, accountNumber);
					    deleteStatement.executeUpdate();
					    }
					catch(SQLException sqle)
					    {
					    return "ERROR: Server is unable to delete account from the data base."
					         + sqle.toString();
					    }
					
					
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
