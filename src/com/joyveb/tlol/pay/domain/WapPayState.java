package com.joyveb.tlol.pay.domain;


public class WapPayState {

	/**
	 * 充值状态,true为成功，false为失败
	 */
	private String state; 
	/**
	 * 充值用户id
	 */
	private int userid; 
	/**
	 * 充值用户joyid
	 */
	private String joyid; 
	/**
	 * 充值用户角色id
	 */
	private int roleid;
	/**
	 * 充值元宝数
	 */
	private int payamt;
	/**
	 * 错误代码
	 */
	private String wappaycode;
	/**
	 * 实际充值金额
	 * 
	 */
	private int allmoney;
	/**
	 * 实际成功金额
	 *
	 */
	private int realmoney;
	
	public int getAllmoney() {
		return allmoney;
	}
	public void setAllmoney(int allmoney) {
		this.allmoney = allmoney;
	}
	public int getRealmoney() {
		return realmoney;
	}
	public void setRealmoney(int realmoney) {
		this.realmoney = realmoney;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public int getUserid() {
		return userid;
	}
	public void setUserid(int userid) {
		this.userid = userid;
	}
	public String getJoyid() {
		return joyid;
	}
	public void setJoyid(String joyid) {
		this.joyid = joyid;
	}
	public int getRoleid() {
		return roleid;
	}
	public void setRoleid(int roleid) {
		this.roleid = roleid;
	}
	public int getPayamt() {
		return payamt;
	}
	public void setPayamt(int payamt) {
		this.payamt = payamt;
	}
	public String getWappaycode() {
		return wappaycode;
	}
	public void setWappaycode(String wappaycode) {
		this.wappaycode = wappaycode;
	}
	
}
