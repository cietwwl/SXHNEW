package com.joyveb.tlol.javafunc;

import org.keplerproject.luajava.LuaException;

import com.joyveb.tlol.DefaultJavaFunc;
import com.joyveb.tlol.LuaService;
import com.joyveb.tlol.OnlineService;
import com.joyveb.tlol.TLOLJavaFunction;

/**
 * 在线角色相关函数注册
 * @author Sid
 */
public enum OnlineJavaFunc implements TLOLJavaFunction {
	/**
	 * OnlineService.getAllOnlines()
	 */
	GetAllOnlines(new DefaultJavaFunc("_GetAllOnlines") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(OnlineService.getAllOnlines());
			return 1;
		}
	}),
	
	/**
	 * OnlineService.getOnline()
	 * 
	 * @param 参数1：int 角色id
	 */
	GetOnline(new DefaultJavaFunc("_GetOnline") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(OnlineService.getOnline((int) this.getParam(2).getNumber()));
			return 1;
		}
	})
,
	
	/**
	 * RoleBean.userIdGetRoleId
	 * 
	 * @param 参数1：
	 */
	GetRoleId(new DefaultJavaFunc("_GetRoleId") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(OnlineService.userIdGetRoleID((int)this.getParam(2).getNumber()));
			return 1;
		}
	}),
	
	/**
	 * 
	 * 
	 * @param 参数1：
	 */
	KickUnLogUser(new DefaultJavaFunc("_KickUnLogUser") {
		@Override
		public int execute() throws LuaException {
			OnlineService.kickUnUser((int) this.getParam(2).getNumber());
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
	private OnlineJavaFunc(final DefaultJavaFunc jf) {
		this.jf = jf;
	}

	@Override
	public void register() {
		jf.register();
	}
	
}
