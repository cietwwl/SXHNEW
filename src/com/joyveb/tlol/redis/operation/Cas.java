package com.joyveb.tlol.redis.operation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import com.joyveb.tlol.redis.Redis;

public abstract class Cas<Result> {

	private Logger logger = LoggerFactory.getLogger(Cas.class);

	protected void onPreCheck(Jedis jedis) {

	}

	public abstract boolean onCheck(Jedis jedis);

	public abstract void onSet(Transaction trans);

	public Result execute(String... watchedKeys) {
		return execute(100, watchedKeys);
	}

	protected abstract Result onExexcuted();

	protected abstract Result onFailed();

	public Result execute(int maxTryTime, String... watchedKeys) {
		Jedis jedis = null;
		Result result = null;
		try {
			jedis = Redis.instance().getResource();
			for (int i = 0; i < maxTryTime; i++) {

				jedis.watch(watchedKeys);
				onPreCheck(jedis);

				if (onCheck(jedis)) {
					Object ret = null;
					Transaction trans = jedis.multi();
					onSet(trans);
					ret = trans.exec();
					if (ret == null)
						continue;

					result = onExexcuted();
				} else {
					jedis.unwatch();
					result = onFailed();
					break;
				}

				if (i == maxTryTime - 1) {
					logger.error("reached max try time:" + maxTryTime);
				}
				break;
			}

			Redis.instance().returnResource(jedis);
			return result;
		} catch (Exception e) {
			if (jedis != null) {
				Redis.instance().returnBrokenResource(jedis);
			}
			logger.error("error on cas: ", e);
		}

		return null;
	}

}