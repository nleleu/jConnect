package com.jconnect.core.transfer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import com.jconnect.core.Gate;
import com.jconnect.core.event.TransferEvent;
import com.jconnect.core.message.Message;
import com.jconnect.core.model.RouteModel;
import com.jconnect.core.model.RouteModel.TransportType;

/**
 * Manages the UDP messages' reception 
 */
public class UDPOutputRunnable  extends AbstractSocketRunnable {


	private Message message;
	private DatagramSocket usingSocket;
	private RouteModel route;


	public UDPOutputRunnable (Gate parent, DatagramSocket usingSocket, RouteModel routeModel, Message message)
	{
		super(parent);
		this.usingSocket =usingSocket;
		this.message = message;
		this.route =routeModel;
	}

	public void run()
	{
		
			try {
				byte[] buf = message.toString().getBytes(); 
				DatagramPacket packet = new DatagramPacket(buf, buf.length,route.getSocketAddress().getAddress(),route.getSocketAddress().getPort());
				
				usingSocket.send(packet);

				TransferEvent ev = new TransferEvent(TransferEvent.State.SEND_SUCCESS, TransportType.UDP);
				ev.setMessage(message);
				parent.addEvent(ev);
				
			} catch (IOException ex) {
				TransferEvent e = new TransferEvent(TransferEvent.State.SEND_FAIL,TransportType.UDP);
				e.error = ex;
				e.setMessage(message);
				parent.addEvent(e);
			}


	}



}
