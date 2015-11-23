package com.joyveb.tlol;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.joyveb.tlol.net.HoldNetHandler;
import com.joyveb.tlol.protocol.MsgHeader;
import com.joyveb.tlol.protocol.MsgID;
import com.joyveb.tlol.util.Log;

public abstract class MessageSend {
	/** 发送消息头 */
	public  static ByteBuffer header = ByteBuffer.allocateDirect(MsgHeader.SIZE).order(ByteOrder.LITTLE_ENDIAN);
	/** 发送消息体 */
	public static ByteBuffer body = ByteBuffer.allocateDirect(10240).order(ByteOrder.LITTLE_ENDIAN);

	public static byte[] getUTF8(String str) {
		byte[] bytes = null;
		try {
			if(str == null)
				str = " ";
			bytes = str.getBytes("UTF-8");
		}catch(Exception e) {
			Log.error(Log.STDOUT, "getUTF8", e);
		}
		return bytes;
	}

	public static void prepareBody() {
		body.clear();
		body.putInt(0);
	}

	public static void putByte(byte b) {
		body.put(b);
	}

	public static void putShort(short s) {
		body.putShort(s);
	}

	public static void putInt(int i) {
		body.putInt(i);
	}

	public static void putLong(long l) {
		body.putLong(l);
	}

	public static void putString(String str) {
		if(str == null || str.length() == 0)
			body.putShort((short) 0);
		else {
			byte[] temp = getUTF8(str);
			body.putShort((short) temp.length);
			body.put(temp);
		}
	}

	/** 失败返回 */
	public static void replyMessage(HoldNetHandler holder, int result, MsgID msgid, String info) {
		prepareBody();
		putShort((short) result);

		if(info == null || info.length() == 0)
			putShort((short) 0);
		else
			putString(info);

		putShort((short) 0);

		sendMsg(holder, msgid);
	}

	/** 普通回复 */
	public static void sendMsg(HoldNetHandler holder, MsgID msgid, String info) {
		if(info == null) {
			sendMsg(holder, msgid);
			return;
		}

		prepareBody();
		putShort((short) 0);
		putString(info);
		putShort((short) 0);

		sendMsg(holder, msgid);
	}

	/** 向客户端发送消息 */
	public static void sendMsg(HoldNetHandler holder, MsgID msgid) {
		if(holder == null || holder.getNetHandler() == null)
			return;
		
		body.limit(body.position()); //将body的长度定为position
		body.position(0);            //将指针指向开始处
		body.putInt(body.limit() - 4); //将除前四个标示后面内容长度的字节外的内容长度放进body开头
		body.position(0);              //将指针指向开始处

		header.clear();                 //清空header
		header.putInt(14 + body.limit()); //将header的长度限制为14个字节加上body的长度
		header.putShort((short) 1000);    //放入protocolID  写死 值为1000
		header.putShort((short) 1);         // msgType 写死为 1
		header.putShort(msgid.getMsgid());  // 将协议号放入header中
		header.putInt(0);                   //msgSeq  固定值0
		header.putInt(msgid.getMsgid() + body.limit() - 7); //将msgCheck放入header里

		ByteBuffer sendBuf = ByteBuffer.allocateDirect(body.limit() + header.limit()).order(ByteOrder.LITTLE_ENDIAN);
		header.flip();
		sendBuf.put(header); //先放header
		sendBuf.put(body);   //再放 body

		holder.getNetHandler().send(sendBuf);  //将消息发出
	}

	public static int bodyPosition() {
		return body.position();
	}

	public static void bodyPosition(int position) {
		body.position(position);
	}

	public static void bodyMark() {
		body.mark();
	}

	public static void bodyReset() {
		body.reset();
	}

	public static void placeholder(int... args) {
		body.mark();

		for(int type : args) {
			if(type == 1)
				body.put((byte) 0);
			else if(type == 2)
				body.putShort((short) 0);
			else if(type == 4)
				body.putInt(0);
			else if(type == 8)
				body.putLong(0);
			else
				Log.error(Log.ERROR, "占位符类型不能为：" + type);
		}
	}

	public static void fillPlaceholder(Object... args) {
		int position = body.position();

		body.reset();

		for(int i = 1; i <= args.length / 2; i++) {
			int type = (Integer) args[i * 2 - 2];
			Number value = (Number) args[i * 2 - 1];
			if(type == 1)
				body.put(((Number) value).byteValue());
			else if(type == 2)
				body.putShort(((Number) value).shortValue());
			else if(type == 4)
				body.putInt(((Number) value).intValue());
			else if(type == 8)
				body.putLong(((Number) value).longValue());
			else
				Log.error(Log.ERROR, "占位符类型不能为：" + type);
		}

		body.position(position);
	}

}
