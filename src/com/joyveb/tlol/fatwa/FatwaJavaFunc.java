package com.joyveb.tlol.fatwa;
import org.keplerproject.luajava.LuaException;

import com.joyveb.tlol.DefaultJavaFunc;
import com.joyveb.tlol.LuaService;
import com.joyveb.tlol.TLOLJavaFunction;
import com.joyveb.tlol.role.RoleBean;

/**
 * 任务操作注册函数
 */
public enum FatwaJavaFunc implements TLOLJavaFunction {
	
	
	/**
	 * RoleBean.getFatwas().getMonitored().remove(int)
	 * 
	 * @param 参数1：RoleBean
	 * @param 参数2：int
	 */
	DoFatwa(new DefaultJavaFunc("_DoFatwa") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((RoleBean) this.getParam(2).getObject()).doFatwa((int) this.getParam(3).getNumber()));
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
	private FatwaJavaFunc(final DefaultJavaFunc jf) {
		this.jf = jf;
	}

	@Override
	public void register() {
		jf.register();
	}

}
