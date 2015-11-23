package com.joyveb.tlol.protocol;

import java.nio.ByteBuffer;

public final class RoleMenuBody extends MsgBody {
	public static final RoleMenuBody INSTANCE = new RoleMenuBody();

	private RoleMenuBody() {
	}

	private int userid;
	@Override
	public boolean readBody(final ByteBuffer body) {
		if (body.remaining() < 4 + 4 + 2)
			return false;

		bodyLen = body.getInt();
		userid = body.getInt();

		end = body.getShort();

		return true;
	}

	public void setUserid(final int userid) {
		this.userid = userid;
	}

	public int getUserid() {
		return userid;
	}

}
