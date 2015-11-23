package com.joyveb.tlol.protocol;

import org.keplerproject.luajava.LuaException;

import com.joyveb.tlol.DefaultJavaFunc;
import com.joyveb.tlol.LuaService;
import com.joyveb.tlol.TLOLJavaFunction;

/**
 * 协议相关函数注册
 * @author Sid
 */
public enum ProtocolJavaFunc implements TLOLJavaFunction {
	/**
	 * EnterMapBody.INSTANCE.getMapx(), EnterMapBody.INSTANCE.getMapy(), EnterMapBody.INSTANCE.getTransid()
	 */
	EnterMapGetAttr(new DefaultJavaFunc("_EnterMapGetAttr") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(EnterMapBody.INSTANCE.getMapx());
			LuaService.push(EnterMapBody.INSTANCE.getMapy());
			LuaService.push(EnterMapBody.INSTANCE.getTransid());
			return 3;
		}
	}),
	
	/**
	 * FindPathBody.INSTANCE.getDestmap(), FindPathBody.INSTANCE.getDestmapx(), FindPathBody.INSTANCE.getDestmapy()
	 */
	FindPathGetAttr(new DefaultJavaFunc("_FindPathGetAttr") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(FindPathBody.INSTANCE.getDestmap());
			LuaService.push(FindPathBody.INSTANCE.getDestmapx());
			LuaService.push(FindPathBody.INSTANCE.getDestmapy());
			return 3;
		}
	});

	/**
	 * 实现默认的可注册Java函数
	 */
	private final DefaultJavaFunc jf;

	/**
	 * @param jf 可注册Java函数
	 */
	private ProtocolJavaFunc(final DefaultJavaFunc jf) {
		this.jf = jf;
	}

	@Override
	public void register() {
		jf.register();
	}
	
}
