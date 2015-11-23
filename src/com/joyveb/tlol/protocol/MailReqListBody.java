package com.joyveb.tlol.protocol;

import java.nio.ByteBuffer;

public final class MailReqListBody extends MsgBody {
	public static final MailReqListBody INSTANCE = new MailReqListBody();

	private MailReqListBody() {
	}

	private byte mailIndex;
	private byte mailNum;

	@Override
	public boolean readBody(final ByteBuffer body) {
		bodyLen = body.getInt();
		mailIndex = body.get();
		mailNum = body.get();
		return true;
	}

	public byte getMailIndex() {
		return mailIndex;
	}

	public void setMailIndex(final byte mailIndex) {
		this.mailIndex = mailIndex;
	}

	public byte getMailNum() {
		return mailNum;
	}

	public void setMailNum(final byte mailNum) {
		this.mailNum = mailNum;
	}

}
