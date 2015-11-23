package com.joyveb.tlol.npc;

import com.joyveb.tlol.LuaService;
import com.joyveb.tlol.core.AgentProxy;
import com.joyveb.tlol.net.IncomingMsg;
import com.joyveb.tlol.protocol.MsgID;
import com.joyveb.tlol.protocol.TalkToNpcBody;
import com.joyveb.tlol.role.RoleBean;

public class NPCAgent extends AgentProxy {

	public NPCAgent(final RoleBean player) {
		this.player = player;
	}
	@Override
	public final void processCommand(final IncomingMsg message) {

		switch (MsgID.getInstance(message.getHeader().getMsgID())) {
		case MsgID_Talk_To_Npc:// 客户端请求和某个NPC的对话
			if (TalkToNpcBody.INSTANCE.readBody(message.getBody()))
				LuaService.callLuaFunction("talkToNPC", player,
						TalkToNpcBody.INSTANCE.getNpcid(),
						TalkToNpcBody.INSTANCE.getDeep(),
						TalkToNpcBody.INSTANCE.getTaskid(),
						TalkToNpcBody.INSTANCE.getUid(),
						TalkToNpcBody.INSTANCE.getNum());
			else
				replyMessage(player, 1, MsgID.MsgID_Talk_To_Npc_Resp, "查看失败！");

			break;
		default:
			break;
		}
	}
}
