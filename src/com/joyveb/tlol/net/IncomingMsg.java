package com.joyveb.tlol.net;

import java.nio.ByteBuffer;

import com.joyveb.tlol.protocol.MsgHeader;

/** 接收到的消息 */
public class IncomingMsg {
	/** 只有在发现客户端断开时为了避免生成多个断开消息才使用 */
	// private TianLongAttachBean attach;
	private int id;
	private NetHandler netHandler;
	private MsgHeader header;
	private ByteBuffer body;

	public IncomingMsg(final NetHandler netHandler, final ByteBuffer header, final ByteBuffer body) {
		this.netHandler = netHandler;
		this.header = new MsgHeader(header);
		this.body = body;

		this.body.flip();
	}

	public final MsgHeader getHeader() {
		return header;
	}

	public final ByteBuffer getBody() {
		return body;
	}

	public final NetHandler getNetHandler() {
		return netHandler;
	}

	public final int getId() {
		return id;
	}

	public final void setId(final int userId) {
		this.id = userId;
	}
}
