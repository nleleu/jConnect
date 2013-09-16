package com.jconnect.core.transfer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;

import com.jconnect.core.Gate;


public class MulticastUDPListenerThread extends AbstractSocketThread {



	private MulticastSocket serverSocket;





	public MulticastUDPListenerThread (Gate parent,MulticastSocket serverSocket)
	{
		super(parent);
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
				String s = new String(recv.getData(), recv.getOffset(), recv.getLength());
				parent.handlerMessage(serverSocket.getRemoteSocketAddress(), s);

			}



		} catch (IOException e) {

		}


	}
}
