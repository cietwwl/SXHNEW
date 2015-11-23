package com.joyveb.tlol.javafunc;

import org.keplerproject.luajava.LuaException;

import com.joyveb.tlol.DefaultJavaFunc;
import com.joyveb.tlol.SubModules;
import com.joyveb.tlol.TLOLJavaFunction;
import com.joyveb.tlol.charge.ShenZhouChargeInfo;
import com.joyveb.tlol.role.RoleBean;

/**
 * 填充消息子模块
 * @author Sid
 */
public enum SubModulesJavaFunc implements TLOLJavaFunction {
	/**
	 * SubModules.fillAttributes()
	 * 
	 * @param 参数1：RoleBean
	 */
	FillAttributes(new DefaultJavaFunc("fillAttributes") {
		@Override
		public int execute() throws LuaException {
			SubModules.fillAttributes((RoleBean) this.getParam(2).getObject());
			return 0;
		}
	}),

	/**
	 * SubModules.fillAttributesDes()
	 * 
	 * @param 参数1：RoleBean
	 */
	FillAttributesDes(new DefaultJavaFunc("fillAttributesDes") {
		@Override
		public int execute() throws LuaException {
			SubModules.fillAttributesDes((RoleBean) this.getParam(2).getObject());
			return 0;
		}
	}),

	/**
	 * SubModules.fillDepotsInfo()
	 * 
	 * @param 参数1：RoleBean
	 */
	FillDepotsInfo(new DefaultJavaFunc("fillDepotsInfo") {
		@Override
		public int execute() throws LuaException {
			SubModules.fillDepotsInfo((RoleBean) this.getParam(2).getObject());
			return 0;
		}
	}),

	/**
	 * SubModules.fillEpithet()
	 * 
	 * @param 参数1：RoleBean
	 */
	FillEpithet(new DefaultJavaFunc("fillEpithet") {
		@Override
		public int execute() throws LuaException {
			SubModules.fillEpithet((RoleBean) this.getParam(2).getObject());
			return 0;
		}
	}),

	/**
	 * ShenZhouChargeInfo.getInstance().sendChargeInfo()
	 * 
	 * @param 参数1：RoleBean
	 */
	FillShenZhouChargeInfo(new DefaultJavaFunc("fillShenZhouChargeInfo") {
		@Override
		public int execute() throws LuaException {
			ShenZhouChargeInfo.getInstance().sendChargeInfo((RoleBean) this.getParam(2).getObject());
			return 0;
		}
	}),

	;

	/**
	 * 实现默认的可注册Java函数
	 */
	private final DefaultJavaFunc jf;

	/**
	 * @param jf 可注册Java函数
	 */
	private SubModulesJavaFunc(final DefaultJavaFunc jf) {
		this.jf = jf;
	}

	@Override
	public void register() {
		jf.register();
	}

}
