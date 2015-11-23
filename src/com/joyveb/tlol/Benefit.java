package com.joyveb.tlol;

/**
 * 福利
 */
public enum Benefit {
	/** 经验 */
	EXP(1);

	/** 福利id */
	private int id;

	private Benefit(final int id) {
		this.setId(id);
	}

	public void setId(final int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

}
