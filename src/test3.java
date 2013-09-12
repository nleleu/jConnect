import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;


public class test3 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Socket socket;
        BufferedReader in;
        PrintWriter out;
 
        try {
         
            socket = new Socket(InetAddress.getLocalHost(),3002,InetAddress.getLocalHost(),3009);  
                System.out.println("Demande de connexion");
 
               
                 
                out = new PrintWriter(socket.getOutputStream());
                out.println("Client message !");
                out.flush();

                socket.close();
                
        }catch (UnknownHostException e) {
             
            e.printStackTrace();
        }catch (IOException e) {
             
            e.printStackTrace();
        }

	}

}
