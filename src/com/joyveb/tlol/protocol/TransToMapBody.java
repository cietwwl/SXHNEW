package com.joyveb.tlol.protocol;

import java.nio.ByteBuffer;

public class TransToMapBody extends MsgBody {
	public static final TransToMapBody INSTANCE = new TransToMapBody();

	private TransToMapBody() {
	}
	
	private short mapId;
	@Override
	public boolean readBody(ByteBuffer body) {
		if (body.remaining() < 4 + 2 + 2)
			return false;
		bodyLen = body.getInt();

		mapId = body.getShort();

		end = body.getShort();
		return true;
	}
	
	public short getMapId() {
		return mapId;
	}
	public void setMapId(short mapId) {
		this.mapId = mapId;
	}
	
	

}
