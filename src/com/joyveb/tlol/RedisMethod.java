package com.joyveb.tlol;

import redis.clients.jedis.Jedis;

import com.joyveb.tlol.redis.Redis;
import com.joyveb.tlol.redis.operation.Execute;

public class RedisMethod {
	/**
	 * 获取boss等级的KEY
	 * 
	 * @return 在线等级的KEY
	 */
	public final String getBossLevelKey() {
		return "BOSSLEVEL" + Conf.instance().getSrvId();
	}

	/**
	 * BOSS等级加1
	 */
	public void incrBossLevel() {

		Redis.instance().execute(new Execute<Void>() {

			@Override
			public Void execute(Jedis jedis) {
				jedis.incr(getBossLevelKey());
				return null;
			}

		});

	}

	/**
	 * 取得BOSS等级
	 */
	public int getBossLevel() {

		return Redis.instance().execute(new Execute<Integer>() {

			@Override
			public Integer execute(Jedis jedis) {
				String num = jedis.get(getBossLevelKey());
				System.out.println("在库里查询的redis等级" + num);

				if (num != null) {
					try {
						int nummm = Integer.valueOf(num);
						if (nummm <= 0) {
							jedis.set(getBossLevelKey(), "1");
							return 1;
						}
						return nummm;
					} catch (NumberFormatException e) {
						e.printStackTrace();
						jedis.set(getBossLevelKey(), "1");
						return 1;
					}
				}
				return 1;

			}

		});

	}

	/**
	 * set BOSS等级
	 */
	public void setBossLevel(final String s) {

		Redis.instance().execute(new Execute<Void>() {

			@Override
			public Void execute(Jedis jedis) {
				jedis.set(getBossLevelKey(), s);
				return null;
			}

		});

	}

	/**
	 * BOSS等级减1
	 */
	public void decrBossLevel() {

		Redis.instance().execute(new Execute<Void>() {

			@Override
			public Void execute(Jedis jedis) {
				System.out.println("调用了减");
				jedis.decr(getBossLevelKey());
				return null;
			}

		});

	}

	private RedisMethod() {

	}

	private static RedisMethod instance = new RedisMethod();

	public static RedisMethod instance() {
		return instance;
	}

	public void deleteKey(final String key) {
		Redis.instance().execute(new Execute<Void>() {

			@Override
			public Void execute(Jedis jedis) {
				jedis.del(key);
				return null;
			}

		});
	}

	public boolean isKeyExists(final String key) {
		return Redis.instance().execute(new Execute<Boolean>() {

			@Override
			public Boolean execute(Jedis jedis) {
				if (jedis.exists(key)) {
					return true;
				}
				return false;
			}

		});
	}

	public String getValue(final String key) {
		return Redis.instance().execute(new Execute<String>() {

			@Override
			public String execute(Jedis jedis) {
				return jedis.get(key);
			}

		});
	}

	public void addValue(final String key, final String value) {
		Redis.instance().execute(new Execute<Void>() {

			@Override
			public Void execute(Jedis jedis) {
				jedis.set(key, value);
				return null;
			}

		});
	}
}
