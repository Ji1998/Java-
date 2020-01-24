//ECE492-Lab2-Guangsen Ji
//2020/1/21
//This lab is to build a GUI window to let the user chat through the GUI window after connecting with the server


import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public class ChatRoomClient                 implements ActionListener{

    Socket s;
    ObjectInputStream ois;
    ObjectOutputStream dos;
    JFrame window = new JFrame("ChatRoomClient");
    JLabel whoInLabel = new JLabel("who is in the chat room");
    JTextField textField = new JTextField("Enter a chat message to send and press ENTER");
    JTextArea textArea = new JTextArea("Received message show here ");
    JScrollPane messageScrollPane = new JScrollPane(textArea);

    public ChatRoomClient(String serverAddress,String chatName)
    {
        if (serverAddress.contains(" ") || chatName.contains(" "))
        throw new IllegalArgumentException("Arguments cannot contain embeded blanks");
        try{
             s = new Socket (serverAddress, 2222);
             dos = new ObjectOutputStream(s.getOutputStream()); //connect output stream with the socket
             dos.writeObject(chatName);
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
        window.getContentPane().add(whoInLabel,"North"); //按钮
        window.getContentPane().add(messageScrollPane, "Center"); //用户反馈界面
        window.getContentPane().add(textField, "South"); //用户输入界面
        window.setSize(500,400); // default size is ICON size
        window.setLocation(500,0);//x:from left to right, y:down forn the top 
        whoInLabel.setBackground(Color.white);
        textField.setBackground(Color.yellow);
        textArea.setFont(new Font("default", Font.BOLD, 20));
        window.setVisible(true);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setTitle(chatName + "'s CHAT ROOM");
        textField.addActionListener(this);//call when ENTER,give address of MyFirstGUI program to the enter program
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
    }
   
    public void actionPerformed(ActionEvent ae)
    {   String chatMessageToSend = textField.getText().trim();
        if (chatMessageToSend.length() == 0) return; // ignore blank messages.
        textField.setText(""); // clear input area
        try {
            dos.writeObject(chatMessageToSend);
            }
        catch(Exception e)
            {
            // do nothing here! (ois.readObject() will be getting the same error)
            }
    
        }


    public static void main(String[] args)
    {   
    try{
        String ServerAddress;
        String chatName;
        // System.out.println("Welcome! Please enter server address and chat name");
        if (args.length == 2){
        System.out.println(" Guangsen Ji Server Address and Chat Name get!");
        ServerAddress = args[0];
        chatName = args[1];
        ChatRoomClient crc = new ChatRoomClient(args[0], args[1]);
        crc.receive(); // branch main thread into client program object to be the receive thread

        }
        else {
            System.out.println("Please enter Server Address and Chat Name !!!"); 
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
                String chatMessageFromServer = (String) ois.readObject(); 
                if (chatMessageFromServer.startsWith("[")){
                    whoInLabel.setText(chatMessageFromServer);
                }
                else {
                    textArea.append(newLine + chatMessageFromServer);
                    // now scroll the textArea to the bottom line so the last message will be visible.
                    textArea.setCaretPosition(textArea.getDocument().getLength()); // really?
                }

               
               }
           catch(Exception e)
               {textArea.setBackground(Color.pink);
                textArea.setText("Connection lose, please close the client and restart to reestablish connection to the server");
                textField.setEditable(false);
                return ;    // 资源优化大师级
               }
           }
    }

   

    
}