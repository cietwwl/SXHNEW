package com.joyveb.tlol.javafunc;

import org.keplerproject.luajava.LuaException;

import com.joyveb.tlol.DefaultJavaFunc;
import com.joyveb.tlol.MessageSend;
import com.joyveb.tlol.TLOLJavaFunction;

/**
 * 待注册的向客户端发送消息函数
 * @author Sid
 */
public enum MessageSendJavaFunc implements TLOLJavaFunction {
	/**
	 * 初始化消息体
	 */
	PrepareBody(new DefaultJavaFunc("prepareBody") {
		@Override
		public int execute() throws LuaException {
			MessageSend.prepareBody();
			return 0;
		}
	}),
	
	/**
	 * MessageSend.putByte()
	 * 
	 * @param 参数1：byte
	 */
	PutByte(new DefaultJavaFunc("putByte") {
		@Override
		public int execute() throws LuaException {
			MessageSend.putByte((byte) this.getParam(2).getNumber());
			return 0;
		}
	}),
	
	/**
	 * MessageSend.putShort()
	 * 
	 * @param 参数1：short
	 */
	PutShort(new DefaultJavaFunc("putShort") {
		@Override
		public int execute() throws LuaException {
			MessageSend.putShort((short) this.getParam(2).getNumber());
			return 0;
		}
	}),
	
	/**
	 * MessageSend.putInt()
	 * 
	 * @param 参数1：int
	 */
	PutInt(new DefaultJavaFunc("putInt") {
		@Override
		public int execute() throws LuaException {
			MessageSend.putInt((int) this.getParam(2).getNumber());
			return 0;
		}
	}),
	
	/**
	 * MessageSend.putLong()
	 * 
	 * @param 参数1：long
	 */
	PutLong(new DefaultJavaFunc("putLong") {
		@Override
		public int execute() throws LuaException {
			MessageSend.putLong((long) this.getParam(2).getNumber());
			return 0;
		}
	}),
	
	/**
	 * MessageSend.putString()
	 * 
	 * @param 参数1：String
	 */
	PutString(new DefaultJavaFunc("putString") {
		@Override
		public int execute() throws LuaException {
			MessageSend.putString(this.getParam(2).getString());
			return 0;
		}
	}),
	
	
	/**
	 * MessageSend.bodyMark()
	 */
	BodyMark(new DefaultJavaFunc("bodyMark") {
		@Override
		public int execute() throws LuaException {
			MessageSend.bodyMark();
			return 0;
		}
	}),
	
	/**
	 * MessageSend.bodyPosition()
	 */
	GetPosition(new DefaultJavaFunc("getPosition") {
		@Override
		public int execute() throws LuaException {
			this.L.pushNumber(MessageSend.bodyPosition());
			return 1;
		}
	}),
	
	/**
	 * MessageSend.bodyPosition()
	 * 
	 * @param 参数1：int position
	 */
	SetPosition(new DefaultJavaFunc("setPosition") {
		@Override
		public int execute() throws LuaException {
			MessageSend.bodyPosition((int) this.getParam(2).getNumber());
			return 0;
		}
	}),
	
	/**
	 * MessageSend.bodyReset()
	 */
	BodyReset(new DefaultJavaFunc("bodyReset") {
		@Override
		public int execute() throws LuaException {
			MessageSend.bodyReset();
			return 0;
		}
	});
	
	/**
	 * 实现默认的可注册Java函数
	 */
	private final DefaultJavaFunc jf;

	/**
	 * @param jf 可注册Java函数
	 */
	private MessageSendJavaFunc(final DefaultJavaFunc jf) {
		this.jf = jf;
	}

	@Override
	public void register() {
		jf.register();
	}
}
