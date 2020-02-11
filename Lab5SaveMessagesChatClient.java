
import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

public class Lab5SaveMessagesChatClient implements ActionListener
{
	public static void main(String[] args)
	{
		String newLine = System.lineSeparator();
		String newPassword = "";
		try {
			if ((args.length < 3) || (args.length > 4))
			{
				System.out.println("Provide three command line parameters to specify "
						+ newLine + "(1) the network address of the ChatRoomServer"
						+ newLine + "(2) your chat name."
						+ newLine + "(3) your password"
						+ newLine + "If you would like to change your password, enter the "
						+ newLine + "new password as a 4th parameter."); 
				return;
			}
			if (args.length == 4) newPassword = args[3];
			Lab5SaveMessagesChatClient lab4crc = new Lab5SaveMessagesChatClient(args[0], args[1], args[2], newPassword);
			lab4crc.receive(); // branch main thread into client program object to be the receive thread!
		}
		catch(Exception e)
		{
			System.out.println("Connection to Chat Room Server has failed: " + e.getMessage());
		}
	}

	// INSTANCE variables
	// chat window GUI objects
	JFrame      window            = new JFrame("Lab5SavePrivateChat"); // set title bar text
	JTextField  errMsgTextField   = new JTextField("Client-side error messages will be displayed here.");
	JTextField  textField         = new JTextField("Enter a PUBLIC chat message to send to ALL CLIENTS and press ENTER");
	JTextArea   textArea          = new JTextArea();
	JScrollPane messageScrollPane = new JScrollPane(textArea);

	// whos-in window GUI objects
	JFrame      whosInWindow      = new JFrame("Who's <-IN and OUT->"); // set title bar text
	JTextField  privateTextField  = new JTextField("Enter PRIVATE message to send ONLY to SELECTED CLIENTS and press ENTER");
	JButton     clearButton       = new JButton("Clear All Selections");
	JPanel      middlePanel       = new JPanel();
	JList<String> whosInList      = new JList<String>();
	JList<String> whosNotInList   = new JList<String>();
	JScrollPane whosInScrollPane  = new JScrollPane(whosInList);
	JScrollPane whosNotScrollPane = new JScrollPane(whosNotInList);

	// instance variable fields
	Socket s;
	ObjectInputStream  ois;
	ObjectOutputStream oos;
	String chatName;


	public Lab5SaveMessagesChatClient(String serverAddress,  // ***CONSTRUCTOR*** method
			String chatName,
			String password,
			String newPassword)
	{
		this.chatName = chatName; // save the local var in the instance var.
		try {
			if (serverAddress.contains(" ") || chatName.contains(" ") || password.contains(" ") || newPassword.contains(" "))
				throw new IllegalArgumentException("Arguments cannot contain embeded blanks");
			int    serverPort    = 5555;
			System.out.println("Connecting to " + serverAddress + " on port " + serverPort + " for " + chatName);
			s = new Socket(serverAddress, serverPort); // wait for server connect
			oos = new ObjectOutputStream(s.getOutputStream());
			String joinMessage = chatName + " " + password;
			if (newPassword.length() > 0) joinMessage += " " + newPassword;
			oos.writeObject(joinMessage);
			ois = new ObjectInputStream(s.getInputStream());
			String reply = (String) ois.readObject(); // wait for server response
			if (reply.startsWith("Welcome"))
				System.out.println("Server reply to join message is: " + reply);
			else
				throw new IllegalArgumentException(reply);
			UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel"); // will help Mac GUIs
		}
		catch(Exception e)
		{
			String errorMessage = e.getMessage();
			throw new IllegalArgumentException(errorMessage);
		} 

		// *** At this point, join to ChatRoom is successful! ***  We want to put the whosInWindow to the
		// *** So BRING UP THE GUI! ***                            left of a wider chat window. Both windows
		// Build the chat window                                   should be the same height.    
		window.getContentPane().add(errMsgTextField, "North");
		window.getContentPane().add(messageScrollPane, "Center");
		window.getContentPane().add(textField,         "South"); 
		window.setTitle(chatName + "'s CHAT ROOM");    
		window.setSize(900,400);   // width,height
		window.setLocation(300,100); // x,y (x is "to the right of the left margin, y is "down-from-the-top")
		errMsgTextField.setBackground(Color.white);
		errMsgTextField.setEditable(false); // keep user from changing!
		textField.setBackground(Color.yellow);
		textArea.setEditable(false); // keep user from typing into this area.
		textArea.setLineWrap(true);     // cause long text added to be properly
		textArea.setWrapStyleWord(true);// "wrapped" to the next line.
		window.setVisible(true);   // show it
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // terminate app when window closed.

		// build the whosInWindow
		whosInWindow.getContentPane().add(clearButton,     "North");
		whosInWindow.getContentPane().add(middlePanel,     "Center");
		whosInWindow.getContentPane().add(privateTextField,"South");
		middlePanel.setLayout(new GridLayout(1,2)); // 1 row, 2 cols
		middlePanel.add(whosInScrollPane);  // left
		middlePanel.add(whosNotScrollPane); // right
		whosInWindow.setSize(300,400);   // width,height - same height as chat window
		whosInWindow.setLocation(0,100); // x,y (x is "to the right of the left margin, y is "down-from-the-top")
		clearButton.setBackground(Color.white);
		privateTextField.setBackground(Color.yellow);
		whosInWindow.setVisible(true);   // show it
		whosInWindow.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);//user can iconify window to get it off the screen.

		textField.addActionListener(this);       // give address of this client program to the textfield GUI object.
		privateTextField.addActionListener(this);// give address of this client program to the privateTextfield.
		clearButton.addActionListener(this);     // give address of this client program to the clearButton.
	} // end of constructor method


	public void actionPerformed(ActionEvent ae) // textfield calls here!
	{
		errMsgTextField.setText("");                // remove last error message
		errMsgTextField.setBackground(Color.white); // from the screen.

		if (ae.getSource() == textField)
		{
			if (!whosInList.isSelectionEmpty() || !whosNotInList.isSelectionEmpty()) // Not Empty Recipients Selections
			{
				errMsgTextField.setText("UNCHECK THE RECIPENTS BEFORE SENDING PUBLIC MESSAGE");
				errMsgTextField.setBackground(Color.pink); 
				return; 
			}
			
			String publicChatMessage = textField.getText().trim();
			if (publicChatMessage.length() == 0)
			{
				errMsgTextField.setText("No PUBLIC message was entered to send.");
				errMsgTextField.setBackground(Color.pink); 
				return; 
			}
			textField.setText(""); // clear input area
			try {
				oos.writeObject(publicChatMessage);
			}
			catch(Exception e)
			{
			}
		} // let the textField's thread return to the textField GUI object!

		if (ae.getSource() == privateTextField)
		{
			
			if (whosInList.isSelectionEmpty() && whosNotInList.isSelectionEmpty()) // Empty Recipients Selections
			{
				errMsgTextField.setText("NO PRIVATE RECIPIENT SELECTED.");
				errMsgTextField.setBackground(Color.pink); 
				return; 
			}
			
			
			String privateMessage = privateTextField.getText().trim();
			privateTextField.setText(""); // clear input area.
			if (privateMessage.length() == 0)
			{
				errMsgTextField.setText("No PRIVATE message was entered to send.");
				errMsgTextField.setBackground(Color.pink); 
				return; 
			}
			System.out.println("Private message: '" + privateMessage + "' was read from the privateTextField."); 
			
		//send WhosInList to server
			
			String[] privateRecipientsArray = whosInList.getSelectedValuesList().toArray(new String[0]);
			if (privateRecipientsArray.length > 0) //this list had some selections.
			{                                                                   
				String[] privateMessageArray = new String[privateRecipientsArray.length+1]; 
				privateMessageArray[0] = privateMessage;             // put the message in slot 0                             
				for (int n = 1; n < privateMessageArray.length; n++) // add recipient names in remaining slots                
					privateMessageArray[n] = privateRecipientsArray[n-1];                 
				try {oos.writeObject(privateMessageArray);} // send save msg + save recipients array to server
				catch(Exception e) {System.out.println("Error sending saveMessageArray to server");}
				System.out.println("Sending a privateMessageArray to server: " + Arrays.toString(privateMessageArray));
			}  
			
		//send WhosNotInList to server
			String[] saveRecipientsArray = whosNotInList.getSelectedValuesList().toArray(new String[0]);
			if (saveRecipientsArray.length > 0) //this list had some selections.
			{                                                                   
				String[] saveMessageArray = new String[saveRecipientsArray.length+1]; 
				saveMessageArray[0] = privateMessage;             // put the message in slot 0                             
				for (int n = 1; n < saveMessageArray.length; n++) // add recipient names in remaining slots                
					saveMessageArray[n] = saveRecipientsArray[n-1];                 
				try {oos.writeObject(saveMessageArray);} // send save msg + save recipients array to server
				catch(Exception e) {System.out.println("Error sending saveMessageArray to server");}
				System.out.println("Sending a saveMessageArray to server: " + Arrays.toString(saveMessageArray));
			} 
			
			
		}

		if (ae.getSource() == clearButton)
		{
			System.out.println("ClearSelections button was pushed.");
			whosInList.clearSelection();
			whosNotInList.clearSelection();
		}
	}

	public void receive()
	{
		String newLine = System.lineSeparator(); // new-line character.
		try {
			while(true) // "CAPTURE" receive thread
			{
				Object messageFromServer = ois.readObject(); // no casting - could be anything!
				if (messageFromServer instanceof String) // a chat message
				{
					String chatMessage = (String) messageFromServer; // make a pointer of type String 
					textArea.append(newLine + chatMessage);
					// auto-scroll textArea to bottom line so the last message will be visible.
					textArea.setCaretPosition(textArea.getDocument().getLength()); // really?
				}
				else if (messageFromServer instanceof String[]) // an array of whos-in
				{
					String[] chatNames = (String[]) messageFromServer; // make a pointer of type String[]
					boolean foundIt = false; 
					for (String name : chatNames) // see if this is an IN or OUT list by if it contains THIS client!
						if (name.equalsIgnoreCase(chatName))
						{ 
							whosInList.setListData(chatNames);
							System.out.println("Who's-in from server: " + Arrays.toString(chatNames));
							foundIt = true;
							break;
						}
					if (foundIt == false)
					{
						whosNotInList.setListData(chatNames);
						System.out.println("Who's-not-in from server: " + Arrays.toString(chatNames));
					}
				} 
				else System.out.println("Unexpected object type received from server: " + messageFromServer);
			} // end of while
		} // end of try
		catch(Exception e)
		{
			// Assume link to server is permanently down.
			textArea.setBackground(Color.pink);
			textArea.setText("CONNECTION TO THE CHAT ROOM SERVER HAS FAILED! "
					+ " You must close and then restart the client to reconnect to the server to continue.");
			textField.setEditable(false); // keep user from trying to send any more public messages.
			privateTextField.setEditable(false); // keep user from trying to send any more private messages.
			clearButton.setEnabled(false); // make button unpushable.
		}
	}  // main thread will return to main() where it will terminate. (we are through receiving if the server is down!)
}