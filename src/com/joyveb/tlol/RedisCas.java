package com.joyveb.tlol;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import com.joyveb.tlol.redis.operation.Cas;

public class RedisCas extends Cas<Boolean> {

	private short zoneId;
	private String key;

	@Override
	public boolean onCheck(Jedis jedis) {
		if (jedis.exists(key)) {
			String value = jedis.get(key);

			if (value != null && value.equals("none")) {
				return true;
			}
		}

		return false;
	}

	@Override
	public void onSet(Transaction trans) {
		trans.set(key, String.valueOf(zoneId));
	}

	@Override
	protected Boolean onExexcuted() {
		return true;
	}

	@Override
	protected Boolean onFailed() {
		return false;
	}

	/**
	 * @param zoneId
	 *            the zoneId to set
	 */
	public void setZoneId(short zoneId) {
		this.zoneId = zoneId;
	}

	/**
	 * @param key
	 *            the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}

}
