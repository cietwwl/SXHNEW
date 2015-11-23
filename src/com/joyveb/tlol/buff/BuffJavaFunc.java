package com.joyveb.tlol.buff;

import org.keplerproject.luajava.LuaException;

import com.joyveb.tlol.DefaultJavaFunc;
import com.joyveb.tlol.LuaService;
import com.joyveb.tlol.TLOLJavaFunction;
import com.joyveb.tlol.role.RoleBean;

/**
 * Buff相关函数注册
 * @author Sid
 */
public enum BuffJavaFunc implements TLOLJavaFunction {
	/**
	 * BuffType.getType(byte).creatBuff()
	 * 
	 * @param 参数1：byte
	 */
	CreatBuff(new DefaultJavaFunc("_CreatBuff") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(BuffType.getType((byte) this.getParam(2).getNumber()).creatBuff());
			return 1;
		}
	}),

	/**
	 * Buff.setAttr(byte, long, byte, int)
	 * 
	 * @param 参数1：Buff
	 * @param 参数2：byte
	 * @param 参数3：long
	 * @param 参数4：byte
	 * @param 参数5：int
	 */
	BuffSetAttr(new DefaultJavaFunc("_BuffSetAttr") {
		@Override
		public int execute() throws LuaException {
			((Buff) this.getParam(2).getObject()).setAttr((byte) this.getParam(3).getNumber(), (long) this.getParam(4)
					.getNumber(), (byte) this.getParam(5).getNumber(), (int) this.getParam(6).getNumber());
			return 0;
		}
	}),

	/**
	 * RoleBean.getBuffManager().addBuff(Buff)
	 * 
	 * @param 参数1：RoleBean
	 * @param 参数2：Buff
	 */
	RoleAddBuff(new DefaultJavaFunc("_RoleAddBuff") {
		@Override
		public int execute() throws LuaException {
			((RoleBean) this.getParam(2).getObject()).getBuffManager().addBuff((Buff) this.getParam(3).getObject());
			return 0;
		}
	}),

	/**
	 * RoleBean.getBuffManager().canAddBuff(byte, byte)
	 * 
	 * @param 参数1：RoleBean
	 * @param 参数2：byte
	 * @param 参数3：byte
	 */
	CanAddBuff(new DefaultJavaFunc("_CanAddBuff") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((RoleBean) this.getParam(2).getObject()).getBuffManager().canAddBuff(
					(byte) this.getParam(3).getNumber(), (byte) this.getParam(4).getNumber()));

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
	private BuffJavaFunc(final DefaultJavaFunc jf) {
		this.jf = jf;
	}

	@Override
	public void register() {
		jf.register();
	}

}
