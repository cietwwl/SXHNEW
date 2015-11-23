package com.joyveb.tlol.protocol;

import java.nio.ByteBuffer;

public final class NameListBody extends MsgBody {
	public static final NameListBody INSTANCE = new NameListBody();

	private NameListBody() {
	}

	private byte type;
	@Override
	public boolean readBody(final ByteBuffer body) {
		if (body.remaining() < 4 + 1 + 2) {
			return false;
		}
		bodyLen = body.getInt();
		type = body.get();
		end = body.getShort();

		return true;
	}

	public void setType(final byte type) {
		this.type = type;
	}

	public byte getType() {
		return type;
	}

}
