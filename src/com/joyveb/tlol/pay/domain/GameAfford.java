package com.joyveb.tlol.pay.domain;

import com.joyveb.tlol.TianLongServer;
import com.joyveb.tlol.role.RoleBean;

/**
 * 游戏添加元宝
 * 
 * @author SunHL
 * @下午01:36:20
 */
public class GameAfford {

	private RoleBean roleBean;
	/**
	 * 消费用户id
	 */
	private int userid;
	/**
	 * joyid
	 */
	private String joyid;
	/**
	 * 充值元宝数量
	 */
	private int amt;
	/**
	 * 原始金额
	 */
	private int originalAmt;

	/**
	 * 服务器ip
	 */
	private String serverIp;

	private int affordConst;// 操作类型

	public GameAfford(RoleBean roleBean, int userid, String joyid, int amt,
			int originalAmt, int affordConst) {
		this.roleBean = roleBean;
		this.userid = userid;
		this.joyid = joyid;
		this.amt = amt;
		this.originalAmt = originalAmt;
		serverIp = TianLongServer.serverIP;
		this.affordConst = affordConst;
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

	public int getAmt() {
		return amt;
	}

	public void setAmt(int amt) {
		this.amt = amt;
	}

	public String getServerIp() {
		return serverIp;
	}

	public void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}

	public int getAffordConst() {
		return affordConst;
	}

	public void setAffordConst(int affordConst) {
		this.affordConst = affordConst;
	}

	public int getOriginalAmt() {
		return originalAmt;
	}

	public void setOriginalAmt(int originalAmt) {
		this.originalAmt = originalAmt;
	}

	public RoleBean getRoleBean() {
		return roleBean;
	}

	public void setRoleBean(RoleBean roleBean) {
		this.roleBean = roleBean;
	}

}
