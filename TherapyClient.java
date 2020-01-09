
import java.io.*;
import java.net.*;
import java.util.*;
public class TherapyClient {
public static void main(String[] args)  throws IOException {
   /* try {
        while (true){

        }

    }
    catch(Exception e){
        System.out.println("A problem has been encountered communicating with the server at " + ServerAddress);
        System.out.println(e);
        return;
    }
*/
    String ServerAddress = "localhost";
    if (args.length == 0){
        System.out.println("Provide a command line parameter as a message to send, server = localhost ");
        ServerAddress = "localhost";
        
    }
     if (args.length==1){
        System.out.println("Provide a command line parameter as a message to send, server = povided by user ");
         ServerAddress = args[0];
    }
    if (args.length > 1){
        System.out.println("WRONG, you are connecting with  ");
        return;
    }
     System.out.println("Welcome to the On Line Therapy System  @2020");
     System.out.println("opening log file ");
        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader keyboard = new BufferedReader(isr);
        FileWriter logFile = new FileWriter("TherapyLog.txt",true);
        BufferedWriter log = new BufferedWriter(logFile);
        log.newLine();
        log.write("New therapy session on " + new Date());
        log.newLine();
        

    try{
        while (true){
            //if(question.length()==0) continue;
            String question = keyboard.readLine().trim();
            if ((question.equalsIgnoreCase("end"))||question.equalsIgnoreCase("stop")||question.equalsIgnoreCase("quit"))
            break;
            Socket s = new Socket (ServerAddress, 1111);
            DataOutputStream dos = new DataOutputStream(s.getOutputStream()); //connect output stream with the socket
            dos.writeUTF(question);          //message send to the socket then to the server
            DataInputStream dis = new DataInputStream(s.getInputStream());
            String reply = dis.readUTF();       //wait and accept server to reply
            System.out.println("Reply from server is :'" + reply + "'"); //print the what server reply
            log.write("Question was : '" + question + "'therapist answer was : " + reply);
            log.newLine();
        }
    System.out.println("closing log file.");
    log.close(); //close of output file writes it to fisk
            
    
    }
    catch (Exception e){ 
        System.out.println("A problem has been encountered communicating with the server at " + ServerAddress);
        System.out.println(e);
        return;
    }


    
}
    
}