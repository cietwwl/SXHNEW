package com.joyveb.tlol.util;

public final class UID {
	
	private UID() {
		
	}
	
	private static long lastUID = System.currentTimeMillis() * 100;

	public static long next() { 
		long curID = System.currentTimeMillis() * 100;
		if (curID > lastUID)
			lastUID = curID;
		else
			lastUID = lastUID + 100;

		return lastUID;
	}

}
