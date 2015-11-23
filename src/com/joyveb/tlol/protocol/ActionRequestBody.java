package com.joyveb.tlol.protocol;

import java.nio.ByteBuffer;

public class ActionRequestBody extends MsgBody {

	public static final ActionRequestBody INSTANCE = new ActionRequestBody();

	private ActionRequestBody() {
	}
	
	private String request = null;
	@Override
	public boolean readBody(ByteBuffer body) {
		bodyLen = body.getInt();
		request = getStrByLen(body, body.getShort());
		return true;
	}
	public String getRequest() {
		return request;
	}
	
}
