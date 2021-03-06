package com.jconnect.core;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.google.gson.JsonSyntaxException;
import com.jconnect.core.event.TransferEvent;
import com.jconnect.core.message.Message;
import com.jconnect.core.model.RouteModel;
import com.jconnect.core.model.RouteModel.TransportType;
import com.jconnect.core.peergroup.AbstractPeerGroup;
import com.jconnect.util.Constants;

/**
 * Thread server constantly listening on Multicast Socket
 */
public class ServerMulticastThread extends Thread {

	private MulticastSocket serverSocket;
	private Gate parent;
	private Logger log = Logger.getLogger(AbstractPeerGroup.class.getName());

	public ServerMulticastThread(Gate parent, MulticastSocket serverSocket) {
		this.parent = parent;
		this.serverSocket = serverSocket;

	}

	public void run() {

		try {
			while (getState() == Thread.State.RUNNABLE) {
				byte[] buf = new byte[Constants.SERVER_BUFFER_SIZE];
				DatagramPacket recv = new DatagramPacket(buf, buf.length);
				serverSocket.receive(recv);
				String data = new String(recv.getData(), recv.getOffset(),
						recv.getLength());
				try {
					TransferEvent ev = new TransferEvent(
							TransferEvent.State.MESSAGE_RECEIVED,
							TransportType.MULTICAST);
					ev.setMessage(Message.parse(data));
					ev.setRoute(new RouteModel(ev.getMessage().getPeer(),
							new InetSocketAddress(recv.getAddress(), recv
									.getPort()), TransportType.MULTICAST));

					parent.addEvent(ev);
				} catch (JsonSyntaxException e) {
					System.out.println(data);
					e.printStackTrace(); //TODO
				}

			}

		} catch (IOException e) {
			log.log(Level.INFO,
					"Server MULTICAST thread close :" + e.getMessage());
		}

	}
}
