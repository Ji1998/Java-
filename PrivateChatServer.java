//ECE492-Lab3-Guangsen Ji
//2020/1/27
//This lab is to build a server for multple users and the server is able to support privte message and password. 

import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.concurrent.*;

public class PrivateChatServer implements Runnable
{

    private ServerSocket ss;
    ConcurrentHashMap<String,ObjectOutputStream> whosIn = new ConcurrentHashMap<String,ObjectOutputStream>();
    ConcurrentHashMap<String,String> passwords = new ConcurrentHashMap<String,String>();


    public PrivateChatServer() throws Exception 
    {
        int ServerPort = 4444;
        try 
        {
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


        try 
        {
            ss =  new ServerSocket(ServerPort);                     //reserved socket port number for the client
        }
        catch(Exception e)
        {
            throw new IllegalArgumentException("Port 4444 is not available");
        }

        System.out.println("ChatRoomServer is up at "+ InetAddress.getLocalHost().getHostAddress() + " on port " + ss.getLocalPort());
        new Thread(this).start(); // a new thread only get into "run" method

    } 


    public static void main(String[] args)
    {
        System.out.println("Guangsen Ji--Lab3--@2020");
        if( args.length!= 0) 
            System.out.println("All Parameters will be ignored -- Guangsen Ji");

       try
       {
           PrivateChatServer crs = new PrivateChatServer();
       }
       catch(Exception e)
       {
           System.out.println(e);
       }

    }

    public void run()   //client threads enter here
    {
        Socket             s                = null;
        ObjectInputStream  ois              = null;
        ObjectOutputStream oos              = null;
        String             chatName         = null;
        String             clientAddress    = null;
        String joinMessage     = null; // the first message received
        String              password        = null;
        String enteredPassword = null;
        String storedPassword  = null;
        String replacePassword = null;
        

        try {
            s = ss.accept(); // wait for next client to connect
            clientAddress = s.getInetAddress().getHostAddress();
            System.out.println("New client connecting from " + clientAddress);
            ois = new ObjectInputStream(s.getInputStream());  // Don't make ois and oos in
            joinMessage = (String) ois.readObject(); 
            oos = new ObjectOutputStream(s.getOutputStream());// avoids a run-time hang!   
            
            
            }
        catch(Exception e) // connecting client may not be using oos,
            {                                                                               // or firstMessage was not a String
            System.out.println("Connect/join failed from " + clientAddress);
            return; // return to the Thread object to terminate this client thread. 
            }
        finally // create a next-client thread whether catch was entered or not. 
            {
            new Thread(this).start(); 
            }
       joinMessage = joinMessage.trim();
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
        if (chatName.contains(" ") || enteredPassword.contains(" ") || replacePassword.contains(" "))
        {
            try {
                oos.writeObject("Invalid chatName or password. Must not be empty or contain an imbedded blank.");
                 s.close();
             }
            catch(Exception e) {}
            return; // kill client thread
        }
        if (passwords.containsKey(chatName))
        {   
        storedPassword = passwords.get(chatName);
        if (!enteredPassword.equals(storedPassword)) // case-sensitive compare
           {
           try {
               oos.writeObject("Incorrect password for " + chatName); // send err msg as join reply.
               oos.close(); // drop connection
               }
           catch(Exception e) {}
           System.out.println("Invalid password (" + enteredPassword + ") received for " + chatName
                            + " should be (" + storedPassword + ")");
           return; // this client thread returns to it's Thread object which terminates it.
           }
        
         else // the providedPassword DID match the storedPassword
           {
           if (replacePassword.length() > 0) // a password change was requested
              { 
                System.out.println("replace request");
                  passwords.replace(chatName, replacePassword);
                  savePasswords(); // save new pw on disk
              }
            }
        }
           //already exist user password pass

           try {                                                               
            oos.writeObject("Welcome to the chat room " + chatName + " !"); // send "join is successful" 
            // And, if they are a never-before client, add their pw to the collection:
            if (!passwords.containsKey(chatName))    
               {
                System.out.println("new USER NCIE !!!!!!");
               if (replacePassword.length() == 0)
               {
                   passwords.put(chatName, enteredPassword); 
               }
                 else
                 {
                   passwords.put(chatName, replacePassword);
                 }
                 savePasswords();
               }
            }                                                               
        catch(Exception e)
            {
            System.out.println("Error sending join reply to client " + chatName);
            return; // terminate this new client thread.
            }
            if (whosIn.containsKey(chatName)) // check for chat name already-in 
            {                              // IF SO THIS IS A *** REJOIN *** situation.
            ObjectOutputStream previousSessionOOS = whosIn.get(chatName);
            try {
                previousSessionOOS.writeObject("Session terminated due to join of client at another location.");
                previousSessionOOS.close(); // terminate the connection to Bubba's previous location
                }
            catch(Exception e) {}
            whosIn.replace(chatName, oos); // replace client's old oos with new oos in whosIn list.
            System.out.println(chatName + " is re-joining.");
            String[] chatNames = whosIn.keySet().toArray(new String[0]);
            Arrays.sort(chatNames);
            //String whosInNow = Arrays.toString(chatNames);
            try {
                oos.writeObject(chatNames);
                }
            catch(Exception e) {}
            }
          else // Do normal join processing.
            {
          //  whosIn.put(chatName, oos); // add new client to whosIn list.
          //  sendToAll("Say hello to " + chatName + " who just joined the chat room!");
          //  System.out.println(chatName + " is joining.");
          //  String[] chatNames = whosIn.keySet().toArray(new String[0]);
          //  Arrays.sort(chatNames);
          //  String whosInNow = Arrays.toString(chatNames);
          //  System.out.println(whosInNow); // show who's in on server console.
          //  sendToAll(chatNames);  
          whosIn.put(chatName, oos); // add new client to whosIn list.
          sendToAll("Say hello to " + chatName + " who just joined the chat room!");
          System.out.println(chatName + " is joining with password " + passwords.get(chatName));
          String[] ChatNames        = whosIn.keySet().toArray(new String[0]);
          String[] passwordsChatNames = passwords.keySet().toArray(new String[0]);
          Vector<String> whosNotInVector = new Vector<String>();
          for (String name : passwordsChatNames) // initialize Vector with passwordsChatNames
               whosNotInVector.add(name);
          for (String name : ChatNames)        // subtract whosIn names from passwords names
               whosNotInVector.remove(name);     // to leave whosNotIn names in Vector.
          String[] notInChatNames = whosNotInVector.toArray(new String[0]);
          Arrays.sort (ChatNames);
          Arrays.sort(passwordsChatNames);
          Arrays.sort(notInChatNames);
          System.out.println("Currently in the chat room: " + Arrays.toString(ChatNames)); // show who's in on server console.
          sendToAll(ChatNames);  
          sendToAll(notInChatNames);     
            }
            

        chatName = chatName.toUpperCase().trim();
        // Check to see if this chatName is valid and has not already joined:
        

        
        try {                                                               
            oos.writeObject("Welcome to the chat room " + chatName + " !"); // send "join is successful" 
            whosIn.put(chatName,oos);  //add the joining client (and their OOS) to the whosIn collection
            String[] whosInArray = whosIn.keySet().toArray(new String[0]);
            Arrays.sort(whosInArray);
            String whosInNow = Arrays.toString(whosInArray);
            sendToAll(whosInNow);
            sendToAll("Hello to " + chatName + " who just joined!");

            }                                                               
        catch(Exception e)
            {
            System.out.println("Error sending join reply to client " + chatName);
            return; // terminate this new client thread.
            }
         try{
            while(true) 
            {                         
               String messageFromClient = (String) ois.readObject();//wait for MY client to say something
               System.out.println("Received '" + messageFromClient + "' from " + chatName); // (debug trace)
             //  while(messageFromClient.length() > 0)
              //     {
               //    if (messageFromClient.startsWith("["))
                //    messageFromClient = messageFromClient.substring(1); // drop 1st char
                 //  else break;
                  //  }
              sendToAll(chatName + " says: " + messageFromClient);
           }  
         }   
         catch(Exception e){
                    //System.out.println("leaving processing");
                    if (whosIn.get(chatName) != oos) return;
                    whosIn.remove(chatName);
                    System.out.println(chatName+"is leaving the chatroom");
                    sendToAll("Goodbye" + chatName + "who just left the chatroom");

                    String[] ChatNames        = whosIn.keySet().toArray(new String[0]);
                    String[] passwordsChatNames = passwords.keySet().toArray(new String[0]);
                    Vector<String> whosNotInVector = new Vector<String>();
                    for (String name : passwordsChatNames) // initialize Vector with passwordsChatNames
                        whosNotInVector.add(name);
                    for (String name : ChatNames)        // subtract whosIn names from passwords names
                         whosNotInVector.remove(name);     // to leave whosNotIn names in Vector.
                    String[] notInChatNames = whosNotInVector.toArray(new String[0]);
                    Arrays.sort (ChatNames);
                    Arrays.sort(passwordsChatNames);
                     Arrays.sort(notInChatNames);
                    System.out.println("Currently in the chat room: " + Arrays.toString(ChatNames)); // show who's in on server console.
                    sendToAll(ChatNames);  
                     sendToAll(notInChatNames);  

            }

    

    }

    private synchronized void sendToAll(Object message) // force client threads to enter one-at-a-time
    {
        ObjectOutputStream[] oosArray = whosIn.values().toArray(new ObjectOutputStream[0]);
        for (ObjectOutputStream clientOOS : oosArray) // the "for each" syntax specifies the type and gives a
            {                                         // name to each entry/row in the list. (So each entry must 
               try{ clientOOS.writeObject(message);  }        // share some common type). Then, following    
               catch (IOException e){}      
            }                                         // the ":", the name of the collection is specified.
                                              // The loop body processes the "current object" in the list. 
            


    }
    
    private synchronized void savePasswords() // make calling client threads enter one-at-a-time
        {
        try {
            System.out.println("Previously in the chat room: " + passwords);

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









}