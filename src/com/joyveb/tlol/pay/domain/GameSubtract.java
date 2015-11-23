package com.joyveb.tlol.pay.domain;

import com.joyveb.tlol.TianLongServer;

public class GameSubtract {

	/**
	 * 消费用户id
	 */
	private int userid;
	/**
	 * joyid
	 */
	private String joyid;
	/**
	 *消费金额
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
	
	private int itemid;
	
	private int itemNum;
	
	private int taskId;
	
	private int subConst;//操作类型
	
	public GameSubtract(int userid, String joyid, int amt, int itemid, int itemNum, int originalAmt, int taskId, int subConst){
		this.userid = userid;
		this.joyid = joyid;
		this.amt = amt;
		this.itemid = itemid;
		this.itemNum = itemNum;
		this.originalAmt = originalAmt;
		serverIp = TianLongServer.serverIP;
		this.taskId = taskId;
		this.subConst = subConst;
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

	public int getOriginalAmt() {
		return originalAmt;
	}

	public int getItemid() {
		return itemid;
	}

	public int getItemNum() {
		return itemNum;
	}

	public int getTaskId() {
		return taskId;
	}

	public int getSubConst() {
		return subConst;
	}

	public void setSubConst(int subConst) {
		this.subConst = subConst;
	}

	public void setOriginalAmt(int originalAmt) {
		this.originalAmt = originalAmt;
	}

	public void setItemid(int itemid) {
		this.itemid = itemid;
	}

	public void setItemNum(int itemNum) {
		this.itemNum = itemNum;
	}

	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}
	
	
}
