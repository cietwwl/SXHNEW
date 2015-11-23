package com.joyveb.tlol.protocol;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

import com.joyveb.tlol.util.Log;

/** 收到的消息体的抽象类 */
public abstract class MsgBody {
	int bodyLen;
	short end;

	/**
	 *  从缓冲区中读取消息体并返回是否读取成功 
	 *  @param body 
	 *  @return 从缓冲区中读取消息体并返回是否读取成功 
	 */
	public abstract boolean readBody(ByteBuffer body);

	/**
	 *  从缓冲区中读取指定长度的字节并将其转换为字符串
	 *  @param body 
	 *  @param len 
	 *  @return  从缓冲区中读取指定长度的字节并将其转换为字符串
	 */
	public final String getStrByLen(final ByteBuffer body, final short len) {
		byte[] temp = new byte[len];
		body.get(temp);
		try {
			return new String(temp, "utf-8");
		} catch (UnsupportedEncodingException e) {
			Log.error(Log.STDOUT, "getStrByLen", e);
			return "";
		}
	}

}
