
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class Lab5SaveMessagesChatServer implements Runnable
{
	public static void main(String[] args)
	{
		if (args.length != 0) System.out.println("Guangsen Ji Entered command line parameters are being ignored.");
		try {
			new Lab5SaveMessagesChatServer();
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
	}

	// INSTANCE VARIABLES
	ServerSocket ss;
	// Collections
	ConcurrentHashMap<String, ObjectOutputStream> whosIn    = new ConcurrentHashMap<String, ObjectOutputStream>();
	ConcurrentHashMap<String, String>             passwords = new ConcurrentHashMap<String, String>();
	ConcurrentHashMap<String, Vector<Object>>  savedMessages = new ConcurrentHashMap<String, Vector<Object>>(); 

	public Lab5SaveMessagesChatServer() // *** CONSTRUCTOR ***
	{
		try {
			ss = new ServerSocket(5555);
			System.out.println("GuangsenJi's Lab5SavePrivateChatServer is up at " 
					+ InetAddress.getLocalHost().getHostAddress()
					+ " on port " + ss.getLocalPort());
		}
		catch(Exception e)
		{
			String errorMessage = "Port number " + ss.getLocalPort() + " is not available on this computer. "
					+ "Cancel the app currently using port " + ss.getLocalPort() + " and restart. " + e.getMessage();
			throw new IllegalArgumentException(errorMessage); 
		}
		//read password file 
		try {
			FileInputStream   fis     = new FileInputStream("passwords.ser"); // .ser file type means serialized object(s)  
			ObjectInputStream diskOIS = new ObjectInputStream(fis);
			passwords = (ConcurrentHashMap<String,String>) diskOIS.readObject(); // read the whole collection from disk!
			diskOIS.close(); // the first line of the method opens the file, this closes it.
			System.out.println("Previously in the chat room: " + passwords); // show passwords to tester!
		}
		catch(FileNotFoundException fnfe) // File will not be there first time you test...
		{
			System.out.println("A passwords.ser file was not found on disk, so an empty passwords collection will be used.");
		}
		catch(Exception e) // some other problem reading the file...
		{
			System.out.println(e); // print the Exception object as the error message.
			throw new IllegalArgumentException(e.getMessage()); // tell loading program 
		}
		//read savedmessage file 

		try {
			FileInputStream   fis     = new FileInputStream("SavedMessages.ser"); // .ser file type means serialized object(s)  
			ObjectInputStream diskOIS = new ObjectInputStream(fis);
			savedMessages = (ConcurrentHashMap<String,Vector<Object>>)diskOIS.readObject(); // read the whole collection from disk!
			diskOIS.close(); // the first line of the method opens the file, this closes it.
			System.out.println("Previously in the chat room: " + savedMessages); // show savedmessage to tester!
			System.out.println(savedMessages); // show contents of the collection!

		}
		catch(FileNotFoundException fnfe) // File will not be there first time you test...
		{
			System.out.println("A savedmessages.ser file was not found on disk, so an empty passwords collection will be used.");
		}
		catch(Exception e) // some other problem reading the file...
		{
			System.out.println(e); // print the Exception object as the error message.
			throw new IllegalArgumentException(e.getMessage()); // tell loading program 
		}

		new Thread(this).start(); // start thread for client #1 (jumps into run())
	}

	public void run() // Application threads enter here...
	{
		// Declare joining client's data as LOCAL variables on this client thread's STACK!
		Socket s              = null;
		ObjectInputStream ois = null;
		ObjectOutputStream oos= null;
		String joinMessage    = null;
		String chatName       = null;
		String clientAddress  = null;
		String enteredPassword= null;
		String storedPassword = null;
		String replacePassword= null;

		// ***************
		// JOIN PROCESSING for each client that connects:
		// ***************
		try {
			System.out.println("A new client thread is waiting in run() for the next client to connect.");
			s = ss.accept(); // wait for a client to join.
			clientAddress = s.getInetAddress().getHostAddress();
			System.out.println("A new client is connecting from " + clientAddress);
			ois = new ObjectInputStream(s.getInputStream());
			joinMessage = (String) ois.readObject();
			oos = new ObjectOutputStream(s.getOutputStream());
		}
		catch(Exception e) // some kind of connection failure
		{
			if (chatName      != null) System.out.println("Connection failure from " + chatName);
			if (clientAddress != null) System.out.println("Connection failure from " + clientAddress);
			System.out.println("Join connection failure " + e);
			return; // terminate this client thread.
		}
		finally // In any event, start another thread to join the next client that connects.
		{
			new Thread(this).start();
		}
		joinMessage = joinMessage.trim(); // remove any leading or trailing blanks

		int blankOffset = joinMessage.indexOf(" ");
		if (blankOffset < 0)
		{
			try {
				oos.writeObject("Invalid message format."); // send vague err msg as join reply.
				oos.close(); // drop connection
			}
			catch(Exception e) {}
			System.out.println("Invalid join message received: '" + joinMessage + "' from " + clientAddress);
			return; // this client thread returns to it's Thread object which terminates it.
		}
		chatName = joinMessage.substring(0,blankOffset).toUpperCase(); // extract the chatName from join message.
		enteredPassword = joinMessage.substring(blankOffset).trim(); // remove leading blanks

		int passwordBlankOffset = enteredPassword.indexOf(" ");
		if (passwordBlankOffset > 0) // can't be 0 because we trim()ed enteredPassword.
		{                         // A replacePasssord has been provided!
			replacePassword = enteredPassword.substring(passwordBlankOffset).trim();// Order of these
			enteredPassword = enteredPassword.substring(0,passwordBlankOffset);     // statements is important!
		}
		else replacePassword = ""; // not null but not a blank.
		// now we can test for an embedded blank in the replacePassword:
		if (chatName.contains(" ") || enteredPassword.contains(" ") || replacePassword.contains(" "))
		{
			try {
				oos.writeObject("Invalid message format."); // send vague err msg as join reply.
				oos.close(); // drop connection
			}
			catch (Exception e) {} 
			System.out.println("Invalid join message received: '" + joinMessage + "' from " + clientAddress);
			return; // this client thread returns to it's Thread object which terminates it.
		}

		if (chatName.length() > 20)
		{
			try {
				oos.writeObject("Invalid chatName. Max 20 characters.");
				s.close();
			}
			catch(Exception e) {}
			return; // kill client thread
		}

		// Let's see if this chatName has previously been in the chat room ,
		// and therefore has a password on file. 
		if (passwords.containsKey(chatName)) // yes, they've been in before.
		{                                   // so see if their providedPassword is correct.
			storedPassword = passwords.get(chatName);
			if (!enteredPassword.equals(storedPassword)) // case-sensitive compare
			{
				try {
					oos.writeObject("Incorrect password for " + chatName); // send err msg as join reply.
					oos.close(); // drop connection
				}
				catch (Exception e) {}
				System.out.println("Invalid password (" + enteredPassword + ") received for " + chatName
						+ " should be (" + storedPassword + ")");
				return; // this client thread returns to it's Thread object which terminates it.
			}
			else // the providedPassword DID match the storedPassword
			{
				if (replacePassword.length() > 0) // a password change was requested
				{
					passwords.replace(chatName, replacePassword); 
					savePasswords(); // save new pw on disk
				}
			}
		}
		// with a valid password, we can send this client the "Welcome" message in response to their join request.
		try {                                                               
			oos.writeObject("Welcome to the chat room " + chatName + " !"); // send "join is successful" 
			// And, if they are a never-before client, add their pw to the collection:
			if (!passwords.containsKey(chatName))    
			{
				if (replacePassword.length() == 0)
					passwords.put(chatName, enteredPassword); 
				else
					passwords.put(chatName, replacePassword);
				savePasswords();
			}
		}                                                               
		catch(Exception e)
		{
			System.out.println("Error sending join reply to client " + chatName);
			return; // terminate this new client thread.
		}

		// System.out.println("Just before joining " + chatName + " the contents of passwords is: " + passwords);

		// Now, belatedly it seems, we can check to see if this clientName is already in the chat room.
		if (whosIn.containsKey(chatName)) // check for chat name already-in 
		{                              // IF SO THIS IS A *** REJOIN *** situation.
			ObjectOutputStream previousSessionOOS = whosIn.get(chatName);
			try {
				previousSessionOOS.close(); // terminate the connection to Bubba's previous location
			}
			catch(Exception e) {}
			// RE-JOIN processing
			whosIn.replace(chatName, oos); // replace client's old oos with new oos in whosIn list.
			System.out.println(chatName + " is re-joining.");
			String[] inChatNames        = whosIn.keySet().toArray(new String[0]);
			String[] passwordsChatNames = passwords.keySet().toArray(new String[0]);
			Vector<String> whosNotInVector = new Vector<String>();
			for (String name : passwordsChatNames) // initialize Vector with passwordsChatNames
				whosNotInVector.add(name);
			for (String name : inChatNames)        // subtract whosIn names from passwords names
				whosNotInVector.remove(name);     // to leave whosNotIn names in Vector.
			String[] notInChatNames = whosNotInVector.toArray(new String[0]);
			Arrays.sort(inChatNames);
			Arrays.sort(passwordsChatNames);
			Arrays.sort(notInChatNames);
			try {
				oos.writeObject(inChatNames);  
				oos.writeObject(notInChatNames);  
			}
			catch(Exception e) {}
		}
		else // Do NORMAL join processing.
		{
			whosIn.put(chatName, oos); // add new client to whosIn list.
			sendToAll("Say hello to " + chatName + " who just joined the chat room!");
			System.out.println(chatName + " is joining with password " + passwords.get(chatName));
			String[] inChatNames        = whosIn.keySet().toArray(new String[0]);
			String[] passwordsChatNames = passwords.keySet().toArray(new String[0]);
			Vector<String> whosNotInVector = new Vector<String>();
			for (String name : passwordsChatNames) // initialize Vector with passwordsChatNames
				whosNotInVector.add(name);
			for (String name : inChatNames)        // subtract whosIn names from passwords names
				whosNotInVector.remove(name);     // to leave whosNotIn names in Vector.
			String[] notInChatNames = whosNotInVector.toArray(new String[0]);
			Arrays.sort(inChatNames);
			Arrays.sort(passwordsChatNames);
			Arrays.sort(notInChatNames);
			System.out.println("Currently in the chat room: " + Arrays.toString(inChatNames)); // show who's in on server console.
			sendToAll(inChatNames);  
			sendToAll(notInChatNames);  
		}
		
//load saved message for the client 		
		Vector<Object> savedMessageList = savedMessages.get(chatName);
		if (savedMessageList != null) // Is there even a list? 
		   { // There IS a list.
		   while (!savedMessageList.isEmpty()) // any messages left?
		       {
		       Object savedMessage = savedMessageList.remove(0);//show oldest message first!
		       try {
		           oos.writeObject(savedMessage);
		           saveMessagesCollection(); // we've changed it! 
		           }
		       catch(Exception e) // joiner has suddenly left!
		           {
		           break; // so stop showing
		           }
		       }
		   }
		           
	
		// System.out.println("After joining " + chatName + " the contents of passwords is: " + passwords);

		// ******************
		// RECEIVE PROCESSING for each client
		// ******************
		// LEAVES the chat room. (meanwhile all local variables are preserved on the STACK!)
		try {
			while (true) {
				Object chatMessage = ois.readObject(); // in Lab4 messages may not be Strings.

				if (chatMessage instanceof String)
				{
					String chatString = (String) chatMessage; // make a pointer of type String
					System.out.println("- Received PUBLIC msg '" + chatString + "' from " + chatName); // call toString()

					if (chatString.length() > 0)
					{
						sendToAll(chatName + " says: " + chatString); // broadcast chat message to all clients.
						System.out.println("- Sent '" + chatMessage + "' to all clients");
					}
					else
					{
						System.out.println("Invalid message from " + chatName);
					}
				}
				else if (chatMessage instanceof String[]) // an array of Strings
				{
					String[] messageArray = (String[]) chatMessage; // make a pointer of type String[]
					String recipientChatName = messageArray[1]; // First recipient in array (has at least 1)

					if (whosIn.containsKey(recipientChatName)) // WhosInList Array
					{
						System.out.println("- Received privateMessageArray " + Arrays.toString(messageArray) + " from " + chatName);
						sendPrivateMessage(chatName, messageArray);
					}
					else // WhosNotInList Array
					{
						System.out.println("- Received saveMessageArray " + Arrays.toString(messageArray) + " from " + chatName);
						saveMessage(chatName, messageArray);
					}
				}
				else 
				{
					System.out.println("Unexpected object type received from client: " + chatMessage); // call toString() on the object
				}
			} 
		}	 
		// **************** 
		catch(Exception e) // LEAVE PROCESSING for each client.
		{              // ****************
			// If this is a client whos is REJOINING the chat room from a new location, and this is
			// the OLD session client thread, then we DON'T want to do the normal leave
			// processing of notifying everyone the client is leaving because they arn't!  
			// How do we tell if this is the OLD session client thread? Simple! We get the oos
			// pointer from whosIn and compare it to this thread's oos pointer on this thread's STACK.
			// If the oos pointers point to different OOS objects, then the one in whosIn has been
			// by the new client thread, and this thread should simply return without doing anything.
			if (whosIn.get(chatName) != oos) return;
			// otherwise do the normal leave processing:   
			System.out.println(chatName + " is leaving the chat room.");
			whosIn.remove(chatName);
			sendToAll("Goodby to " + chatName + " whos has just left the chat room."); // broadcast to all clients in the chat room.
			String[] inChatNames        = whosIn.keySet().toArray(new String[0]);
			String[] passwordsChatNames = passwords.keySet().toArray(new String[0]);
			Vector<String> whosNotInVector = new Vector<String>();
			for (String name : passwordsChatNames) // initialize Vector with passwordsChatNames
				whosNotInVector.add(name);
			for (String name : inChatNames)        // subtract whosIn names from passwords names
				whosNotInVector.remove(name);     // to leave whosNotIn names in Vector.
			String[] notInChatNames = whosNotInVector.toArray(new String[0]);
			Arrays.sort(inChatNames);
			Arrays.sort(passwordsChatNames);
			Arrays.sort(notInChatNames);
			System.out.println("Currently in the chat room: " + Arrays.toString(inChatNames)); // show who's in on server console.
			sendToAll(inChatNames);  
			sendToAll(notInChatNames);  
			return; // kill client thread (and clear STACK). 
		}

	}

	private synchronized void sendToAll(Object message) // make threads enter one-at-a-time 
	{
		//System.out.println("Sending '" + message + "' to everyone.");
		ObjectOutputStream[] clientOOSlist = whosIn.values().toArray(new ObjectOutputStream[0]);
		for(ObjectOutputStream oos : clientOOSlist)
		{
			try {
				oos.writeObject(message);
			}
			catch(Exception e)
			{
				// just skip this client (they probably left). Contine sending to others.
			}
		}
	}

	private synchronized void sendPrivateMessage(String chatName, String[] recipientList)
	{
		String actualRecipients = " "; // Recipients sent successfully

		// Send to each recipient(s)
		for (int i = 1; i < recipientList.length; i++) // Loop thru each recipient in array
		{

			String otherRecipients = " "; // who else is recipient(s)
			for (int n = 1;  n < recipientList.length; n++)
			{
				if (!recipientList[n].equalsIgnoreCase(recipientList[i]))
					otherRecipients += recipientList[n] + " ";
			}

			String privateMessage;
			if (otherRecipients.trim().length() == 0) // If only one private recipient
				privateMessage = chatName + " sends you this PRIVATE message: " + " '" +recipientList[0]+"' ";
			else // there are other recipients. 
				privateMessage = chatName + " sends this PRIVATE message: '" + "' " + recipientList[0]+ " '" 
						+ "' to: you and " + otherRecipients;

			ObjectOutputStream recipientOOS = whosIn.get(recipientList[i]); // Get private recipient(s) oos

			if (recipientOOS != null) // perhaps this recipient just left the chat room! 
			{ 
				try 
				{
					recipientOOS.writeObject(privateMessage);
					actualRecipients += recipientList[i] + " ";
				}
				catch(Exception e) {} // skip it if failure
			}
		}

		System.out.println("- Sent Private msg '" + recipientList[0] + "' to: " + actualRecipients);

		// Confirm with Sender
		ObjectOutputStream senderOOS = whosIn.get(chatName);
		try 
		{
			senderOOS.writeObject("(You sent PRIVATE message: '" + recipientList[0] + "' to: " + actualRecipients + ")");
		}
		catch(Exception e) {} // skip it if failure

	}


	private synchronized void savePasswords() // make calling client threads enter one-at-a-time
	{
		try 
		{
			FileOutputStream   fos     = new FileOutputStream("passwords.ser"); // .ser file type means serialized object(s)  
			ObjectOutputStream diskOOS = new ObjectOutputStream(fos);
			diskOOS.writeObject(passwords); // write the whole collection to disk!
			diskOOS.close(); // the first line of the method opens the file, this closes it.
		}                // (It was not opened in "append" mode, so this overwrites the existing file.)
		catch(Exception e)
		{
			System.out.println("Error saving passwords file on disk. " + e);
		}
	}

	private synchronized void saveMessage(String chatName, String[] recipientList)
	{	
		String actualRecipients = " "; // Recipients sent successfully

		for (int i = 1; i < recipientList.length; i++) // Loop thru each recipient in array
		{

			String otherRecipients = " "; // who else is recipient(s)
			for (int n = 1;  n < recipientList.length; n++)
			{
				if (!recipientList[n].equalsIgnoreCase(recipientList[i]))
					otherRecipients += recipientList[n] + " ";
			}

			String messageToBeSaved ;
			Date Time = new Date();
			
			if (otherRecipients.trim().length() == 0) // If only one private recipient
				messageToBeSaved = chatName + " sent you this PRIVATE message: " + "' " + recipientList[0]+ " '"+ "at " + Time;
			else // there are other recipients. 
				messageToBeSaved = chatName + " sends this PRIVATE message: '" + "' "+recipientList[0] +" '"
						+ "' to: you and " + otherRecipients+ "at " + Time;
			
			Vector<Object> recipientVector = savedMessages.get(recipientList[i]);
			if (recipientVector == null) // this recipient has never had a message saved for them 
			{
				recipientVector = new Vector<Object>();              // so make them an empty Vector
				savedMessages.put(recipientList[i], recipientVector);// and add this recipient to the 
			}                                                     // savedMessages collection. 
			recipientVector.add(messageToBeSaved); // add this message to this recipient's Vector. 
			actualRecipients += recipientList[i] + " ";

		}
		
		System.out.println("- saved Private msg '" + recipientList[0] + "' to: " + actualRecipients +" in .ser file");
		saveMessagesCollection() ;
		
		
		ObjectOutputStream senderOOS = whosIn.get(chatName);
		try 
		{
			senderOOS.writeObject("You saved PRIVATE message: '" + recipientList[0] + "' to: " + actualRecipients + " in .ser");
		}
		catch(Exception e) {} // skip it if failure
		
		
		
		

	}




	private synchronized void saveMessagesCollection() 
	{
		try 
		{
			FileOutputStream   fos     = new FileOutputStream("SavedMessages.ser"); // .ser file type means serialized object(s)  
			ObjectOutputStream diskOOS = new ObjectOutputStream(fos);
			diskOOS.writeObject( savedMessages); // write the whole collection to disk!
			diskOOS.close(); // the first line of the method opens the file, this closes it.
		}                // (It was not opened in "append" mode, so this overwrites the existing file.)
		catch(Exception e)
		{
			System.out.println("Error saving MESSAGES file on disk. " + e);
		}


	}


}















