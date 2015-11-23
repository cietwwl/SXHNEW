package com.joyveb.tlol.pay.domain;

public class SubtractState {
	
	public static final int Buy_ITEM_FROM_MALL = 1;
	public static final int OPEN_YUANBAO_GOLD_BOX = 2;
	public static final int OPEN_YUANBAO_SILVER_BOX = 3;
	public static final int OPEN_YUANBAO_COPPER_BOX = 4;
	public static final int FASTREFRESH_GOLD_BOX = 5;
	public static final int FASTREFRESH_YUANBAO_BOX = 6;
	public static final int BET_ACCOUNT = 7;
	public static final int SELECT_YUANBAO = 8;
	public static final int BUY_JINYAOSHI = 9;
	public static final int BUY_BOSS = 10;
	
	/**
	 * 消费状态，true为成功，false为失败
	 */
	private String state;
	/**
	 * 消费用户id
	 */
	private String userid;
	/**
	 * joyid
	 */
	private String joyid;
	/**
	 * 消费金额
	 */
	private int cost;
	/**
	 * 消费描述
	 */
	private String des;
	/**
	 * 错误状态码,分为0,1,2,3,4,5.0为成功，其他按照错误等级发送邮件
	 */
	private String code;
	/*
	 * 物品id
	 */
    private int itemid;
	/**
	 * 物品数量
	 */
	private int itemNum;
	/**
	 * 原始金额
	 */
	private int originalPrice;
	/**
	 * 当前金额(扣费后金额)
	 */
	private int nowamt;
	
	private int taskId;
	
	private int subConst;//操作类型
	
	public int getSubConst() {
		return subConst;
	}
	public void setSubConst(int subConst) {
		this.subConst = subConst;
	}
	public int getNowamt() {
		return nowamt;
	}
	public void setResAmt(int nowamt) {
		this.nowamt = nowamt;
	}
	public int getItemid() {
		return itemid;
	}
	public void setItemid(int itemid) {
		this.itemid = itemid;
	}
	public int getItemNum() {
		return itemNum;
	}
	public void setItemNum(int itemNum) {
		this.itemNum = itemNum;
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
	public int getTaskId() {
		return taskId;
	}
	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}
	
	
}
