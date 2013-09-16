package tests.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;

public class test4 {

	
	public static void main(String[] args) throws IOException {
		ServerSocket serverSocket=new ServerSocket(3009);//TODO : Pref
		DatagramSocket s = new DatagramSocket(3009);
		System.out.println(serverSocket.getLocalSocketAddress());
		System.out.println(s.getLocalSocketAddress());
		
	}
	
	
}
