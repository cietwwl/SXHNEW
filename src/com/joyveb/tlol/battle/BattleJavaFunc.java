package com.joyveb.tlol.battle;

import org.keplerproject.luajava.LuaException;

import com.joyveb.tlol.DefaultJavaFunc;
import com.joyveb.tlol.TLOLJavaFunction;
import com.joyveb.tlol.role.RoleBean;

public enum BattleJavaFunc implements TLOLJavaFunction {

	broadCastBattleInfo(new DefaultJavaFunc("_broadCastBattleInfo") {
		@Override
		public int execute() throws LuaException {
			((Battle) this.getParam(2).getObject()).broadCastBattleInfo();
			return 0;
		}
	}),

	/**
	 * BOSS卡发生战斗
	 * 
	 * @param 参数1
	 *            ：RoleBean 人物
	 * @param 参数2
	 *            ：int 怪物ID
	 * @param 参数3
	 *            ：int 怪物数量
	 */
	BossCardBattleInfo(new DefaultJavaFunc("_BossCardBattleInfo") {
		@Override
		public int execute() throws LuaException {
			BattleAgent battleAgent = new BattleAgent((RoleBean) this.getParam(
					2).getObject());
			battleAgent.bossCardbattle((int) this.getParam(3).getNumber(),
					(int) this.getParam(4).getNumber());
			return 0;
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
	private BattleJavaFunc(final DefaultJavaFunc jf) {
		this.jf = jf;
	}

	@Override
	public void register() {
		jf.register();

	}

}
