package com.joyveb.tlol.protocol;

import java.nio.ByteBuffer;

public final class GangInviteEchoBody extends MsgBody {
	public static final GangInviteEchoBody INSTANCE = new GangInviteEchoBody();

	private GangInviteEchoBody() {
	}

	private boolean agree;

	private long gangid;

	@Override
	public boolean readBody(final ByteBuffer body) {
		if (body.remaining() < 4 + 1 + 8)
			return false;

		bodyLen = body.getInt();

		agree = (body.get() == 1) ? true : false;

		gangid = body.getLong();

		return true;
	}

	public void setAgree(final boolean agree) {
		this.agree = agree;
	}

	public boolean isAgree() {
		return agree;
	}

	public void setGangid(final long gangid) {
		this.gangid = gangid;
	}

	public long getGangid() {
		return gangid;
	}

}
