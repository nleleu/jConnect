package com.jconnect.core.network;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;

import com.jconnect.core.Gate;

public class UnicastSenderThread  extends AbstractSocketThread {


	String message=new String();


	public UnicastSenderThread (Gate parent, Socket usingSocket,String message)
	{
		super(parent,usingSocket);
		this.message = message;
	}

	public void run()
	{
		System.out.println("Debut d'un thread d'envoi sur "+usingSocket.getRemoteSocketAddress());
		if(usingSocket==null)
		{
			System.out.println("1");
			parent.handleEndOfSender(usingSocket.getRemoteSocketAddress(), SOCKET_STATUS_UNKNOWN_ERROR,message);
			
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
                out.println();
                out.flush();
				parent.handleEndOfSender(usingSocket.getRemoteSocketAddress(), SOCKET_STATUS_OK,message);
			}catch (SocketTimeoutException e)
			{
			
				parent.handleEndOfSender(usingSocket.getRemoteSocketAddress(), SOCKET_STATUS_TIMEOUT,message);
			} catch (IOException e) {
				System.out.println("3");
				parent.handleEndOfSender(usingSocket.getRemoteSocketAddress(), SOCKET_STATUS_UNKNOWN_ERROR,message);
			}
		
		}





	}



}
