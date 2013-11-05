package com.jconnect.core.event;


public class RequestHandler {

	private int iniCount;
	private int receivedCount = 0;
	private double timeOut;
	private RequestCallBack callBack;

	public RequestHandler(int maxAnswerCount, double millisTimeOut,
			RequestCallBack callBack) {
		iniCount = maxAnswerCount;
		this.timeOut = millisTimeOut;
		this.callBack = callBack;

	}

	/**
	 * 
	 * @return true if the handle has to be removed
	 */
	public boolean isOver() {
		if (System.currentTimeMillis() > timeOut)
			return true;
		return false;
	}

	/**
	 * 
	 * @param me
	 *            Message to handle
	 * @return true if the handle has to be removed
	 */
	public boolean handleMessage(MessageEvent me) {
		switch (me.getState()) {
		case MESSAGE_RECEIVED:
			callBack.onRequestEvent(new RequestEvent(
					RequestEvent.State.ANSWER_RECEIVED, me.getMessage()));
			return true;

		case SEND_FAIL:
			callBack.onRequestEvent(new RequestEvent(
					RequestEvent.State.SEND_FAIL, null));
			break;
		default:
			break;
		}

		receivedCount++;
		if (receivedCount < iniCount)
			return false;
		return true;

	}

}
