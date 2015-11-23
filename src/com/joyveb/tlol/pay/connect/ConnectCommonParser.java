package com.joyveb.tlol.pay.connect;


public class ConnectCommonParser extends ConnectParser {

	private static ConnectParser instance = null;
	
	public static final ConnectParser getInstance() {
		if (instance == null) {
			instance = new ConnectCommonParser();
		}
		return instance;
	}
	
	private ConnectCommonParser() {
		
	}
	
}
