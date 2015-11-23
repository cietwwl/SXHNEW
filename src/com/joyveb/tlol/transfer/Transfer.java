package com.joyveb.tlol.transfer;

public class Transfer {

	private int userId;
	private int outTime;

	public Transfer(int userId, int outTime) {
		this.userId = userId;
		this.outTime = outTime;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getOutTime() {
		return outTime;
	}

	public void setOutTime(int outTime) {
		this.outTime = outTime;
	}

}
