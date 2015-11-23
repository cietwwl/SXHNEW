package com.joyveb.tlol.billboard;

import java.util.ArrayList;

import com.joyveb.tlol.core.AgentProxy;
import com.joyveb.tlol.net.IncomingMsg;
import com.joyveb.tlol.protocol.MsgID;
import com.joyveb.tlol.role.RoleBean;
import com.joyveb.tlol.role.RoleCard;
import com.joyveb.tlol.util.Log;

public class Billboard extends AgentProxy {

	public Billboard(final RoleBean roleBean) {
		this.player = roleBean;
	}

	/**
	 * @param msg 消息内容
	 */
	public final void processCommand(final IncomingMsg msg) {
		switch(MsgID.getInstance(msg.getHeader().getMsgID())) {
		case MsgID_Top_Get_Info:
			getTopInfo();
			break;
		case MsgID_Top_Get_Detail:
			msg.getBody().getInt(); // body长度
			byte type = msg.getBody().get();
			getTopDetail(type);
			break;
		default:
			Log.error(Log.STDOUT, "processCommand", "unhandled msgid! : " + msg.getHeader().getMsgID());
			break;
		}
	}

	private void getTopInfo() {
		body.clear();
		body.putInt(0);
		body.put((byte) TopRatedService.INSTANCE.getTopRatedSize());

		for(int i = 1; i <= TopRatedService.INSTANCE.getTopRatedSize(); i++) {
			body.put((byte) i);
			putString(TopRatedService.INSTANCE.getTopRated(i).getTopRatedName());
		}

		body.putShort((short) 0);

		sendMsg(player, MsgID.MsgID_Top_Get_Info_Resp);

	}

	private void getTopDetail(final byte type) {
		TopRated<RoleCard> topRated = TopRatedService.INSTANCE.getTopRated(type);
		if(topRated == null) {
			replyMessage(player, 1, MsgID.MsgID_Top_Get_Detail_Resp, "获取失败");
		}else {
			ArrayList<RoleCard> topPlayers = topRated.getTopCards();
			prepareBody();
			putByte(type);
			putString(topRated.getTopRatedName());

			putByte((byte) topPlayers.size());
			for(int i = 0; i < topPlayers.size(); i++) {
				RoleCard topPlayer = topPlayers.get(i);
				putString(topRated.getDescribe(i, topPlayer));
				putInt(topPlayer.getRoleid());
				putString(topPlayer.getName());
			}

			putShort((short) 0);

			sendMsg(player, MsgID.MsgID_Top_Get_Detail_Resp);
		}
	}

}
