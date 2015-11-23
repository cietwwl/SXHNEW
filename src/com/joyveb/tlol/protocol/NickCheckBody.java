package com.joyveb.tlol.protocol;

import java.nio.ByteBuffer;

public final class NickCheckBody extends MsgBody {
	public static final NickCheckBody INSTANCE = new NickCheckBody();

	private NickCheckBody() {
	}

	private String nick;

	@Override
	public boolean readBody(final ByteBuffer body) {
		if (body.remaining() <= 4 + 2)
			return false;

		bodyLen = body.getInt();

		short nickLen = body.getShort();

		if (nickLen <= 0 || body.remaining() < nickLen)
			return false;

		nick = getStrByLen(body, nickLen).trim();

		return true;
	}

	public void setNick(final String nick) {
		this.nick = nick;
	}

	public String getNick() {
		return nick;
	}

}
