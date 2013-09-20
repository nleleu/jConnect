package com.jconnect.core.transfer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.logging.Logger;

import com.jconnect.core.Gate;
import com.jconnect.core.event.TransferEvent;
import com.jconnect.core.peergroup.AbstractPeerGroup;

public class TCPInputRunnable extends AbstractSocketRunnable {

	private Socket usingSocket;
	private Logger log = Logger.getLogger(AbstractPeerGroup.class.getName());

	public TCPInputRunnable(Gate parent, Socket usingSocket) {
		super(parent);
		this.usingSocket = usingSocket;
	}

	public void run() {
		if (usingSocket.isClosed()) {
			parent.addEvent(new TransferEvent(usingSocket.getRemoteSocketAddress(),	TransferEvent.State.SOCKET_CLOSED));
		}

		else {
			try {
				BufferedReader in = new BufferedReader(new InputStreamReader(
						usingSocket.getInputStream()));
				String read = new String();
				while (read != null) {

					read = in.readLine();
					if (read != null){
						TransferEvent e = new TransferEvent(usingSocket.getRemoteSocketAddress(),	TransferEvent.State.MESSAGE_RECEIVED);
						e.setData(read);
						parent.addEvent(e);
					}
						
				}
				

			} catch (SocketTimeoutException ex) {
				TransferEvent  e =new TransferEvent(usingSocket.getRemoteSocketAddress(),	TransferEvent.State.INPUT_TIME_OUT);
				e.error=ex;
				parent.addEvent(e);
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

}
