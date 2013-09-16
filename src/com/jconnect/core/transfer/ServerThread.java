package com.jconnect.core.transfer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.jconnect.core.Gate;

public class ServerThread extends AbstractSocketThread {

	private ServerSocket serverSocket;





	public ServerThread (Gate parent,ServerSocket serverSocket)
	{
		super(parent);
		this.serverSocket = serverSocket;

	}

	public void run()
	{
		while(true)
		{

			try {
				Socket socketclient = serverSocket.accept();

				socketclient.setSoTimeout(1000); //TODO : Prefs
				parent.addSocketToListen(socketclient);
			} catch (IOException e) {
				
				
			}


		}





	}

}
