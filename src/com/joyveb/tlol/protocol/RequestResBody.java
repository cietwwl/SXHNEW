package com.joyveb.tlol.protocol;

import java.nio.ByteBuffer;

public final class RequestResBody extends MsgBody {

	private byte resType = -1;
	private short resId = -1;
	private int offset = -1;

	public static final RequestResBody INSTANCE = new RequestResBody();

	private RequestResBody() {
	}

	@Override
	public boolean readBody(final ByteBuffer body) {
		bodyLen = body.getInt();
		resType = body.get();
		resId = body.getShort();
		offset = body.getInt();
		return true;
	}

	public byte getResType() {
		return resType;
	}

	public void setResType(final byte resType) {
		this.resType = resType;
	}

	public short getResId() {
		return resId;
	}

	public void setResId(final short resId) {
		this.resId = resId;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(final int offset) {
		this.offset = offset;
	}

}
