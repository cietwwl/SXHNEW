package com.joyveb.tlol.net;

import java.nio.ByteBuffer;

public interface NetHandler {
	byte STATE_NORMAL = 0; // 正常运行中
	byte STATE_CLOSED_BY_REMOTE = 1; // 远程主机关闭了连接
	byte STATE_CLOSED_PKG_MODIFIED = 2; // 包被修改
	byte STATE_CLOSED_R_POOL_OVERFLOW = 3; // 接收池溢出
	byte STATE_CLOSED_S_POOL_OVERFLOW = 4; // 收溢出
	byte STATE_CLOSED_PKG_LEN_ILLEGAL = 5; // 包长度非法
	byte STATE_CLOSED_UNKNOW = 6; // 未知
	byte STATE_CLOSED_BY_APP_LEVEL = 7; // 应用层关闭
	byte STATE_CLOSED_SYS_SHUTDOWN = 8; // 系统关闭
	byte STATE_CLOSED_TIME_OUT = 9; // 超时

	/**
	 * 接收数据
	 * @param userId 
	 * @return if there is any data, return a byte array,else return null
	 */
	IncomingMsg receive(int userId);

	/**
	 * 发送数据
	 * 
	 * @param buf 
	 */
	void send(ByteBuffer buf);

	/**
	 * 关闭连接
	 * @param state 
	 */
	void close(byte state);

	/**
	 * 连接是否已关闭
	 * @return 连接是否已关闭
	 */
	byte getState();

	void register();
}
