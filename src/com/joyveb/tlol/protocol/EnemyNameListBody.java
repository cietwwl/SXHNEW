package com.joyveb.tlol.protocol;

import java.nio.ByteBuffer;

public final class EnemyNameListBody extends MsgBody {
	public static final EnemyNameListBody INSTANCE = new EnemyNameListBody();

	private EnemyNameListBody() {
	}
	private byte enemyIndex;
	private byte enemyNum;
	@Override
	public boolean readBody(final ByteBuffer body) {
		if (body.remaining() < 4 + 1 + 1) {
			return false;
		}
		bodyLen = body.getInt();
		enemyIndex = body.get();
		enemyNum = body.get();

		return true;
	}


	public byte getEnemyIndex() {
		return enemyIndex;
	}

	public void setEnemyIndex(byte enemyIndex) {
		this.enemyIndex = enemyIndex;
	}

	public byte getEnemyNum() {
		return enemyNum;
	}

	public void setEnemyNum(byte enemyNum) {
		this.enemyNum = enemyNum;
	}

}
