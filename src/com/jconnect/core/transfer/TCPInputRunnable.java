package com.jconnect.core.transfer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

import com.jconnect.core.Gate;
import com.jconnect.core.event.TransferEvent;
import com.jconnect.core.message.Message;
import com.jconnect.core.model.RouteModel;
import com.jconnect.core.model.RouteModel.TransportType;

/**
 * Manages the TCP messages' reception 
 */
public class TCPInputRunnable extends AbstractSocketRunnable {

	private Socket usingSocket;

	public TCPInputRunnable(Gate parent, Socket usingSocket) {
		super(parent);
		this.usingSocket = usingSocket;
	}

	public void run() {
		if (usingSocket.isClosed()) {
			parent.addEvent(new TransferEvent(TransferEvent.State.SOCKET_CLOSED, TransportType.TCP));
		}

		else {
			try {
				BufferedReader in = new BufferedReader(new InputStreamReader(
						usingSocket.getInputStream()));
				String read = new String();
				while (read != null) {

					read = in.readLine();
					if (read != null){
						TransferEvent e = new TransferEvent(	TransferEvent.State.MESSAGE_RECEIVED, TransportType.TCP);
						e.setMessage(Message.parse(read));
						e.setRoute(new RouteModel(e.getMessage().getPeer(), new InetSocketAddress(usingSocket.getInetAddress(),usingSocket.getPort()), TransportType.TCP));
						parent.addEvent(e);
					}
						
				}
				

			} catch (SocketTimeoutException ex) {
				TransferEvent  e =new TransferEvent(	TransferEvent.State.INPUT_TIME_OUT, TransportType.TCP);
				e.error=ex;
				parent.addEvent(e);
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

}
