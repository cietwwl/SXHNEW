package com.joyveb.tlol.map;

public class Coords {
	/** 地图id */
	private short map;
	/** x坐标 */
	private int x;
	/** y坐标 */
	private int y;

	public Coords() {
	};

	public Coords(final short map, final int x, final int y) {
		this.map = map;
		this.x = x;
		this.y = y;
	}

	public final boolean nonNegative() {
		return map >= 0 && x >= 0 && y >= 0;
	}
	@Override
	public final String toString() {
		return " 地图：" + map + " X坐标：" + x + " Y坐标：" + y;
	}

	public final Coords setMap(final short map) {
		this.map = map;
		return this;
	}

	public final short getMap() {
		return map;
	}

	public final Coords setX(final int x) {
		this.x = x;
		return this;
	}

	public final int getX() {
		return x;
	}

	public final Coords setY(final int y) {
		this.y = y;
		return this;
	}

	public final int getY() {
		return y;
	}

}
