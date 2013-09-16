package com.jconnect.core.transfer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import com.jconnect.core.Gate;

public class MulticastUDPSenderThread  extends AbstractSocketThread {


	private String message=new String();
	private DatagramSocket usingSocket;
	private InetAddress group;
	private int port;


	public MulticastUDPSenderThread (Gate parent, DatagramSocket usingSocket,InetAddress group,int port,String message)
	{
		super(parent);
		this.usingSocket =usingSocket;
		this.message = message;
		this.port = port;
		this.group = group;
	}

	public void run()
	{
		System.out.println("Debut d'un thread d'envoi sur "+usingSocket);
		if(usingSocket==null)
		{
			System.out.println("1");
			parent.handleEndOfSender(null, SOCKET_STATUS_UNKNOWN_ERROR,message);

		}
		else if(usingSocket.isClosed())
		{
			System.out.println("2");
			parent.handleEndOfSender(null, SOCKET_STATUS_CLOSED,message);
		}

		else
		{
			try {
				byte[] buf = message.getBytes(); 
				DatagramPacket packet = new DatagramPacket(buf, buf.length,group,port);
				usingSocket.send(packet);
				
				parent.handleEndOfSender(null, SOCKET_STATUS_OK,message);				

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}





	}



}
