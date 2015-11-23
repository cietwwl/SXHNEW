package com.joyveb.tlol.protocol;

import java.nio.ByteBuffer;

public final class DelAllMailBody extends MsgBody {
	public static final DelAllMailBody INSTANCE = new DelAllMailBody();

	private DelAllMailBody() {
	}

	private short delMailType;

	public short getDelMailType() {
		return delMailType;
	}

	public void setDelMailType(short delMailType) {
		this.delMailType = delMailType;
	}

	@Override
	public boolean readBody(final ByteBuffer body) {
		bodyLen = body.getInt();
		delMailType = body.getShort();
		return true;
	}


}
