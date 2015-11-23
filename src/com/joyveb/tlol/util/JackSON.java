package com.joyveb.tlol.util;

import org.codehaus.jackson.map.ObjectMapper;

public final class JackSON {
	public static ObjectMapper INSTANCE = new ObjectMapper();
	private JackSON() {
		
	}
}
