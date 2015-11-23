package com.joyveb.tlol.bigOrSmall;

import org.keplerproject.luajava.LuaException;

import com.joyveb.tlol.DefaultJavaFunc;
import com.joyveb.tlol.LuaService;
import com.joyveb.tlol.TLOLJavaFunction;
import com.joyveb.tlol.role.RoleBean;
/**
 * 押大小的javaFunction（暂时无用）
 * 2011-1-17
 * LiJG
 */
public enum BigOrSmallJavaFunc implements TLOLJavaFunction {

	/**
	 * BigOrSmallManager.addBetRole
	 * 将押注人员添加到vector
	 * @param 参数1：int 人物ID
	 * @param 参数2：int 押注金额
	 * @param 参数3：int 押注项目
	 */
	BigOrSmallAddBetRole(new DefaultJavaFunc("_BigOrSmallAddBetRole") {
		@Override
		public final int execute() throws LuaException {
			BigOrSmallManager.getInstance().addBetRole((int)this.getParam(2).getNumber(), (int)this.getParam(3).getNumber(), (int)this.getParam(4).getNumber());
			return 1;
		}
	}),
	/**
	 * BigOrSmallManager.getLastRoundResult
	 * 查看上局结果
	 */
	BigOrSmallgetLastRoundResult(new DefaultJavaFunc("_BigOrSmallgetLastRoundResult") {
		@Override
		public final int execute() throws LuaException {
			LuaService.push(BigOrSmallManager.getInstance().getLastRoundResult());
			return 1;
		}
	}),
	/**
	 * BigOrSmallManager.getRemainTime
	 * 查看剩余时间
	 */
	BigOrSmallGetRemainTime(new DefaultJavaFunc("_BigOrSmallGetRemainTime") {
		@Override
		public final int execute() throws LuaException {
			LuaService.push(BigOrSmallManager.getInstance().getRemainTime());
			return 1;
		}
	}),
	/**
	 * BigOrSmallManager.betMoney
	 * 查看玩家可押注金额
	 * @param 参数1：RoleBean 押注玩家
	 */
	BigOrSmallBetMoney(new DefaultJavaFunc("_BigOrSmallBetMoney") {
		@Override
		public final int execute() throws LuaException {
			LuaService.push(BigOrSmallManager.getInstance().betMoney((RoleBean)this.getParam(2).getObject()));
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
	private BigOrSmallJavaFunc(final DefaultJavaFunc jf) {
		this.jf = jf;
	}
	
	@Override
	public void register() {
		jf.register();
	}
}
