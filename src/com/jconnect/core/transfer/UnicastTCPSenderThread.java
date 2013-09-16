package com.jconnect.core.transfer;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import com.jconnect.core.Gate;

public class UnicastTCPSenderThread  extends AbstractSocketThread {


	private String message=new String();
	private Socket usingSocket;

	public UnicastTCPSenderThread (Gate parent, Socket usingSocket,String message)
	{
		super(parent);
		this.usingSocket = usingSocket;
		this.message = message;
	}

	public void run()
	{
		System.out.println("Debut d'un thread d'envoi sur "+usingSocket.getRemoteSocketAddress());
		if(usingSocket==null)
		{
			System.out.println("1");
			parent.handleEndOfSender(null, SOCKET_STATUS_UNKNOWN_ERROR,message);

		}
		else if(usingSocket.isClosed())
		{
			System.out.println("2");
			parent.handleEndOfSender(usingSocket.getRemoteSocketAddress(), SOCKET_STATUS_CLOSED,message);
		}

		else
		{
			try {


				PrintWriter out = new PrintWriter(usingSocket.getOutputStream());
				out.println(message);
				out.flush();
				parent.handleEndOfSender(usingSocket.getRemoteSocketAddress(), SOCKET_STATUS_OK,message);
			} catch (IOException e) {
				System.out.println("3");
				parent.handleEndOfSender(usingSocket.getRemoteSocketAddress(), SOCKET_STATUS_UNKNOWN_ERROR,message);
			}

		}





	}



}
