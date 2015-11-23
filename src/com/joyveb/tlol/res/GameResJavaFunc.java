package com.joyveb.tlol.res;

import org.keplerproject.luajava.LuaException;

import com.joyveb.tlol.DefaultJavaFunc;
import com.joyveb.tlol.LuaService;
import com.joyveb.tlol.TLOLJavaFunction;

/**
 * GameResource相关函数注册
 * @author Sid
 */
public enum GameResJavaFunc implements TLOLJavaFunction {
	/**
	 * ResourceManager.getInstance().getResVer(String)
	 * 
	 * @param 参数1：String
	 */
	ResourceManagerGetResVer(new DefaultJavaFunc("_ResourceManagerGetResVer") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(ResourceManager.getResVer(this.getParam(2).getString()));
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
	private GameResJavaFunc(final DefaultJavaFunc jf) {
		this.jf = jf;
	}

	@Override
	public void register() {
		jf.register();
	}
	
}
