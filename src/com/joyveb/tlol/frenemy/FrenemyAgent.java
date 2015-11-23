package com.joyveb.tlol.frenemy;

import java.util.ArrayList;

import com.joyveb.tlol.LuaService;
import com.joyveb.tlol.core.AgentProxy;
import com.joyveb.tlol.db.DataHandler;
import com.joyveb.tlol.db.DataStruct;
import com.joyveb.tlol.db.PreSql;
import com.joyveb.tlol.db.parser.BatchGetRoleCard;
import com.joyveb.tlol.db.parser.CommonParser;
import com.joyveb.tlol.db.parser.DbConst;
import com.joyveb.tlol.enemy.EnemyAgent;
import com.joyveb.tlol.net.IncomingMsg;
import com.joyveb.tlol.protocol.MsgID;
import com.joyveb.tlol.protocol.NameListAddBody;
import com.joyveb.tlol.protocol.NameListBody;
import com.joyveb.tlol.protocol.NameListDelBody;
import com.joyveb.tlol.role.RoleBean;
import com.joyveb.tlol.role.RoleCard;
import com.joyveb.tlol.role.RoleCardService;
import com.joyveb.tlol.util.Log;

public class FrenemyAgent extends AgentProxy implements DataHandler {

	public FrenemyAgent(final RoleBean player) {
		this.player = player;
	}
	@Override
	public final void processCommand(final IncomingMsg message) {
		switch (MsgID.getInstance(message.getHeader().getMsgID())) {
		case MsgID_NameList_List:
			if (NameListBody.INSTANCE.readBody(message.getBody()))
				listFrenemy();
			else
				replyMessage(player, 1, MsgID.MsgID_NameList_List_Resp, "查看失败！");

			break;
		case MsgID_NameList_Add:
			if (NameListAddBody.INSTANCE.readBody(message.getBody()))
				addFrenemy();
			else
				replyMessage(player, 1, MsgID.MsgID_NameList_Add_Resp, "添加失败！");

			break;
		case MsgID_NameList_Del:
			if (NameListDelBody.INSTANCE.readBody(message.getBody()))
				delFrenemy();
			else
				replyMessage(player, 1, MsgID.MsgID_NameList_Del_Resp, "删除失败！");

			break;
		default:
			break;
		}
	}

	private void addFrenemy() {
		int roleid = NameListAddBody.INSTANCE.getId();
		String nick = NameListAddBody.INSTANCE.getNick();
		boolean friend = NameListAddBody.INSTANCE.isFriend();

		if (roleid == 0 && !RoleCardService.INSTANCE.hasCard(nick)) {
			PreSql preSql = new PreSql();
			preSql.sqlstr = RoleCard.SQL + " where snick = ?";
			preSql.parameter.add(nick);
			CommonParser.getInstance().postTask(DbConst.BatchGetRoleCard, this,
					new BatchGetRoleCard(preSql, nick, friend));
			return;
		}

		if (roleid == 0)
			roleid = RoleCardService.INSTANCE.getCard(
					NameListAddBody.INSTANCE.getNick()).getRoleid();

		addFrenemyHandle(roleid, friend);
	}

	private void addFrenemyHandle(final int id, final boolean isFriend) {
		if (id == 0) {
			replyMessage(player, 3, MsgID.MsgID_NameList_Add_Resp,
					"添加失败！角色不存在！");
			return;
		}

		if (player.getFoes().contains(id)) {
			replyMessage(player, 4, MsgID.MsgID_NameList_Add_Resp,
					"添加失败！角色已经在黑名单中！");
			return;
		}

		if (player.getFrends().contains(id)) {
			replyMessage(player, 5, MsgID.MsgID_NameList_Add_Resp,
					"添加失败！角色已经在好友列表中！");
			return;
		}

		if (player.getEnemys().contains(id)) {
			EnemyAgent ea = new EnemyAgent(player);
			boolean retval = ea.delEnemy(id);
			if(retval == false){
				replyMessage(player, 6, MsgID.MsgID_NameList_EnemyDel_Resp,
						"添加到好友列表失败！");
				return;
			} 
			
		}
		ArrayList<Integer> frenemys = isFriend ? player.getFrends() : player
				.getFoes();

		if (frenemys.size() == 20) {
			replyMessage(player, 7, MsgID.MsgID_NameList_Add_Resp,
					"添加失败！列表中角色已达到上限！");
			return;
		}

		frenemys.add(id);

		replyMessage(player, 0, MsgID.MsgID_NameList_Add_Resp, "添加成功～");
	}

	private void delFrenemy() {
		if (NameListDelBody.INSTANCE.getType() == NameListDelBody.Friend) {
			if (player.getFrends().contains(
					NameListDelBody.INSTANCE.getRoleid()))
				player.getFrends().remove(
						Integer.valueOf(NameListDelBody.INSTANCE.getRoleid()));
			else {
				replyMessage(player, 2, MsgID.MsgID_NameList_Del_Resp,
						"删除失败！用户不在好友列表中！");
				return;
			}
		} else {
			if (player.getFoes().contains(NameListDelBody.INSTANCE.getRoleid()))
				player.getFoes().remove(
						Integer.valueOf(NameListDelBody.INSTANCE.getRoleid()));
			else {
				replyMessage(player, 3, MsgID.MsgID_NameList_Del_Resp,
						"添加失败！用户不在黑名单中！");
				return;
			}
		}

		replyMessage(player, 0, MsgID.MsgID_NameList_Del_Resp, "删除成功～");
	}

	private void listFrenemy() {
		boolean friends = NameListBody.INSTANCE.getType() == 0;
		ArrayList<Integer> list = friends ? player.getFrends() : player
				.getFoes();

		prepareBody();

		placeholder(1);

		int fill = 0;

		/** 先发在线的，后发不在线的 */

		for (int roleid : list) {
			if (!RoleCardService.INSTANCE.hasCard(roleid))
				continue;

			RoleCard card = RoleCardService.INSTANCE.getCard(roleid);

			if (!card.isRoleOnline())
				continue;

			fill++;
			putInt(roleid);
			putString("LV " + card.getLevel() + " " + card.getName());

			putInt(0);
		}

		for (int roleid : list) {
			if (!RoleCardService.INSTANCE.hasCard(roleid))
				continue;

			RoleCard card = RoleCardService.INSTANCE.getCard(roleid);

			if (card.isRoleOnline())
				continue;

			fill++;
			putInt(roleid);
			putString("LV " + card.getLevel() + " " + card.getName());

			putInt(0xAAAAAA);
		}

		fillPlaceholder(1, fill);

		if (fill > 0) {
			if (friends) {
				if (player.getTeam() == null)
					LuaService.callLuaFunction("fillSingleFriendMenu");
				else if (player.equals(player.getTeam().getLeader()))
					LuaService.callLuaFunction("fillSkipperFriendMenu");
				else
					LuaService.callLuaFunction("fillTeamerFriendMenu");
			} else
				LuaService.callLuaFunction("fillFoeNameMenu");
		}

		putShort((short) 0);

		sendMsg(player, MsgID.MsgID_NameList_List_Resp);
	}

	@Override
	public final void handle(final DbConst eventID, final boolean flag, final DataStruct ds) {
		switch (eventID) {
		case BatchGetRoleCard:
			BatchGetRoleCard batchGetRoleCard = (BatchGetRoleCard) ds;
			Object[] sceneInfo = batchGetRoleCard.getSceneInfo();

			if (batchGetRoleCard.hasGot()) {
				RoleCard card = batchGetRoleCard.getCards().get(0);
				addFrenemyHandle(card.getRoleid(), (Boolean) sceneInfo[1]);
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
}
