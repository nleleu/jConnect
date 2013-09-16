package com.jconnect.core.transfer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketTimeoutException;

import com.jconnect.core.Gate;

public class UnicastTCPListenerThread  extends AbstractSocketThread {

private Socket usingSocket;



	public UnicastTCPListenerThread (Gate parent, Socket usingSocket)
	{
		super(parent);
		this.usingSocket = usingSocket;
	}

	public void run()
	{
		System.out.println("Debut d'un thread d'écoute sur "+usingSocket.getRemoteSocketAddress());
		if(usingSocket==null)
		{
			System.out.println("1");
			parent.handleEndOfListener(null, SOCKET_STATUS_UNKNOWN_ERROR);

		}
		else if(usingSocket.isClosed())
		{
			System.out.println("2");
			parent.handleEndOfListener(usingSocket.getRemoteSocketAddress(), SOCKET_STATUS_CLOSED);
		}

		else
		{
			try {
				BufferedReader in = new BufferedReader (new InputStreamReader (usingSocket.getInputStream()));
				String read = new String();
				while(read!=null)
				{

					read = in.readLine();
					if(read!=null)
						parent.handlerMessage(usingSocket.getRemoteSocketAddress(), read);

				}
				parent.handleEndOfListener(usingSocket.getRemoteSocketAddress(), SOCKET_STATUS_CLOSED);

			}catch (SocketTimeoutException e)
			{

				parent.handleEndOfListener(usingSocket.getRemoteSocketAddress(), SOCKET_STATUS_TIMEOUT);
			} catch (IOException e) {
				System.out.println("3");
				parent.handleEndOfListener(usingSocket.getRemoteSocketAddress(), SOCKET_STATUS_UNKNOWN_ERROR);
			}

		}





	}



}
