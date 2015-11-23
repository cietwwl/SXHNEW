package com.joyveb.tlol.protocol;

import java.nio.ByteBuffer;

/** 消息头 */
public class MsgHeader {
	public static final int SIZE = 18;

	private int msgLength;
	private short protocolID;
	private short msgType;
	private short msgID;
	private int msgSeq;
	private int msgCheck;

	public MsgHeader(final ByteBuffer header) {
		header.flip();
		msgLength = header.getInt();
		protocolID = header.getShort();
		msgType = header.getShort();
		msgID = header.getShort();
		msgSeq = header.getInt();
		msgCheck = header.getInt();

		header.position(0);
	}

	/** 
	 * 将消息头写入缓冲区 
	 * @param header 
	 */
	public final void writeHeader(final ByteBuffer header) {
		header.putShort(protocolID);
		header.putShort(msgType);
		header.putShort(msgID);
		header.putInt(msgSeq);
		header.putInt(msgCheck);
	}
	@Override
	public final String toString() {
		return "msgLen[" + msgLength + "] protocolID [" + protocolID
				+ "] msgType[" + msgType + "] msgID[" + msgID + "] msgSeq["
				+ msgSeq + "] msgCheck[" + msgCheck + "]";
	}

	public final void setMsgLength(final int msgLength) {
		this.msgLength = msgLength;
	}

	public final int getMsgLength() {
		return msgLength;
	}

	public final void setProtocolID(final short protocolID) {
		this.protocolID = protocolID;
	}

	public final short getProtocolID() {
		return protocolID;
	}

	public final void setMsgType(final short msgType) {
		this.msgType = msgType;
	}

	public final short getMsgType() {
		return msgType;
	}

	public final void setMsgID(final short msgID) {
		this.msgID = msgID;
	}

	public final short getMsgID() {
		return msgID;
	}

	public final void setMsgSeq(final int msgSeq) {
		this.msgSeq = msgSeq;
	}

	public final int getMsgSeq() {
		return msgSeq;
	}

	public final void setMsgCheck(final int msgCheck) {
		this.msgCheck = msgCheck;
	}

	public final int getMsgCheck() {
		return msgCheck;
	}
}
