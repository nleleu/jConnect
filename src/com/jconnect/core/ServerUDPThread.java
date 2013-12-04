package com.jconnect.core;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.jconnect.core.event.TransferEvent;
import com.jconnect.core.message.Message;
import com.jconnect.core.model.RouteModel;
import com.jconnect.core.model.RouteModel.TransportType;
import com.jconnect.core.peergroup.AbstractPeerGroup;
import com.jconnect.util.Constants;

/**
 * Thread server constantly listening on UDP Socket
 */
public class ServerUDPThread extends Thread {


	private Logger log = Logger.getLogger(AbstractPeerGroup.class.getName());
	private DatagramSocket datagramSocket;
	private Gate parent;





	public ServerUDPThread (Gate parent,DatagramSocket datagramSocket)
	{
		this.parent = parent;
		this.datagramSocket = datagramSocket;

	}


	public void run()
	{


		try {
			while(getState()==Thread.State.RUNNABLE)
			{
				byte[] buf = new byte[Constants.SERVER_BUFFER_SIZE]; 
				DatagramPacket recv = new DatagramPacket(buf, buf.length);
				datagramSocket.receive(recv);
				
				String data = new String(recv.getData(), recv.getOffset(), recv.getLength());
				
				TransferEvent e = new TransferEvent(TransferEvent.State.MESSAGE_RECEIVED, TransportType.UDP);
				e.setMessage(Message.parse(data));
				e.setRoute(new RouteModel(e.getMessage().getPeer(), new InetSocketAddress(recv.getAddress(),recv.getPort()), TransportType.UDP));
				parent.addEvent(e);
				
			}
		} catch (IOException e) {
			log.log(Level.DEBUG, "Server UDP thread close :"+e.getMessage()); 
		}


	}
}
