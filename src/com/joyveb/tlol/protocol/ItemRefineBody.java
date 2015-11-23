package com.joyveb.tlol.protocol;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class ItemRefineBody extends MsgBody {

	public static final ItemRefineBody INSTANCE = new ItemRefineBody();
	
	private ItemRefineBody() {}
	
	private ArrayList<Long> uids = new ArrayList<Long>();
	
	@Override
	public boolean readBody(ByteBuffer body) {
		uids.clear();
		
		if(body.remaining() < 4 + 1 + 8)
			return false;
		
		bodyLen = body.getInt();
		
		byte num = body.get();
			
		if(num <= 0 || body.remaining() < num * 8)
			return false;
		
		for(int i = 0; i < num; i++)
			uids.add(body.getLong());
		
		return true;
	}

	public ArrayList<Long> getUids() {
		return uids;
	}

	public void setUids(ArrayList<Long> uids) {
		this.uids = uids;
	}

}
