package com.joyveb.tlol.protocol;

import java.nio.ByteBuffer;

public final class FindPathBody extends MsgBody {
	public static final FindPathBody INSTANCE = new FindPathBody();

	private FindPathBody() {
	}

	private short destmap;
	private int destmapx;
	private int destmapy;

	@Override
	public boolean readBody(final ByteBuffer body) {
		if (body.remaining() < 4 + 2 + 4 + 4 + 2)
			return false;

		bodyLen = body.getInt();
		destmap = body.getShort();
		destmapx = body.getInt();
		destmapy = body.getInt();

		end = body.getShort();

		return true;
	}

	public short getDestmap() {
		return destmap;
	}

	public void setDestmap(final short destmap) {
		this.destmap = destmap;
	}

	public int getDestmapx() {
		return destmapx;
	}

	public void setDestmapx(final int destmapx) {
		this.destmapx = destmapx;
	}

	public int getDestmapy() {
		return destmapy;
	}

	public void setDestmapy(final int destmapy) {
		this.destmapy = destmapy;
	}

}
