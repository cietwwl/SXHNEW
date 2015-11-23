package com.joyveb.tlol.javafunc;

import org.keplerproject.luajava.LuaException;

import com.joyveb.tlol.DefaultJavaFunc;
import com.joyveb.tlol.LuaService;
import com.joyveb.tlol.TLOLJavaFunction;

/**
 * 待注册的基本函数
 * @author Sid
 */
public enum LangJavaFunc implements TLOLJavaFunction {
	/**
	 * Long.toString()
	 * 
	 * @param 参数1：long
	 */
	Long2String(new DefaultJavaFunc("_Long2String") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(Long.toString((long) this.getParam(2).getNumber()));
			return 1;
		}
	}),
	
	/**
	 * String.getBytes("UTF-8")
	 * 
	 * @param 参数1：String
	 */
	GetUTF8(new DefaultJavaFunc("getUTF8") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(this.getParam(2).getString());
			return 1;
		}
	}),
	
	/**
	 * Enum.ordinal()
	 * 
	 * @param 参数1：Enum
	 */
	EnumOrdinal(new DefaultJavaFunc("_EnumOrdinal") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((Enum<?>) this.getParam(2).getObject()).ordinal());
			return 1;
		}
	}),
	
	/**
	 * Object.equals()
	 * 
	 * @param 参数1：Object
	 * @param 参数2：Object
	 */
	ObjectEquals(new DefaultJavaFunc("_ObjectEquals") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(this.getParam(2).getObject().equals(this.getParam(3).getObject()));
			return 1;
		}
	}),
	
	/**
	 * Object.toString()
	 */
	ObjectToString(new DefaultJavaFunc("_ObjectToString") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(this.getParam(2).getObject().toString());
			return 1;
		}
	}),
	
	/**
	 * Enum.name()
	 */
	EnumName(new DefaultJavaFunc("_EnumName") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((Enum<?>)this.getParam(2).getObject()).name());
			return 1;
		}
	});

	/**
	 * 实现默认的可注册Java函数
	 */
	private final DefaultJavaFunc jf;

	/**
	 * @param jf 可注册Java函数
	 */
	private LangJavaFunc(final DefaultJavaFunc jf) {
		this.jf = jf;
	}

	@Override
	public void register() {
		jf.register();
	}
	
}
