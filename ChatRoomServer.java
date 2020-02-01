//ECE492-Lab2-Guangsen Ji
//2020/1/27
//This lab is to build a multi thread server that can support multiple client to join and leave 

import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.concurrent.*;

public class ChatRoomServer implements Runnable
{

    private ServerSocket ss;
    ConcurrentHashMap<String,ObjectOutputStream> whosIn = new ConcurrentHashMap<String,ObjectOutputStream>();



    public ChatRoomServer() throws Exception 
    {
            int ServerPort = 2222;
            try 
            {
                ss =  new ServerSocket(ServerPort);                     //reserved socket port number for the client
            }
            catch(Exception e)
            {
            throw new IllegalArgumentException("Port 2222 is not available");
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
           ChatRoomServer crs = new ChatRoomServer();
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

        try {
            s = ss.accept(); // wait for next client to connect
            clientAddress = s.getInetAddress().getHostAddress();
            System.out.println("New client connecting from " + clientAddress);
            ois = new ObjectInputStream(s.getInputStream());  // Don't make ois and oos in
            chatName = (String) ois.readObject();             // ADJACENT statements. This
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
        // If we are still running here then s, ois, oos are all good and chatName is a String! 
        // If not, we have dumped this caller and are waiting again (with a new thread) in accept() above for the next client to connect

        chatName = chatName.toUpperCase().trim();
        // Check to see if this chatName is valid and has not already joined:
         if ((chatName.length() == 0) || chatName.contains(" "))
         {
            try {
                    oos.writeObject("Invalid chatName. Must not be empty or contain an imbedded blank.");
                     s.close();
                 }
            catch(Exception e) {}
            return; // kill client thread
        }

        if (whosIn.containsKey(chatName)) // check for duplicate chat name
            {
            try {
                 oos.writeObject("Sorry, chatName " + chatName + " is already in the chat room.");
                s.close();
                }
            catch(Exception e) {}
            return; // kill client thread
            }
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
               while(messageFromClient.length() > 0)
                   {
                   if (messageFromClient.startsWith("["))
                    messageFromClient = messageFromClient.substring(1); // drop 1st char
                   else break;
                    }
              sendToAll(chatName + " says: " + messageFromClient);
           }  
         }   
         catch(Exception e){
                    whosIn.remove(chatName);
                    System.out.println(chatName+"is leaving the chatroom");
                    sendToAll("Goodbye" + chatName + "who just left the chatroom");

                    String[] whosInArray = whosIn.keySet().toArray(new String[0]);
                    Arrays.sort(whosInArray);
                    String whosInNow = Arrays.toString(whosInArray);
                    System.out.println(whosInNow);
                    sendToAll(whosInNow);

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
    




}