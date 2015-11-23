package com.joyveb.tlol.guess;

import org.keplerproject.luajava.LuaException;

import com.joyveb.tlol.DefaultJavaFunc;
import com.joyveb.tlol.LuaService;
import com.joyveb.tlol.TLOLJavaFunction;
import com.joyveb.tlol.role.RoleBean;




/**
 * @猜猜看
 * @author SunHL
 * @下午05:38:05
 */
public enum GuessJavaFunc implements TLOLJavaFunction {

	/**
	 * GuessManager.addRole 将猜猜看玩家添加到vector
	 * 
	 * @param 参数1
	 *            ：int 人物ID
	 * @param 参数2
	 *            ：int 押注金额
	 * @param 参数3
	 *            ：int 押注项目
	 */
	GuessAddRole(new DefaultJavaFunc("_GuessAddRole") {
		@Override
		public final int execute() throws LuaException {
			GuessManager.getInstance().addBetRole(
					(int) this.getParam(2).getNumber(),
					(int) this.getParam(3).getNumber(),
					(int) this.getParam(4).getNumber());
			return 1;
		}
	}),
	/**
	 * GuessManager.getLastRoundResult 查看上局结果
	 */
	GuessGetLastRoundResult(new DefaultJavaFunc("_GuessGetLastRoundResult") {
		@Override
		public final int execute() throws LuaException {
			LuaService.push(GuessManager.getInstance().getLastRoundResult());
			return 1;
		}
	}),
	/**
	 * GuessManager.getRemainTime 查看剩余时间
	 */
	GuessGetRemainTime(new DefaultJavaFunc("_GuessGetRemainTime") {
		@Override
		public final int execute() throws LuaException {
			LuaService.push(GuessManager.getInstance().getRemainTime());
			return 1;
		}
	}),
	/**
	 * GuessManager.betMoney 查看玩家可押注金额
	 * 
	 * @param 参数1
	 *            ：RoleBean 押注玩家
	 */
	GuessBetMoney(new DefaultJavaFunc("_GuessBetMoney") {
		@Override
		public final int execute() throws LuaException {
			LuaService.push(GuessManager.getInstance().betMoney(
					(RoleBean) this.getParam(2).getObject()));
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
	private GuessJavaFunc(final DefaultJavaFunc jf) {
		this.jf = jf;
	}

	@Override
	public void register() {
		jf.register();
	}
}
