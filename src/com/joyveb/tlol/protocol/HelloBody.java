package com.joyveb.tlol.protocol;

import java.nio.ByteBuffer;

public final class HelloBody extends MsgBody {
	public static final HelloBody INSTANCE = new HelloBody();

	private HelloBody() {
	}
	@Override
	public boolean readBody(final ByteBuffer body) {
		bodyLen = body.getInt();
		return bodyLen == 0 && !body.hasRemaining();
	}
}
