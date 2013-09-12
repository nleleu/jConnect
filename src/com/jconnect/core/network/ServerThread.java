package com.jconnect.core.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.jconnect.core.Gate;

public class ServerThread extends Thread {

	private Gate parent;
	private ServerSocket serverSocket;





	public ServerThread (Gate parent,ServerSocket serverSocket)
	{
		this.parent = parent;
		this.serverSocket = serverSocket;

	}

	public void run()
	{



		while(true)
		{


			try {
				Socket socketclient = serverSocket.accept();

				socketclient.setSoTimeout(1000); //TODO : Prefs
				parent.addSocket(socketclient);
			} catch (IOException e) {
				
				
			}


		}





	}

}
