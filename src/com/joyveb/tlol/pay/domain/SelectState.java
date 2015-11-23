package com.joyveb.tlol.pay.domain;

public class SelectState {

	/**
	 * 查询状态，true为成功，false为失败
	 */
	private String state;
	/**
	 * 充值用户id
	 */
	private String userid;
		
	/**
	 * 当前金额
	 */
	private int nowamt;
	/**
	 * 操作类型
	 */
	private int payConst;

	public int getPayConst() {
		return payConst;
	}

	public void setPayConst(int payConst) {
		this.payConst = payConst;
	}

	public int getNowamt() {
		return nowamt;
	}

	public void setNowAmt(int nowamt) {
		this.nowamt = nowamt;
	}	

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}
}
