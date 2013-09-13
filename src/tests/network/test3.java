package tests.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class test3 {

	
	public static void main(String[] args) throws IOException {
		ServerSocket serverSocket=new ServerSocket(3009);//TODO : Pref
		Socket socketclient = serverSocket.accept();
		BufferedReader in = new BufferedReader (new InputStreamReader (socketclient.getInputStream()));
		System.out.println(in.readLine());
		socketclient.close();
		serverSocket.close();
		
	}
	
	
}
