package com.joyveb.tlol.protocol;

import java.nio.ByteBuffer;

public final class NameListDelBody extends MsgBody {
	public static final NameListDelBody INSTANCE = new NameListDelBody();

	private NameListDelBody() {
	}

	public static final byte Friend = 0;
	public static final byte Foe = 1;

	private int roleid;
	private byte type;
	@Override
	public boolean readBody(final ByteBuffer body) {
		if (body.remaining() < 4 + 4 + 1 + 2)
			return false;

		bodyLen = body.getInt();
		roleid = body.getInt();
		type = body.get();
		end = body.getShort();

		return true;
	}

	public void setRoleid(final int roleid) {
		this.roleid = roleid;
	}

	public int getRoleid() {
		return roleid;
	}

	public void setType(final byte type) {
		this.type = type;
	}

	public byte getType() {
		return type;
	}
}
