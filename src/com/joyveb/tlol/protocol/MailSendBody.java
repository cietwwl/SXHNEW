package com.joyveb.tlol.protocol;

import java.nio.ByteBuffer;

public final class MailSendBody extends MsgBody {
	public static final MailSendBody INSTANCE = new MailSendBody();

	private MailSendBody() {
	}

	private boolean isPayMail;
	private int receiverId;
	private String receiverName;
	private String subject;
	private String content;
	private long attachmentId;
	private short attachmentNum;
	private int gold;
	private int payG1old;
	
	public int getPayG1old() {
		return payG1old;
	}

	public void setPayG1old(final int payG1old) {
		this.payG1old = payG1old;
	}

	@Override
	public boolean readBody(final ByteBuffer body) {
		bodyLen = body.getInt();
		isPayMail = body.get() == 0 ? false : true;
		receiverId = body.getInt();
		receiverName = getStrByLen(body, body.getShort());
		subject = getStrByLen(body, body.getShort());
		content = getStrByLen(body, body.getShort());
		attachmentId = body.getLong();
		attachmentNum = body.getShort();
		gold = body.getInt();
		this.setPayGold(body.getInt());
		return true;
	}

	public boolean isPayMail() {
		return isPayMail;
	}

	public void setPayMail(final boolean isPayMail) {
		this.isPayMail = isPayMail;
	}

	public String getReceiverName() {
		return receiverName;
	}

	public void setReceiverName(final String receiverName) {
		this.receiverName = receiverName;
	}

	public void setReceiverId(final int receiverId) {
		this.receiverId = receiverId;
	}

	public int getReceiverId() {
		return receiverId;
	}

	public void setReceverId(final int receiverId) {
		this.receiverId = receiverId;
	}

	public String getSubject() {
		if (subject == null) {
			subject = " ";
		}
		return subject;
	}

	public void setSubject(final String subject) {
		this.subject = subject;
	}

	public String getContent() {
		if (content == null) {
			content = " ";
		}
		return content;
	}

	public void setContent(final String content) {
		this.content = content;
	}

	public long getAttachmentId() {
		return attachmentId;
	}

	public void setAttachmentId(final long attachmentId) {
		this.attachmentId = attachmentId;
	}

	public short getAttachmentNum() {
		return attachmentNum;
	}

	public void setAttachmentNum(final short attachmentNum) {
		this.attachmentNum = attachmentNum;
	}

	public int getGold() {
		return gold;
	}

	public void setGold(final int gold) {
		this.gold = gold;
	}

	public int getPayGold() {
		return this.payG1old;
	}

	public void setPayGold(final int payGold) {
		this.payG1old = payGold;
	}

}
