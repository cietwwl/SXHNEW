package com.joyveb.tlol.pay.domain;

public class PayState {

	/**
	 * 充值状态,true为成功，false为失败
	 */
	private String state; 
	/**
	 * 充值用户id
	 */
	private String userid; 
	/**
	 * 充值用户joyid
	 */
	private String joyid; 
	/**
	 * 充值订单号
	 */
	private String order; 

	/**
	 * 错误描述
	 */
	private String desc;
	/**
	 * 失败充值金额
	 */
	private int amt;
	/**
	 * 错误代码
	 */
	private int code;
	
	
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
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
	public String getOrder() {
		return order;
	}
	public void setOrder(String order) {
		this.order = order;
	}
	public int getAmt() {
		return amt;
	}
	public void setAmt(int amt) {
		this.amt = amt;
	}
	
}
