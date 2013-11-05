package com.jconnect.core.event;

import com.jconnect.core.message.Message;

/**
 * Wraps a {@link State} and a {@link Message}
 *
 */
public class MessageEvent {

	public enum State {
		MESSAGE_RECEIVED, SEND_SUCCESS, SEND_FAIL
	}

	private State state;
	private Message message;

	public MessageEvent(State state, Message message) {
		this.state = state;
		this.message = message;
	}

	public State getState() {
		return state;
	}

	public Message getMessage() {
		return message;
	}

}
