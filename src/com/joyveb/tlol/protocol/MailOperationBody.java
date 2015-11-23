package com.joyveb.tlol.protocol;

import java.nio.ByteBuffer;

public final class MailOperationBody extends MsgBody {
	public static final MailOperationBody INSTANCE = new MailOperationBody();

	private MailOperationBody() {
	}

	private long mailId;

	@Override
	public boolean readBody(final ByteBuffer body) {
		bodyLen = body.getInt();
		mailId = body.getLong();
		return false;
	}

	public long getMailId() {
		return mailId;
	}

}
