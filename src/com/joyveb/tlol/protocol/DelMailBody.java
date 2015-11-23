package com.joyveb.tlol.protocol;

import java.nio.ByteBuffer;

public final class DelMailBody extends MsgBody {
	public static final DelMailBody INSTANCE = new DelMailBody();

	private DelMailBody() {
	}

	private long mailId;

	@Override
	public boolean readBody(final ByteBuffer body) {
		bodyLen = body.getInt();
		mailId = body.getLong();
		return true;
	}

	public long getMailId() {
		return mailId;
	}

}
