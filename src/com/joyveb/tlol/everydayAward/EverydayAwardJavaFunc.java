package com.joyveb.tlol.everydayAward;

import org.keplerproject.luajava.LuaException;

import com.joyveb.tlol.DefaultJavaFunc;
import com.joyveb.tlol.LuaService;
import com.joyveb.tlol.TLOLJavaFunction;
import com.joyveb.tlol.item.Item;
import com.joyveb.tlol.role.RoleBean;

/**
 * @每日开奖
 * @author SunHL
 * @下午05:38:05
 */
public enum EverydayAwardJavaFunc implements TLOLJavaFunction {

	/**
	 * EverydayAwardManager.getGoldBox 开启金宝箱
	 * 
	 * @param 参数1
	 *            ：RoleBean 角色
	 * @param 参数2
	 *            ：Item 物品
	 */
	GetBox(new DefaultJavaFunc("_GetBox") {
		@Override
		public final int execute() throws LuaException {
			EverydayAwardManager.getInstance().getBox(
					(RoleBean) this.getParam(2).getObject(),
					(Item) this.getParam(3).getObject());
			return 1;
		}
	}),

	/**
	 * EverydayAwardManager.roleBoxKey 查看钥匙数量
	 * 
	 * @param 参数1
	 *            ：RoleBean 角色
	 * @param 参数2
	 *            ：int 钥匙ID
	 */
	RoleBoxKey(new DefaultJavaFunc("_RoleBoxKey") {
		@Override
		public final int execute() throws LuaException {
			LuaService.push(EverydayAwardManager.getInstance().roleBoxKeyCount(
					(RoleBean) this.getParam(2).getObject(),
					(int) this.getParam(3).getNumber()));
			return 1;
		}
	});

	/**
	 * 实现默认的可注册Java函数
	 */
	private final DefaultJavaFunc jf;

	/**
	 * @param jf
	 *            可注册Java函数
	 */
	private EverydayAwardJavaFunc(final DefaultJavaFunc jf) {
		this.jf = jf;
	}

	@Override
	public void register() {
		jf.register();
	}
}
