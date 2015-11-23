package com.joyveb.tlol.protocol;

import java.nio.ByteBuffer;

/** 客户端请求获取地图数据消息体 */
public final class RequestMapBody extends MsgBody {
	public static final RequestMapBody INSTANCE = new RequestMapBody();

	private RequestMapBody() {
	}

	/** 请求的区域地图号 */
	private short area;
	@Override
	public boolean readBody(final ByteBuffer body) {
		if (body.remaining() < 4 + 2 + 2)
			return false;

		bodyLen = body.getInt();

		area = body.getShort();
		end = body.getShort();

		return true;
	}

	public void setArea(final short area) {
		this.area = area;
	}

	public short getArea() {
		return area;
	}
}
