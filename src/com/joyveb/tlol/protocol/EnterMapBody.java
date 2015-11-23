package com.joyveb.tlol.protocol;

import java.nio.ByteBuffer;

public final class EnterMapBody extends MsgBody {
	public static final EnterMapBody INSTANCE = new EnterMapBody();

	private EnterMapBody() {
	}

	private int mapx;
	private int mapy;
	private int transid;
	@Override
	public boolean readBody(final ByteBuffer body) {
		body.position(0);
		if (body.remaining() < 4 * 4)
			return false;

		bodyLen = body.getInt();
		mapx = body.getInt();
		mapy = body.getInt();
		transid = body.getInt();

		return true;
	}

	public void setMapx(final int mapx) {
		this.mapx = mapx;
	}

	public int getMapx() {
		return mapx;
	}

	public void setMapy(final int mapy) {
		this.mapy = mapy;
	}

	public int getMapy() {
		return mapy;
	}

	public void setTransid(final int transid) {
		this.transid = transid;
	}

	public int getTransid() {
		return transid;
	}
}