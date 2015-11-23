package com.joyveb.tlol.protocol;

import java.nio.ByteBuffer;

public final class RoleMoveBody extends MsgBody {
	public static final RoleMoveBody INSTANCE = new RoleMoveBody();

	private RoleMoveBody() {
	}

	private int mapX;
	private int mapY;
	@Override
	public boolean readBody(final ByteBuffer body) {
		body.position(0);
		if (body.remaining() < 4 + 4 + 4 + 2)
			return false;

		bodyLen = body.getInt();

		mapX = body.getInt();
		mapY = body.getInt();

		end = body.getShort();

		return true;
	}

	public void setMapY(final int mapY) {
		this.mapY = mapY;
	}

	public int getMapY() {
		return mapY;
	}

	public void setMapX(final int mapX) {
		this.mapX = mapX;
	}

	public int getMapX() {
		return mapX;
	}

}
