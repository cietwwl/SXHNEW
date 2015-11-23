package com.joyveb.tlol.charge;

import com.joyveb.tlol.TianLongServer;
import com.joyveb.tlol.core.AgentProxy;
import com.joyveb.tlol.fee.FeeService;
import com.joyveb.tlol.net.IncomingMsg;
import com.joyveb.tlol.pay.connect.ConnectCommonParser;
import com.joyveb.tlol.pay.connect.ConnectTaskManager;
import com.joyveb.tlol.pay.domain.GamePayPram;
import com.joyveb.tlol.protocol.ChargeInfoBody;
import com.joyveb.tlol.protocol.MsgID;
import com.joyveb.tlol.role.RoleBean;
import com.joyveb.tlol.util.Log;

public class ChargeAgent extends AgentProxy {

	public ChargeAgent(final RoleBean player) {
		this.player = player;
	}
	
	private boolean ifcon() {

		for (int i = 0; i < FeeService.INSTANCE.getFeeList().size(); i++) {
			if (ChargeInfoBody.INSTANCE.getIndex() == FeeService.INSTANCE.getFeeList().get(i).getFeeKey()) {
				ChargeInfoBody.INSTANCE.setP_FrpId(FeeService.INSTANCE.getFeeList().get(i).getFeeSign());
				return true;
			}
		}
		return false;
	}

	public String getFeeNames() {
		String str = "";
		if (FeeService.INSTANCE.getFeeList().size() > 0)
			for (int i = 0; i < FeeService.INSTANCE.getFeeList().size(); i++) {
				str += " " + FeeService.INSTANCE.getFeeList().get(i).getFeeName();
			}
		return str;
	}
	@Override
	public final void processCommand(final IncomingMsg msg) {
		switch (MsgID.getInstance(msg.getHeader().getMsgID())) {
		case MsgID_Charge_ChargeInfo:
			if (ChargeInfoBody.INSTANCE.readBody(msg.getBody())) {
				if (!ifcon()) {
					
					sendMsg(player, MsgID.MsgID_Charge_ChargeInfo_Resp, "此充值已停用,现在支持的充值方式为"+getFeeNames()+",请选择正确的充值方式！");
				} else {
						GamePayPram gpp = new GamePayPram();
						gpp.setChannelCode("szf");
						gpp.setP_Amt(0);
						gpp.setP_CardAmt(ChargeInfoBody.INSTANCE.getCardAmt());
						gpp.setUserid(player.getUserid());					
						gpp.setP_CardNo(ChargeInfoBody.INSTANCE.getCardId());
						gpp.setP_FrpId(ChargeInfoBody.INSTANCE.getP_FrpId());
						gpp.setJoyid(player.getJoyid());
						gpp.setRoleid(player.getId());//角色编号
						gpp.setP_CardPwd(ChargeInfoBody.INSTANCE.getCardPwd());
						gpp.setServerIp(TianLongServer.serverIP);
						gpp.setSrvid(TianLongServer.srvId);
						gpp.setPayPort(ConnectTaskManager.getInstance().getPAYPORT());
						gpp.setP_Md(ChargeInfoBody.INSTANCE.getP_MD());
						gpp.setP_Pf(ChargeInfoBody.INSTANCE.getP_PF());
						gpp.setP_Rhv(ChargeInfoBody.INSTANCE.getP_RHV());
						gpp.setP_BNum(ConnectTaskManager.getInstance().getP_BNum());
						ConnectCommonParser.getInstance().postTask(
								player.getYuanBaoOp(), gpp);
						sendMsg(player, MsgID.MsgID_Charge_ChargeInfo_Resp,
								"您的充值请求已成功/现在您可以继续游戏了，系统约在10分钟发送一封提醒邮件，请注意查收！");
				}	
			} else
				replyMessage(player, 1, MsgID.MsgID_Charge_ChargeInfo_Resp,
						"充值信息解析失败！");
			break;
		default:
			Log.error(Log.STDOUT, "processCommand", "unhandled msgid! : "
					+ msg.getHeader().getMsgID());
			break;
		}

	}
}
