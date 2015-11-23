package com.joyveb.tlol.role;

/**
 * 角色职业
 * @author Sid
 */
public enum Vocation {
	/** 侠客 */
	SHAQ("侠客"),
	/** 术士 */
	Warlock("术士"),
	/** 刺客 */
	Assassin("刺客");
	
	/** 职业名称 */
	private final String name;
	
	/**
	 * @param name 职业名称
	 */
	private Vocation(final String name) {
		this.name = name;
	}

	/** @return 职业名称 */
	public final String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
