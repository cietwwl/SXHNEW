package com.joyveb.tlol.marry;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.joyveb.tlol.LuaService;
import com.joyveb.tlol.core.AgentProxy;
import com.joyveb.tlol.db.DataHandler;
import com.joyveb.tlol.db.DataStruct;
import com.joyveb.tlol.db.parser.BatchGetRoleCard;
import com.joyveb.tlol.db.parser.DbConst;
import com.joyveb.tlol.net.IncomingMsg;
import com.joyveb.tlol.protocol.MarryNameListBody;
import com.joyveb.tlol.protocol.MsgID;
import com.joyveb.tlol.role.RoleBean;
import com.joyveb.tlol.role.RoleCard;
import com.joyveb.tlol.role.RoleCardService;
import com.joyveb.tlol.util.Log;

public class MarryAgent extends AgentProxy implements DataHandler {


	

	public MarryAgent(final RoleBean player) {
		this.player = player;
	}

	@Override
		public final void processCommand(final IncomingMsg message) {
			switch (MsgID.getInstance(message.getHeader().getMsgID())) {
			case MsgID_MarryList_List_view:
				if (MarryNameListBody.INSTANCE.readBody(message.getBody()))
					listMarry();
				else
					replyMessage(player, 1, MsgID.MsgID_MarryList_List_Resp, "查看失败！");
				break;
			default:
				break;
			}
		}

	private void listMarry() {
		
		prepareBody();
		placeholder(1);
		int fill = 0;
		ArrayList<Integer> listMarry = new ArrayList<Integer>();
		listMarry = player.getMarry();
		/** 先发在线的，后发不在线的 */

		for (int roleid : listMarry) {
			if (!RoleCardService.INSTANCE.hasCard(roleid))
				continue;

			RoleCard card = RoleCardService.INSTANCE.getCard(roleid);

			if (!card.isRoleOnline())
				continue;

			fill++;
			putInt(roleid);
			putString("夫妻         "+card.getName()+"  LV " + card.getLevel());
			putInt(0);
			putString(card.getName());
		}

		for (int roleid : listMarry) {
			if (!RoleCardService.INSTANCE.hasCard(roleid))
				continue;

			RoleCard card = RoleCardService.INSTANCE.getCard(roleid);

			if (card.isRoleOnline())
				continue;

			fill++;
			putInt(roleid);
			putString("夫妻         "+card.getName()+"  LV " + card.getLevel());

			putInt(0xAAAAAA);
			putString(card.getName());
		}
		ArrayList<Integer> listMaster = new ArrayList<Integer>();
		listMaster = player.getMaster();
	/** 先发在线的，后发不在线的 */
	if(player.getLevel()<50){	
		for (int roleid : listMaster) {
			if (!RoleCardService.INSTANCE.hasCard(roleid))
				continue;

			RoleCard card = RoleCardService.INSTANCE.getCard(roleid);

			if (!card.isRoleOnline())
				continue;

			fill++;
			putInt(roleid);
			putString("师傅         "+card.getName()+"  LV " + card.getLevel());
			putInt(0);
			putString(card.getName());
		}

		for (int roleid : listMaster) {
			if (!RoleCardService.INSTANCE.hasCard(roleid))
				continue;

			RoleCard card = RoleCardService.INSTANCE.getCard(roleid);

			if (card.isRoleOnline())
				continue;

			fill++;
			putInt(roleid);
			
			putString("师傅         "+card.getName()+"  LV " + card.getLevel());
					
			putInt(0xAAAAAA);
			putString(card.getName());
		}
	}
		ArrayList<Integer> listApprentice = new ArrayList<Integer>();
		listApprentice = player.getApprentice();
		/** 先发在线的，后发不在线的 */
		
		for (int roleid : listApprentice) {
			if (!RoleCardService.INSTANCE.hasCard(roleid))
				continue;

			RoleCard card = RoleCardService.INSTANCE.getCard(roleid);

			if (!card.isRoleOnline())
				continue;

			fill++;
			putInt(roleid);
			putString("徒弟         "+card.getName()+"  LV " + card.getLevel());
			putInt(0);
			putString(card.getName());
		}

		for (int roleid : listApprentice) {
			if (!RoleCardService.INSTANCE.hasCard(roleid))
				continue;

			RoleCard card = RoleCardService.INSTANCE.getCard(roleid);

			if (card.isRoleOnline())
				continue;

			fill++;
			putInt(roleid);
			putString("徒弟         "+card.getName()+"  LV " + card.getLevel());

			putInt(0xAAAAAA);
			putString(card.getName());
		}
		fillPlaceholder(1, fill);
		
		if (fill > 0) {
			LuaService.callLuaFunction("fillMarryandMaterMenu");
		}
		
		putShort((short) 0);

		sendMsg(player, MsgID.MsgID_MarryList_List_Resp);
		}
	

	@Override
	public final void handle(final DbConst eventID, final boolean flag, final DataStruct ds) {
		switch (eventID) {
		case BatchGetRoleCard:
			BatchGetRoleCard batchGetRoleCard = (BatchGetRoleCard) ds;
			Object[] sceneInfo = batchGetRoleCard.getSceneInfo();

			if (batchGetRoleCard.hasGot()) {
				RoleCard card = batchGetRoleCard.getCards().get(0);
			//	addFrenemyHandle(card.getRoleid(), (Boolean) sceneInfo[1]);
			} else
				replyMessage(player, 2, MsgID.MsgID_NameList_Add_Resp, "角色"
						+ sceneInfo[0] + "不存在！");

			break;
		default:
			Log.error(Log.STDOUT, "handle", "unhandled msgid! : "
					+ eventID);
			break;
		}
	}

	public String getApprenticeNameById(int roleId) {
	

		ArrayList<Integer> listApprentice = new ArrayList<Integer>();
		listApprentice = player.getApprentice();
		
		for (int roleid : listApprentice) {
			if (!RoleCardService.INSTANCE.hasCard(roleid))
				continue;
			if(roleId == roleid){
				RoleCard card = RoleCardService.INSTANCE.getCard(roleid);
				return card.getName();
			}
		}
		return "";
	}
	
}
