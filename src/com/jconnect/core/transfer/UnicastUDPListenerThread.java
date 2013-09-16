package com.jconnect.core.transfer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import com.jconnect.core.Gate;


public class UnicastUDPListenerThread extends AbstractSocketThread {



	private DatagramSocket datagramSocket;





	public UnicastUDPListenerThread (Gate parent,DatagramSocket datagramSocket)
	{
		super(parent);
		this.datagramSocket = datagramSocket;

	}


	public void run()
	{


		try {
			while(true)
			{
				byte[] buf = new byte[1000]; // TODO : constante ?
				DatagramPacket recv = new DatagramPacket(buf, buf.length);
				datagramSocket.receive(recv);
				String s = new String(recv.getData(), recv.getOffset(), recv.getLength());
				
				parent.handlerMessage(datagramSocket.getRemoteSocketAddress(), s);
				
			}



		} catch (IOException e) {

		}


	}
}
