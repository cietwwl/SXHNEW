package com.joyveb.tlol.betAccount;

import org.keplerproject.luajava.LuaException;

import com.joyveb.tlol.DefaultJavaFunc;
import com.joyveb.tlol.LuaService;
import com.joyveb.tlol.TLOLJavaFunction;
import com.joyveb.tlol.role.RoleBean;

/**
 * 元宝赌数javaFunction
 * @author SunHL
 * @下午 05:16:13
 */
public enum BetAccountJavaFunc implements TLOLJavaFunction {

	/**
	 * BetAccountManager.addBetRole
	 * 将押注人员添加到vector
	 * @param 参数1：RoleBean 押注玩家
	 */
	BetAccountAddBetRole(new DefaultJavaFunc("_BetAccountAddBetRole") {
		@Override
		public final int execute() throws LuaException {
			BetAccountManager.getInstance().addBetRole((RoleBean)this.getParam(2).getObject(), (int)this.getParam(3).getNumber(), (int)this.getParam(4).getNumber());
			return 1;
		}
	}),
	/**
	 * BetAccountManager.getOddsByBet
	 * 根据押注点数获取比率
	 * @param 参数1：int 压住点数
	 */
	GetOddsByBet(new DefaultJavaFunc("_GetOddsByBet") {
		@Override
		public final int execute() throws LuaException {
			LuaService.push(BetAccountManager.getInstance().getOddsByBet((int)this.getParam(2).getNumber()));
			return 1;
		}
	}),
	/**
	 * BetAccountManager.getLastRoundResult
	 * 查看上局中奖结果
	 */
	BetAccountgetLastRoundResult(new DefaultJavaFunc("_BetAccountgetLastRoundResult") {
		@Override
		public final int execute() throws LuaException {
			LuaService.push(BetAccountManager.getInstance().getLastRoundResult());
			return 1;
		}
	}),
	/**
	 * BetAccountManager.getRemainTime
	 * 查看本局剩余时间
	 */
	BetAccountGetRemainTime(new DefaultJavaFunc("_BetAccountGetRemainTime") {
		@Override
		public final int execute() throws LuaException {
			LuaService.push(BetAccountManager.getInstance().getRemainTime());
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
	private BetAccountJavaFunc(final DefaultJavaFunc jf) {
		this.jf = jf;
	}
	
	@Override
	public void register() {
		jf.register();
	}
}
