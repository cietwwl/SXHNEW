package com.joyveb.tlol.protocol;

import java.nio.ByteBuffer;

public final class GetItemInfoBody extends MsgBody {
	public static final GetItemInfoBody INSTANCE = new GetItemInfoBody();

	private GetItemInfoBody() {
	}

	private long itemid;
	@Override
	public boolean readBody(final ByteBuffer body) {
		if (body.remaining() < 4 + 8 + 2)
			return false;
		bodyLen = body.getInt();

		itemid = body.getLong();

		end = body.getShort();

		return true;
	}

	public void setItemid(final long itemid) {
		this.itemid = itemid;
	}

	public long getItemid() {
		return itemid;
	}
}
