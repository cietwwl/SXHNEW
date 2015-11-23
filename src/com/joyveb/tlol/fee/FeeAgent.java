package com.joyveb.tlol.fee;

import java.util.List;

import com.joyveb.tlol.core.AgentProxy;
import com.joyveb.tlol.db.DataHandler;
import com.joyveb.tlol.db.DataStruct;
import com.joyveb.tlol.db.parser.DbConst;
import com.joyveb.tlol.db.parser.FeeData;
import com.joyveb.tlol.net.IncomingMsg;
import com.joyveb.tlol.protocol.FeeListBody;
import com.joyveb.tlol.protocol.MsgID;
import com.joyveb.tlol.role.RoleBean;

public class FeeAgent extends AgentProxy implements DataHandler {

	public FeeAgent(final RoleBean player) {
		this.player = player;
	}

	@Override
	public final void processCommand(final IncomingMsg message) {
		switch (MsgID.getInstance(message.getHeader().getMsgID())) {
		case MsgID_Fee_Info:
			if (FeeListBody.INSTANCE.readBody(message.getBody()))
				queryFee(FeeListBody.INSTANCE.getVer());
			else
				replyMessage(player, 1, MsgID.MsgID_Fee_Info_Resp, "查看失败！");
			break;
		default:
			break;
		}
	}

	private void queryFee(int ver) {
		List<FeeData> fee = FeeService.INSTANCE.getFeeList();
		prepareBody();
		if (ver >= fee.get(0).getVersion()) {
			putShort((short) 0);
		} else {
			putShort((short) 1);
			putShort((short) fee.get(0).getVersion());
			putByte((byte) fee.size());
			
			for (int i = 0; i < fee.size(); i++) {
				putString(fee.get(i).getFeeName());
				putShort(fee.get(i).getFeeKey());
				putByte(fee.get(i).getFeeMoneyNum());
				
				for (int j = 0; j < fee.get(i).getFeeMoneys().split(",").length; j++){
					putShort(Short.parseShort((fee.get(i).getFeeMoneys().split(",")[j])));
				}
				putString(fee.get(i).getFeeTip());
			}

			putString(fee.get(0).getFeeRate());
			putString(fee.get(0).getFeeHelp());

		}
		sendMsg(player, MsgID.MsgID_Fee_Info_Resp);
	}

	@Override
	public void handle(DbConst eventID, boolean flag, DataStruct ds) {
		// TODO Auto-generated method stub

	}

}
