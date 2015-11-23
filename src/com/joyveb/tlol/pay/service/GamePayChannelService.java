package com.joyveb.tlol.pay.service;

import com.joyveb.tlol.pay.connect.ConnectTaskManager;
import com.joyveb.tlol.pay.domain.GamePayPram;
import com.joyveb.tlol.pay.domain.GameAfford;
import com.joyveb.tlol.pay.domain.GameSubtract;
import com.joyveb.tlol.pay.domain.SelectYuanbao;

public class GamePayChannelService {

	private static GamePayChannelService service = null;

	private GamePayChannelService() {

	}

	public static GamePayChannelService getInstance() {
		if (service == null) {
			service = new GamePayChannelService();
		}
		return service;
	}

	/**
	 * 查询所有平台
	 * 
	 * @return
	 */
	// public List queryAll(){
	// return paychannelDao.queryAll();
	// }
	// /**
	// * 查询特定通道
	// * @param code
	// * @return
	// */
	// public String queryByCode(String code){
	// return StringUtils.getProp(code, "payserver.properties");
	// }

	/**
	 * 插入失败记录
	 * 
	 * @param pram
	 */
	// public void insertGamePram(GamePayPram pram){
	// failPayDao.insertFailPay(pram);
	// }

	/**
	 * 组拼充值参数
	 */
	public static String getGamePayPara(GamePayPram pram) {
		StringBuffer sbCert = new StringBuffer();
		sbCert.append("&P_Amt=");
		sbCert.append(pram.getP_Amt() * 10);
		sbCert.append("&P_CardAmt=");
		sbCert.append(pram.getP_CardAmt());
		sbCert.append("&P_CardNo=");
		sbCert.append(pram.getP_CardNo());
		sbCert.append("&P_CardPwd=");
		sbCert.append(pram.getP_CardPwd());
		sbCert.append("&P_FrpId=");
		sbCert.append(pram.getP_FrpId());
		sbCert.append("&userid=");
		sbCert.append(pram.getUserid());
		sbCert.append("&joyid=");
		sbCert.append(pram.getJoyid());
		sbCert.append("&roleid=");
		sbCert.append(pram.getRoleid());
		sbCert.append("&srvid=");
		sbCert.append(pram.getSrvid());
		sbCert.append("&P_Md=");
		sbCert.append(pram.getP_Md());
		sbCert.append("&P_Rhv=");
		sbCert.append(pram.getP_Rhv());
		sbCert.append("&P_Pf=");
		sbCert.append(pram.getP_Pf());
		sbCert.append("&SERVERIP=");
		sbCert.append(pram.getServerIp());
		sbCert.append("&SERVERPORT=");
		sbCert.append(pram.getPayPort());
		sbCert.append("&gameid=");
		sbCert.append(ConnectTaskManager.GAMEID);
		sbCert.append("&P_Bnum=");
		sbCert.append(ConnectTaskManager.getInstance().getP_BNum());
		
		return sbCert.toString();
	}

	/**
	 * 组拼消费参数
	 * 
	 * @param subtract
	 * @return
	 */
	public static String getGamePaySubTract(GameSubtract subtract) {
		StringBuffer sbCert = new StringBuffer();
		sbCert.append("&userid=");
		sbCert.append(subtract.getUserid());
		sbCert.append("&joyid=");
		sbCert.append(subtract.getJoyid());
		sbCert.append("&amt=");
		sbCert.append(subtract.getAmt());
		sbCert.append("&serverIp=");
		sbCert.append(subtract.getServerIp());
		sbCert.append("&gameid=");
		sbCert.append(ConnectTaskManager.GAMEID);
		sbCert.append("&privateData=");
		sbCert.append(subtract.getItemid() + "_" + subtract.getItemNum() + "_"
				+ subtract.getOriginalAmt() + "_" + subtract.getTaskId() + "_"
				+ subtract.getSubConst());
		return sbCert.toString();
	}

	/**
	 * 组拼游戏添加元宝参数
	 * 
	 * @param afford
	 * @return
	 */
	public static String getGameAfford(GameAfford afford) {
		StringBuffer sbCert = new StringBuffer();
		sbCert.append("&userid=");
		sbCert.append(afford.getUserid());
		sbCert.append("&joyid=");
		sbCert.append(afford.getJoyid());
		sbCert.append("&amt=");
		sbCert.append(afford.getAmt());
		sbCert.append("&serverIp=");
		sbCert.append(afford.getServerIp());
		sbCert.append("&gameid=");
		sbCert.append(ConnectTaskManager.GAMEID);
		sbCert.append("&privateData=");
		int nowAmt = afford.getRoleBean().getMoney();
		sbCert.append(afford.getAffordConst() + "_" + nowAmt + "_" + afford.getAmt());
		return sbCert.toString();
	}
	
	/**
	 * 组拼查询用户元宝参数
	 * @param userid
	 * @return
	 */
	public static String getSelectAccount(SelectYuanbao selectYuanbao){
		StringBuffer sbCert = new StringBuffer();
		sbCert.append("&userid=");
		sbCert.append(selectYuanbao.getUserid());
		sbCert.append("&gameid=");
		sbCert.append(ConnectTaskManager.GAMEID);
		return sbCert.toString();
	}

}
