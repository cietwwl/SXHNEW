package com.joyveb.tlol.protocol;

import java.nio.ByteBuffer;

public final class MessageSendBody extends MsgBody {
	public static final MessageSendBody INSTANCE = new MessageSendBody();

	private MessageSendBody() {
	}

	private byte type;
	private int id;
	private String name;
	private String content;
	@Override
	public boolean readBody(final ByteBuffer body) {
		if (body.remaining() <= 4 + 1 + 4 + 2)
			return false;

		bodyLen = body.getInt();
		type = body.get();
		id = body.getInt();
		short nameLen = body.getShort();
		if (nameLen > 0)
			name = getStrByLen(body, nameLen);

		short contentLen = body.getShort();
		content = getStrByLen(body, contentLen);

		end = body.getShort();

		return true;
	}

	public void setType(final byte type) {
		this.type = type;
	}

	public byte getType() {
		return type;
	}

	public void setContent(final String content) {
		this.content = content;
	}

	public String getContent() {
		return content;
	}

	public void setId(final int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
