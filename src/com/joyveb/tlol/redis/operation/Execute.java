package com.joyveb.tlol.redis.operation;

import redis.clients.jedis.Jedis;

public interface Execute<T> {
	T execute(Jedis jedis);
}