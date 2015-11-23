package com.joyveb.tlol.boss;


public class Fighter {
	private int roleId;
	private String name;
	private long hit;// 攻击者攻击
	private long totalHit;
	private boolean state;// true 为可以战斗，false为不可战斗，再次报名就激活
	private int freeze = 2 * 1000;// 冷却时间
	private long lastTime;

	/**
	 * @return the roleId
	 */
	public int getRoleId() {
		return roleId;
	}

	/**
	 * @param roleId
	 *            the roleId to set
	 */
	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the hit
	 */
	public long getHit() {
		return hit;
	}

	/**
	 * @param hit
	 *            the hit to set
	 */
	public void setHit(long hit) {
		this.hit = hit;
	}

	/**
	 * @return the totalHit
	 */
	public long getTotalHit() {
		return totalHit;
	}

	/**
	 * @param totalHit
	 *            the totalHit to set
	 */
	public void setTotalHit(long totalHit) {
		this.totalHit = totalHit;
	}

	/**
	 * @param hit
	 *            伤害量
	 */
	public void addTotalHit(long hit) {
		this.totalHit += hit;
	}

	/**
	 * @return the state
	 */
	public boolean isState() {
		return state;
	}

	/**
	 * @param state
	 *            the state to set
	 */
	public void setState(boolean state) {
		this.state = state;
	}

	/**
	 * @return the freeze
	 */
	public int getFreeze() {
		return freeze;
	}

	/**
	 * @param freeze
	 *            the freeze to set
	 */
	public void setFreeze(int freeze) {
		this.freeze = freeze;
	}

	/**
	 * @return the lastTime
	 */
	public long getLastTime() {
		return lastTime;
	}

	/**
	 * @param lastTime the lastTime to set
	 */
	public void setLastTime(long lastTime) {
		this.lastTime = lastTime;
	}



}
