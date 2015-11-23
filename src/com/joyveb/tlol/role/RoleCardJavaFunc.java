package com.joyveb.tlol.role;

import org.keplerproject.luajava.LuaException;

import com.joyveb.tlol.DefaultJavaFunc;
import com.joyveb.tlol.LuaService;
import com.joyveb.tlol.TLOLJavaFunction;

/**
 * 角色名片相关函数注册
 * @author Sid
 */
public enum RoleCardJavaFunc implements TLOLJavaFunction {
	/**
	 * RoleCardService.INSTANCE.getCard(RoleBean.getRoleid())
	 * 
	 * @param 参数1：RoleBean
	 */
	RoleGetCard(new DefaultJavaFunc("_RoleGetCard") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(RoleCardService.INSTANCE.getCard(((RoleBean) this
					.getParam(2).getObject()).getRoleid()));
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
	private RoleCardJavaFunc(final DefaultJavaFunc jf) {
		this.jf = jf;
	}

	@Override
	public void register() {
		jf.register();
	}
	
}
