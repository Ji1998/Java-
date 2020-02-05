//ECE492-Lab4-Guangsen Ji


import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;

import jdk.nashorn.internal.runtime.regexp.joni.exception.ErrorMessages;

import java.awt.*;
import java.awt.event.*;


public class PrivateChatClient              implements ActionListener{

    Socket s;
    ObjectInputStream ois;
    ObjectOutputStream dos;
    JFrame window = new JFrame("ChatRoomClient");
    JTextField errMsgTextField = new JTextField("Client-side error messages will be displayed here.");
    JTextField textField = new JTextField("Enter a public chat message to send to ALL and press ENTER");
    JTextArea textArea = new JTextArea("Received message show here ");
    JScrollPane messageScrollPane = new JScrollPane(textArea);
    // whos-in window GUI objects   
    JFrame      whosInWindow      = new JFrame("Who's <-IN and OUT->"); // set title bar text
    JTextField  privateTextField  = new JTextField("Enter PRIVATE message to send ONLY to SELECTED CLIENTS and press ENTER");
    JButton     clearButton       = new JButton("Clear All Selections");
    JPanel      middlePanel       = new JPanel();
    JList whosInList      = new JList();
    JList whosNotInList   = new JList();
    JScrollPane whosInScrollPane  = new JScrollPane(whosInList);
    JScrollPane whosNotScrollPane = new JScrollPane(whosNotInList);
    String chatName;

    public PrivateChatClient(String serverAddress,String chatName, String password, String newPassword)
    {
        this.chatName = chatName; // save the local var in the instance var.
        if (serverAddress.contains(" ") || chatName.contains(" "))
        throw new IllegalArgumentException("Arguments cannot contain embeded blanks");
        try{
             s = new Socket (serverAddress, 4444);
             dos = new ObjectOutputStream(s.getOutputStream()); //connect output stream with the socket
             if (newPassword != ""){
                dos.writeObject(chatName + " " + password + " " + newPassword);
             }
             else {
                 dos.writeObject(chatName + " " + password);
             }
             
             ois = new ObjectInputStream(s.getInputStream());
             String reply = (String)ois.readObject();
             if(reply.startsWith("Welcome"))
                 System.out.println("Server said:" + reply);
              else 
                 throw new IllegalArgumentException(reply);
        }
        catch(Exception e){
            String errorMessage = e.getMessage();
            throw new IllegalArgumentException(errorMessage);
        }
        System.out.println("MyFirstGUI Guangsen Ji @OFF-Uber 2020 ");
        //window.getContentPane().add(whoInLabel,"North"); //button
        window.getContentPane().add(errMsgTextField,"North"); //button
        window.getContentPane().add(messageScrollPane, "Center"); //user reflect 
        window.getContentPane().add(textField, "South"); //user input 
        window.setSize(500,400); // default size is ICON size
        window.setLocation(500,0);//x:from left to right, y:down forn the top 
        //whoInLabel.setBackground(Color.white);
        errMsgTextField.setBackground(Color.white);
        errMsgTextField.setEditable(false); // keep user from changing errMsg
        textField.setBackground(Color.yellow);
        textArea.setFont(new Font("default", Font.BOLD, 20));
        window.setVisible(true);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setTitle(chatName + "'s CHAT ROOM");
        textField.addActionListener(this);//call when ENTER,give address of MyFirstGUI program to the enter program
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

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
        clearButton.addActionListener(this);
        privateTextField.setBackground(Color.yellow);
        privateTextField.addActionListener(this);
        whosInWindow.setVisible(true);   // show it
        whosInWindow.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);//user can iconify window to get it off the screen

    }
   
    public void actionPerformed(ActionEvent ae)
    {   
       
       
        errMsgTextField.setText("");                // remove last error message
        errMsgTextField.setBackground(Color.white); // from the screen.
        if(ae.getSource() == clearButton)
        {
            System.out.println("The ClearSelections button was pushed.");
            whosInList.clearSelection();    // remove the HIGHLIGHTING
            whosNotInList.clearSelection(); // not the entries!
        }
        
        if(ae.getSource() == privateTextField)
        {       String privatextget = privateTextField.getText().trim();
                System.out.println("A private message :" + privatextget);
                privateTextField.setText("");
                if ((privatextget.length() == 0))
                {
                    errMsgTextField.setText("No message was entered or send");
                    errMsgTextField.setBackground(Color.pink);
                    return ;
                }
                try
                {
                        dos.writeObject(privatextget);
                }
                catch(Exception e)
                {
                    // do nothing here! (ois.readObject() will be getting the same error)
                }



        }
        
        if(ae.getSource() == textField)
        {
        String publicChatMessage = textField.getText().trim();
         if (publicChatMessage.length() == 0)
            {
                errMsgTextField.setText("No message was entered to send.");
                errMsgTextField.setBackground(Color.pink); 
            return; // ignore blank messages.
            }
            try
            {
                    dos.writeObject(publicChatMessage);
            }
            catch(Exception e)
            {
                // do nothing here! (ois.readObject() will be getting the same error)
            }


        }   
        
       // String chatMessageToSend = textField.getText().trim();
       // if (chatMessageToSend.length() == 0) return; // ignore blank messages.
       // textField.setText(""); // clear input area
       // try {
       //     dos.writeObject(chatMessageToSend);
       //     }
       // catch(Exception e)
       //     {
            // do nothing here! (ois.readObject() will be getting the same error)
       //     }
    
        }


    public static void main(String[] args)
    {   
    try{
        String ServerAddress;
        String chatName;
        String passwords;
        String replace_passwords;
        
        // System.out.println("Welcome! Please enter server address and chat name");
        if (args.length == 3){
        System.out.println(" Guangsen Ji Server Address, Chat Name and password get!");
        ServerAddress = args[0];
        chatName = args[1];
        passwords = args[2];
        
        PrivateChatClient  crc = new PrivateChatClient(args[0], args[1], args[2],"");
        crc.receive(); // branch main thread into client program object to be the receive thread

        }
        if (args.length == 4){
            System.out.println(" Guangsen Ji Server Address, Chat Name and password get!");
            ServerAddress = args[0];
            chatName = args[1];
            passwords = args[2];
            replace_passwords = args[3];

            PrivateChatClient  crc = new PrivateChatClient(args[0], args[1], args[2],args[3]);
            crc.receive(); // branch main thread into client program object to be the receive thread
    
            }

        else {
            System.out.println("Please enter Server Address, Chat Name, password and optional replacement password!!!"); 
            return;
        }
    
}
    catch(Exception e){
        System.out.println(e);
        return;
    }
    
    }
    public void receive() // main (loading) thread enters here!
    {          
        String newLine = System.lineSeparator(); // new-line character.
        while(true) // "CAPTURE" the main (loading) thread 
           {
           try {
                Object messageFromServer = ois.readObject(); // no casting - could be anything!
                if (messageFromServer instanceof String) // it's a chat message
                {
                String chatMessage = (String) messageFromServer; // make a pointer of type String 
                textArea.append(newLine + chatMessage);
                // auto-scroll textArea to bottom line so the last message will be visible.
                textArea.setCaretPosition(textArea.getDocument().getLength()); // really?
                }
                else if (messageFromServer instanceof String[]) // an array of whos-in or whos-not-in
                {
               String[] chatNames = (String[]) messageFromServer; // make a pointer of type String[]
               boolean foundIt = false; 
               for (String name : chatNames) // see if this is an IN or OUT list by if it contains THIS client!
                   if (name.equalsIgnoreCase(chatName))
                      { 
                      System.out.println("Who's-in from server: " + Arrays.toString(chatNames));
                      whosNotInList.setListData(chatNames); // show in the JList on the GUI
                      foundIt = true;
                      break;
                      }
               if (foundIt == false) 
                 {
                      System.out.println("Who's-not-in from server: " + Arrays.toString(chatNames));
                      whosNotInList.setListData(chatNames); // show in the JList on the GUI
                 }
                }
                else System.out.println("Unexpected object type received from server: " + messageFromServer);

               
               }
           catch(Exception e)
               {textArea.setBackground(Color.pink);
                textArea.setText("Connection lose, please close the client and restart to reestablish connection to the server");
                textField.setEditable(false);
                privateTextField.setEditable(false); // keep user from trying to send any more private messages.
                clearButton.setEnabled(false); // make button unpushable.
                return ;    // 资源优化大师级
               }
           }
    }

   

    
}