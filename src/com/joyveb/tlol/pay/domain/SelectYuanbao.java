package com.joyveb.tlol.pay.domain;

import com.joyveb.tlol.TianLongServer;
import com.joyveb.tlol.role.RoleBean;

/**
 * 查询元宝
 * 
 * @author SunHL
 * @下午01:36:20
 */
public class SelectYuanbao {

	private RoleBean roleBean;
	/**
	 * 用户id
	 */
	private int userid;
	
	/**
	 * 原始金额
	 */
	private int originalAmt;

	/**
	 * 服务器ip
	 */
	private String serverIp;

	private int affordConst;// 操作类型

	public SelectYuanbao(RoleBean roleBean, int userid,int affordConst) {
		this.roleBean = roleBean;
		this.userid = userid;
		serverIp = TianLongServer.serverIP;
		this.affordConst = affordConst;
	}

	public int getUserid() {
		return userid;
	}

	public void setUserid(int userid) {
		this.userid = userid;
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
