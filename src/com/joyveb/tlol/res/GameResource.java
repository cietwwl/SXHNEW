package com.joyveb.tlol.res;

public class GameResource {
	private String resName = "";
	private byte[] gameRes = null;
	private int version = 0;

	public GameResource(final String resName, final byte[] gameRes, final int version) {
		this.resName = resName;
		this.gameRes = gameRes;
		this.version = version;
	}

	public final String getResName() {
		return resName;
	}

	public final byte[] getGameRes() {
		return gameRes;
	}

	public final int getVersion() {
		return version;
	}

}
