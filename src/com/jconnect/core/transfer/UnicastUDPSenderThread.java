package com.jconnect.core.transfer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import com.jconnect.core.Gate;

public class UnicastUDPSenderThread  extends AbstractSocketThread {


	private String message=new String();
	private DatagramSocket usingSocket;
	private int port;
	private InetAddress contact;


	public UnicastUDPSenderThread (Gate parent, DatagramSocket usingSocket,InetAddress dest,int port,String message)
	{
		super(parent);
		this.usingSocket =usingSocket;
		this.message = message;
		this.contact = dest;
		this.port = port;
	}

	public void run()
	{
		System.out.println("Debut d'un thread d'envoi sur "+contact+" "+port);
		if(usingSocket==null)
		{
			System.out.println("1");
			parent.handleEndOfSender(null, SOCKET_STATUS_UNKNOWN_ERROR,message);

		}
		
		else
		{
			try {
				byte[] buf = message.getBytes(); 
				DatagramPacket packet = new DatagramPacket(buf, buf.length,contact,port);

				usingSocket.send(packet);

				parent.handleEndOfSender(usingSocket.getRemoteSocketAddress(), SOCKET_STATUS_OK,message);				

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}





	}



}
