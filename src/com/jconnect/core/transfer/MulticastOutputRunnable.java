package com.jconnect.core.transfer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import com.jconnect.core.Gate;
import com.jconnect.core.event.TransferEvent;
import com.jconnect.core.message.Message;
import com.jconnect.core.model.RouteModel.TransportType;

/**
 * Manages the multicast messages' emission 
 */
public class MulticastOutputRunnable  extends AbstractSocketRunnable {


	private Message message;
	private DatagramSocket usingSocket;
	private InetAddress group;
	private int port;


	public MulticastOutputRunnable (Gate parent, DatagramSocket usingSocket,InetAddress group,int port,Message message)
	{
		super(parent);
		this.usingSocket =usingSocket;
		this.message = message;
		this.port = port;
		this.group = group;
	}

	public void run()
	{
		
		if(usingSocket.isClosed())
		{
			parent.addEvent(new TransferEvent(usingSocket.getRemoteSocketAddress(),	TransferEvent.State.SOCKET_CLOSED, TransportType.MULTICAST));
		}

		else
		{
			try {
				byte[] buf = message.toString().getBytes(); 
				DatagramPacket packet = new DatagramPacket(buf, buf.length,group,port);
				usingSocket.send(packet);
				
				TransferEvent e = new TransferEvent(null, TransferEvent.State.SEND_SUCCESS, TransportType.MULTICAST);
				e.setMessage(message);
				parent.addEvent(e);

			} catch (IOException e) {
				e.printStackTrace();
			}

		}





	}



}
