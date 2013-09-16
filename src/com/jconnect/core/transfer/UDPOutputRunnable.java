package com.jconnect.core.transfer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import com.jconnect.core.Gate;
import com.jconnect.core.transfer.event.TransferEvent;

public class UDPOutputRunnable  extends AbstractSocketRunnable {


	private String message=new String();
	private DatagramSocket usingSocket;
	private int port;
	private InetAddress contact;


	public UDPOutputRunnable (Gate parent, DatagramSocket usingSocket,InetAddress dest,int port,String message)
	{
		super(parent);
		this.usingSocket =usingSocket;
		this.message = message;
		this.contact = dest;
		this.port = port;
	}

	public void run()
	{
		
			try {
				byte[] buf = message.getBytes(); 
				DatagramPacket packet = new DatagramPacket(buf, buf.length,contact,port);

				usingSocket.send(packet);

				TransferEvent ev = new TransferEvent(null, TransferEvent.State.SEND_SUCCESS);
				parent.addEvent(ev);
			} catch (IOException e) {
				e.printStackTrace();
			}


	}



}
