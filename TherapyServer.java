
import java.io.*;
import java.net.*;
public class TherapyServer{
public static void main(String[] args)  throws Exception {
    String[] answers = {"Absolutely", "Certainly Not!", "Forget it!", "Ask Your Mother", "I don't think so...",
                        "Are you kidding?", "Not Today!", "In your dreams!", "It's not looking good.",
                        "Why not?", "It's OK with me!", "Sounds good!", "Let's do it!","It's Only A Matter Of Time..."};
    int ServerPort = 1111;
    ServerSocket ss ;
    try {
      ss =  new ServerSocket(ServerPort);       //reserved socket port number for the client
    } 
    catch (Exception e){
        System.out.println("port" + ServerPort + "is not available.");
        System.out.println("An already-running version of the server may need be terminated. ");
        return ;        //terminate
    }
    System.out.println("Server is up  at " + InetAddress.getLocalHost().getHostAddress() +
    " waiting for a Client to connect on port " + ss.getLocalPort());
    while (true)
    {
        Socket s = ss.accept();                 //wait for client to connect to the server socket; S->SS->S
        DataInputStream dis = new DataInputStream(s.getInputStream()); // connect input from client wit DataInputsStream
        String message = dis.readUTF();                                // read the input from client 
        DataOutputStream dos = new DataOutputStream(s.getOutputStream());      //connect output from server with DataOutputStream
        int index = (int)(Math.random()*answers.length);
        dos.writeUTF("Got your message: '" + message + "'" + "\n"+answers[index]);
        dos.close();
        System.out.println("received: '" + message + "' from " + s.getInetAddress().getHostAddress());



    }
    }
    
}