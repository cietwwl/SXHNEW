package com.joyveb.tlol.protocol;

import java.nio.ByteBuffer;

public final class FeeListBody extends MsgBody {
	public static final FeeListBody INSTANCE = new FeeListBody();
	
	private FeeListBody() {
	}
	private int len;
	private int ver;
	@Override
	public boolean readBody(final ByteBuffer body) {
		len = body.getInt();
		ver = body.getShort();
		if(len>4+2){
			return false;
		}
		return true;
	}
	public int getLen() {
		return len;
	}
	public int getVer() {
		return ver;
	}
	
}
