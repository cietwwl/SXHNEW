package com.joyveb.tlol.pay.domain;

public class AffordState {

	public static final int BET_ACCOUNT = 7;
	/**
	 * 充值状态，true为成功，false为失败
	 */
	private String state;
	/**
	 * 充值用户id
	 */
	private String userid;
	/**
	 * joyid
	 */
	private String joyid;
	/**
	 * 充值金额
	 */
	private int cost;
	/**
	 * 充值描述
	 */
	private String des;
	/**
	 * 错误状态码,分为0,1,2,3,4,5.0为成功，其他按照错误等级发送邮件
	 */
	private String code;
	/**
	 * 原始金额
	 */
	private int originalPrice;
	/**
	 * 当前金额(充值后金额)
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

	public void setResAmt(int nowamt) {
		this.nowamt = nowamt;
	}

	public int getOriginalPrice() {
		return originalPrice;
	}

	public void setOriginalPrice(int originalPrice) {
		this.originalPrice = originalPrice;
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

	public String getJoyid() {
		return joyid;
	}

	public void setJoyid(String joyid) {
		this.joyid = joyid;
	}

	public int getCost() {
		return cost;
	}

	public void setCost(int cost) {
		this.cost = cost;
	}

	public String getDes() {
		return des;
	}

	public void setDes(String des) {
		this.des = des;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setNowamt(int nowamt) {
		this.nowamt = nowamt;
	}

}
