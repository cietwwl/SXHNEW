package com.joyveb.tlol.pay.domain;

import java.util.Date;

public class GamePayPram {

	/**
	 * 编号���
	 */
	private int id;
	/**
	 * joyid
	 */
	private String joyid; 
	/**
	 * �通道代号ֵͨ��
	 */
	private String channelCode; 
	/**
	 * ��支付金额ֵ���
	 */
	private int p_Amt; 
	/**
	 * 卡上金额�����
	 */
	private int p_CardAmt; 
	/**
	 * 卡号����
	 */
	private String p_CardNo; 
	/**
	 * 卡密码 ����
	 */
	private String p_CardPwd;
	/**
	 * 支付渠道编码�������
	 */
	private String p_FrpId;
	/**
	 * 机型ID
	 */
	private String p_Md;
	/**
	 * 子机型ID
	 */
	private String p_Rhv;
	/**
	 * ����机型平台
	 */
	private String p_Pf;
	/**
	 * �z用户id
	 */
	private int userid;
	/**
	 * 请求时间
	 */
	private Date requesttime;
	/**
	 * 处理状态
	 */
	private int processState;
	/**
	 * 服务器ip
	 */
	private String serverIp;
	/**
	 * 监听端口
	 * @return
	 */
	private String payPort;
	/**
	 * 角色id
	 * @return
	 */
	private int roleid;
	/**
	 * 服务器id(统计需求)
	 * @return
	 */
	private String srvid;
	/**
	 * 服务器商户编号
	 * @return
	 */
	private String p_BNum;
	public String getP_BNum() {
		return p_BNum;
	}
	public void setP_BNum(String num) {
		p_BNum = num;
	}
	public String getSrvid() {
		return srvid;
	}
	public void setSrvid(String srvid) {
		this.srvid = srvid;
	}
	public int getRoleid() {
		return roleid;
	}
	public void setRoleid(int roleid) {
		this.roleid = roleid;
	}
	public String getPayPort() {
		return payPort;
	}
	public void setPayPort(String payPort) {
		this.payPort = payPort;
	}
	public String getServerIp() {
		return serverIp;
	}
	public void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getJoyid() {
		return joyid;
	}
	public void setJoyid(String joyid) {
		this.joyid = joyid;
	}
	public String getChannelCode() {
		return channelCode;
	}
	public void setChannelCode(String channelCode) {
		this.channelCode = channelCode;
	}
	public int getP_Amt() {
		return p_Amt;
	}
	public void setP_Amt(int amt) {
		p_Amt = amt;
	}
	public int getP_CardAmt() {
		return p_CardAmt;
	}
	public void setP_CardAmt(int cardAmt) {
		p_CardAmt = cardAmt;
	}
	public String getP_CardNo() {
		return p_CardNo;
	}
	public void setP_CardNo(String cardNo) {
		p_CardNo = cardNo;
	}
	public String getP_CardPwd() {
		return p_CardPwd;
	}
	public void setP_CardPwd(String cardPwd) {
		p_CardPwd = cardPwd;
	}
	public String getP_FrpId() {
		return p_FrpId;
	}
	public void setP_FrpId(String frpId) {
		if(frpId == null || "".equals(frpId))
			p_FrpId = "SZX";
		else
			p_FrpId = frpId;
	}
	public String getP_Md() {
		return p_Md;
	}
	public void setP_Md(String md) {
		p_Md = md;
	}
	public String getP_Rhv() {
		return p_Rhv;
	}
	public void setP_Rhv(String rhv) {
		p_Rhv = rhv;
	}
	public String getP_Pf() {
		return p_Pf;
	}
	public void setP_Pf(String pf) {
		p_Pf = pf;
	}
	public int getUserid() {
		return userid;
	}
	public void setUserid(int userid) {
		this.userid = userid;
	}
	public Date getRequesttime() {
		return requesttime;
	}
	public void setRequesttime(Date requesttime) {
		this.requesttime = requesttime;
	}
	public int getProcessState() {
		return processState;
	}
	public void setProcessState(int processState) {
		this.processState = processState;
	}
	
}
