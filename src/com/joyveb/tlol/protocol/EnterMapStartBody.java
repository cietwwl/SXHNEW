package com.joyveb.tlol.protocol;

import java.nio.ByteBuffer;

import com.joyveb.tlol.map.Coords;

public final class EnterMapStartBody extends MsgBody {
	public static final EnterMapStartBody INSTANCE = new EnterMapStartBody();

	private EnterMapStartBody() {
	}

	private Coords coords = new Coords();

	@Override
	public boolean readBody(final ByteBuffer body) {
		if (body.remaining() < 4 + 2 + 4 + 4)
			return false;

		bodyLen = body.getInt();

		return coords.setMap(body.getShort())
				.setX(body.getInt())
				.setY(body.getInt()).nonNegative();
	}

	public void setCoords(final Coords coords) {
		this.coords = coords;
	}

	public Coords getCoords() {
		return coords;
	}

}
