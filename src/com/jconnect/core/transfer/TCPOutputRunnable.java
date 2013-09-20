package com.jconnect.core.transfer;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import com.jconnect.core.Gate;
import com.jconnect.core.event.TransferEvent;

public class TCPOutputRunnable  extends AbstractSocketRunnable {


	private String message=new String();
	private Socket usingSocket;

	public TCPOutputRunnable (Gate parent, Socket usingSocket,String message)
	{
		super(parent);
		this.usingSocket = usingSocket;
		this.message = message;
	}

	public void run()
	{
		if(usingSocket.isClosed())
		{
			TransferEvent e = new TransferEvent(usingSocket.getRemoteSocketAddress(),	TransferEvent.State.SOCKET_CLOSED);
			e.setData(message);
			parent.addEvent(e);
			
		}

		else
		{
			try {


				PrintWriter out = new PrintWriter(usingSocket.getOutputStream());
				out.println(message);
				out.flush();
				TransferEvent e = new TransferEvent(usingSocket.getRemoteSocketAddress(),	TransferEvent.State.SEND_SUCCESS);
				parent.addEvent(e);
			} catch (IOException ex) {
				TransferEvent e = new TransferEvent(usingSocket.getRemoteSocketAddress(),	TransferEvent.State.SEND_FAIL);
				e.error = ex;
				e.setData(message);
				parent.addEvent(e);
			}

		}





	}



}
