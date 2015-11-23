package com.joyveb.tlol.protocol;

import java.nio.ByteBuffer;

public final class NameListAddBody extends MsgBody {
	public static final NameListAddBody INSTANCE = new NameListAddBody();

	private NameListAddBody() {
	}

	private int id;
	private String nick;
	private boolean friend;
	@Override
	public boolean readBody(final ByteBuffer body) {
		if (body.remaining() <= 4 + 4 + 2)
			return false;

		bodyLen = body.getInt();
		id = body.getInt();
		if (id < 0)
			return false;

		short nickLen = body.getShort();
		if (id == 0 && nickLen <= 0)
			return false;

		nick = getStrByLen(body, nickLen).trim();
		friend = body.get() == 0;
		return true;
	}

	public void setId(final int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setNick(final String nick) {
		this.nick = nick;
	}

	public String getNick() {
		return nick;
	}

	public void setFriend(final boolean friend) {
		this.friend = friend;
	}

	public boolean isFriend() {
		return friend;
	}
}
