package com.joyveb.tlol.mail;

import com.joyveb.tlol.item.Item;
import com.joyveb.tlol.util.UID;

public class Mail {
	public static final int VERSION = 1;
	
	public static final byte LV_NORMAL = 0;
	public static final byte LV_RETURN = 1;
	public static final byte LV_SYS = 2;
	public static final byte STATE_NEW = 0;
	public static final byte STATE_FULL = 1;
	private long mailId;
	private boolean payMail;
	private boolean read;
	private int senderId;
	private String senderName;
	private int receiverId;
	private String receiverName;
	private String subject;
	private String content;
	private Item attachment;
	private long sendTime;
	private int gold;
	private int payGold;
	private byte level;

	
	/**
	 * 新建邮件
	 * 
	 * @param sender 发送者ID
	 * @param receiver 接受者昵称
	 * @param senderName 发送者姓名
	 * @param payMail 是否为到付邮件
	 * @param subject 邮件主题
	 * @param content 邮件内容
	 * @param attchment 邮件附件
	 * @param gold 寄送金币数量
	 * @param payGold 到付邮件需支付的金额
	 * @param level 等级
	 */
	public Mail(final int sender, final int receiver, final String senderName, final boolean payMail,
			final String subject, final String content, final Item attchment, final int gold,
			final int payGold, final byte level) {
		this.mailId = UID.next();
		this.senderId = sender;
		this.receiverId = receiver;
		this.senderName = senderName;
		this.payMail = payMail;
		this.subject = subject;
		this.content = content.trim();
		this.attachment = attchment;
		this.gold = gold;
		this.payGold = payGold;
		this.sendTime = System.currentTimeMillis();
		this.level = level;
	}

	public Mail() {

	}

	public final long getMailId() {
		return mailId;
	}

	public final void setMailId(final long mailId) {
		this.mailId = mailId;
	}

	public final String getSenderName() {
		return senderName;
	}

	public final void setSenderName(final String senderName) {
		this.senderName = senderName;
	}

	public final String getReceiverName() {
		return receiverName;
	}

	public final void setReceiverName(final String receiverName) {
		this.receiverName = receiverName;
	}

	public final boolean isPayMail() {
		return payMail;
	}

	public final void setPayMail(final boolean payMail) {
		this.payMail = payMail;
	}

	public final boolean isRead() {
		return read;
	}

	public final void setRead(final boolean read) {
		this.read = read;
	}

	public final long getSendTime() {
		return sendTime;
	}

	public final void setSendTime(final long sendTime) {
		this.sendTime = sendTime;
	}

	public final int getPayGold() {
		return payGold;
	}

	public final void setPayGold(final int payGold) {
		this.payGold = payGold;
	}

	public final int getSenderId() {
		return senderId;
	}

	public final void setSenderId(final int senderId) {
		this.senderId = senderId;
	}

	public final int getReceiverId() {
		return receiverId;
	}

	public final void setReceiverId(final int receiver) {
		this.receiverId = receiver;
	}

	public final String getSubject() {
		return subject;
	}

	public final void setSubject(final String subject) {
		this.subject = subject;
	}

	public final String getContent() {
		return content;
	}

	public final void setContent(final String content) {
		this.content = content;
	}

	public final Item getAttachment() {
		return attachment;
	}

	public final void setAttachment(final Item attachment) {
		this.attachment = attachment;
	}

	public final int getGold() {
		return gold;
	}

	public final void setGold(final int gold) {
		this.gold = gold;
	}

	public final byte getLevel() {
		return level;
	}

	public final void setLevel(final byte level) {
		this.level = level;
	}
	@Override
	public final String toString() {
		String tmpStr = "[邮件主题" + this.subject + "][邮件内容: " + this.content
				+ "]";
		if (attachment != null)
			tmpStr += "[附件: " + attachment + attachment.getStorage() + "]";
		if (gold > 0)
			tmpStr += "[金币: " + gold + "]";

		return tmpStr;
	}

}
