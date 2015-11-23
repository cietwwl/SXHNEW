package com.joyveb.tlol.protocol;

import java.nio.ByteBuffer;

/** 玩家请求周围角色和NPC */
public final class RoleArroundBody extends MsgBody {
	public static final RoleArroundBody INSTANCE = new RoleArroundBody();

	private RoleArroundBody() {
	}

	private int mapx;
	private int mapy;
	@Override
	public boolean readBody(final ByteBuffer body) {
		if (body.remaining() < 4 + 4 + 4 + 2)
			return false;

		bodyLen = body.getInt();
		mapx = body.getInt();
		mapy = body.getInt();
		end = body.getShort();

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

}
