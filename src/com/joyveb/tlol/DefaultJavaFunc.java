package com.joyveb.tlol;

import org.keplerproject.luajava.JavaFunction;
import org.keplerproject.luajava.LuaException;

import com.joyveb.tlol.util.Log;

public abstract class DefaultJavaFunc extends JavaFunction {

    private final String funcName;
    public DefaultJavaFunc(final String funcName) {
		super(null);
		this.funcName = funcName;
	}

	public void register() {
		try {
			this.L = LuaService.lua;
			register(funcName);
		}catch(LuaException e) {
			Log.error(Log.ERROR, "注册Java函数" + funcName + "失败！");
			e.printStackTrace();
		}
	}
}
