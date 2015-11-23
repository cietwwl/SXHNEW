package com.joyveb.tlol.racing;

import org.keplerproject.luajava.LuaException;

import com.joyveb.tlol.DefaultJavaFunc;
import com.joyveb.tlol.LuaService;
import com.joyveb.tlol.TLOLJavaFunction;
import com.joyveb.tlol.role.RoleBean;

/**
 * 赛马的javaFunction（暂时无用）
 * 
 * @author SunHL
 * @下午05:16:13
 */
public enum RacingJavaFunc implements TLOLJavaFunction {

	/**
	 * RacingManager.RacingSurplusMarks 获取赛马历史战报
	 * 
	 * @param 参数1
	 *            ：RacingManager
	 */
	RacingGetHistoryMessage(new DefaultJavaFunc("_RacingGetHistoryMessage") {
		@Override
		public final int execute() throws LuaException {
			LuaService.push(RacingManager.getInstance().racingHistoryMessage(
					(int) this.getParam(2).getNumber(),
					(String) this.getParam(3).getString(),
					(String) this.getParam(4).getString()));
			return 1;
		}
	}),
	/**
	 * RacingManager.RacingSurplusMarks 获取赛马战报
	 * 
	 * @param 参数1
	 *            ：RacingManager
	 */
	RacingGetMessage(new DefaultJavaFunc("_RacingGetMessage") {
		@Override
		public final int execute() throws LuaException {
			LuaService.push(((RoleBean) this.getParam(2).getObject())
					.getRacingMessage());
			return 1;
		}
	}),
	/**
	 * RacingManager.RacingSurplusMarks 获取奖品积分
	 * 
	 * @param 参数1
	 *            ：RacingManager
	 */
	RacingDeductMarks(new DefaultJavaFunc("_RacingDeductMarks") {
		@Override
		public final int execute() throws LuaException {
			LuaService.push(RacingManager.getInstance().racingDeductMarks(
					(int) this.getParam(2).getNumber()));
			return 1;
		}
	}),
	/**
	 * RacingManager.RacingSurplusMarks 获取奖品编号
	 * 
	 * @param 参数1
	 *            ：RacingManager
	 */
	RacingPrizeCode(new DefaultJavaFunc("_RacingPrizeCode") {
		@Override
		public final int execute() throws LuaException {
			LuaService.push(RacingManager.getInstance().racingPrizeCode(
					(int) this.getParam(2).getNumber()));
			return 1;
		}
	}),
	/**
	 * RacingManager.RacingSurplusMarks 获取赛马积分
	 * 
	 * @param 参数1
	 *            ：RacingManager
	 */
	RacingGetMarks(new DefaultJavaFunc("_RacingGetMarks") {
		@Override
		public final int execute() throws LuaException {
			LuaService.push(RacingManager.getInstance().racingGetMarks(
					(RoleBean) this.getParam(2).getObject()));
			return 1;
		}
	}),
	/**
	 * RacingManager.RacingSurplusMarks 设置赛马积分
	 * 
	 * @param 参数1
	 *            ：RacingManager
	 */
	RacingSetMarks(new DefaultJavaFunc("_RacingSetMarks") {
		@Override
		public int execute() throws LuaException {
			RacingManager.getInstance().racingSetMarks(
					(RoleBean) this.getParam(2).getObject(),
					(int) this.getParam(3).getNumber());
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
	private RacingJavaFunc(final DefaultJavaFunc jf) {
		this.jf = jf;
	}

	@Override
	public void register() {
		jf.register();
	}
}
