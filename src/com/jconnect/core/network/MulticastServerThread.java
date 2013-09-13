package com.jconnect.core.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.util.Observable;
import java.util.Observer;

import com.jconnect.core.Gate;


public class MulticastServerThread extends Thread {








	private Gate parent;
	private MulticastSocket serverSocket;





	public MulticastServerThread (Gate parent,MulticastSocket serverSocket)
	{
		this.parent = parent;
		this.serverSocket = serverSocket;

	}


	public void run()
	{


		try {
			while(true)
			{
				byte[] buf = new byte[1000]; // TODO : constante ?
				DatagramPacket recv = new DatagramPacket(buf, buf.length);
				serverSocket.receive(recv);
			}



		} catch (IOException e) {

		}


	}
}
