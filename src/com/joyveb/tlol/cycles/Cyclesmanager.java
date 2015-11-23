package com.joyveb.tlol.cycles;

import com.joyveb.tlol.role.RoleBean;

public final class Cyclesmanager {
	
	/**单例类的常量*/
	private static Cyclesmanager instance = new Cyclesmanager();
	/**
	 * 构造方法
	 */
	private Cyclesmanager() {
		
	}
	
	/**
	 * @return instance
	*/
	public static Cyclesmanager getInstance() {
		return instance;
	}

	/**
	 * 可押注金额
	 * @param role 角色对象
	 * @return String 可押注的金额（单位：银）
	 */
	public String betCyclesMoney(final RoleBean role) {
		int roleGold = role.getGold() / 100;		
		return roleGold + "银";
	}
}
