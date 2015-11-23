package com.joyveb.tlol.boss;

public class Boss {
	public static Boss instance = new Boss();
	private long bossMaxHp;// BOSS总血量
	private long bossHp;// BOSS血量
	private int def;// BOSS 防御
	private int level = 0;

	private long basicHp = 1000000l;

	/**
	 * @return the basicHp
	 */
	public long getBasicHp() {
		return basicHp;
	}

	/**
	 * @param basicHp the basicHp to set
	 */
	public void setBasicHp(long basicHp) {
		this.basicHp = basicHp;
	}

	private Boss() {
	}

	/**
	 * @return the bossMaxHp
	 */
	public long getBossMaxHp() {
		return bossMaxHp;
	}

	/**
	 * @param bossMaxHp
	 *            the bossMaxHp to set
	 */
	public void setBossMaxHp(long bossMaxHp) {
		this.bossMaxHp = bossMaxHp;
	}

	/**
	 * @return the bossHp
	 */
	public long getBossHp() {
		return bossHp;
	}

	/**
	 * @param bossHp
	 *            the bossHp to set
	 */
	public void setBossHp(long bossHp) {
		this.bossHp = bossHp;
	}

	/**
	 * @param hp
	 *            每次减去的血量
	 */
	public void minusHp(long hp) {
		this.bossHp = this.bossHp - hp;
	}

	/**
	 * @return the def
	 */
	public int getDef() {
		return def;
	}

	/**
	 * @param def
	 *            the def to set
	 */
	public void setDef(int def) {
		this.def = def;
	}

	/**
	 * @return the level
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * @param level
	 *            the level to set
	 */
	public void setLevel(int level) {
		this.level = level;
	}

}
