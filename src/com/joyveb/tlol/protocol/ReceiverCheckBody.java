package com.joyveb.tlol.protocol;

import java.nio.ByteBuffer;

public final class ReceiverCheckBody extends MsgBody {
	public static final ReceiverCheckBody INSTANCE = new ReceiverCheckBody();

	private ReceiverCheckBody() {
	}

	private String ReceiverName;

	@Override
	public boolean readBody(final ByteBuffer body) {
		bodyLen = body.getInt();
		ReceiverName = getStrByLen(body, body.getShort());
		return true;
	}

	public String getReceiverName() {
		return ReceiverName;
	}

	public void setReceiverName(final String receiverName) {
		ReceiverName = receiverName;
	}

}
