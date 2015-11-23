package com.joyveb.tlol.enemy;

import java.util.ArrayList;

import com.joyveb.tlol.LuaService;
import com.joyveb.tlol.core.AgentProxy;
import com.joyveb.tlol.db.DataHandler;
import com.joyveb.tlol.db.DataStruct;
import com.joyveb.tlol.db.PreSql;
import com.joyveb.tlol.db.parser.BatchGetRoleCard;
import com.joyveb.tlol.db.parser.CommonParser;
import com.joyveb.tlol.db.parser.DbConst;
import com.joyveb.tlol.net.IncomingMsg;
import com.joyveb.tlol.protocol.EnemyNameListAddBody;
import com.joyveb.tlol.protocol.EnemyNameListBody;
import com.joyveb.tlol.protocol.EnemyNameListDelBody;
import com.joyveb.tlol.protocol.MsgID;
import com.joyveb.tlol.role.RoleBean;
import com.joyveb.tlol.role.RoleCard;
import com.joyveb.tlol.role.RoleCardService;
import com.joyveb.tlol.util.Log;

public class EnemyAgent extends AgentProxy implements DataHandler {

	private EnemyNameListBody enemyNameListBody = EnemyNameListBody.INSTANCE;


	public EnemyAgent(final RoleBean player) {
		this.player = player;
	}
	@Override
	public final void processCommand(final IncomingMsg message) {
		switch (MsgID.getInstance(message.getHeader().getMsgID())) {
		case MsgID_NameList_EnemyList:
			if (EnemyNameListBody.INSTANCE.readBody(message.getBody()))
				listEnemy();
			else
				replyMessage(player, 1, MsgID.MsgID_NameList_EnemyList_Resp, "查看失败！");

			break;
		case MsgID_NameList_EnemyAdd:
			if (EnemyNameListAddBody.INSTANCE.readBody(message.getBody()))
				addEnemy();
			else
				replyMessage(player, 1, MsgID.MsgID_NameList_EnemyAdd_Resp, "添加失败！");

			break;
		case MsgID_NameList_EnemyDel:
			if (EnemyNameListDelBody.INSTANCE.readBody(message.getBody())){
				boolean retval = delEnemy(-1);
				if(retval){
					replyMessage(player, 0, MsgID.MsgID_NameList_EnemyDel_Resp, "删除成功～");
				}
				else{
					replyMessage(player, 1, MsgID.MsgID_NameList_EnemyDel_Resp,
							"删除失败！用户不在仇人列表中！");	
				}
			}
			else
				replyMessage(player, 1, MsgID.MsgID_NameList_EnemyDel_Resp, "删除失败！");

			break;
		default:
			break;
		}
	}

	private void addEnemy() {
		int roleid = EnemyNameListAddBody.INSTANCE.getId();

		if (roleid == 0) {
			PreSql preSql = new PreSql();
			preSql.sqlstr = RoleCard.SQL + " where nid = ?";
			preSql.parameter.add(roleid);
			CommonParser.getInstance().postTask(DbConst.BatchGetRoleCard, this,
					new BatchGetRoleCard(preSql, roleid));
			return;
		}

		addEnemyHandle(roleid);
	}

	private void addEnemyHandle(final int id) {
		if (id == 0) {
			replyMessage(player, 3, MsgID.MsgID_NameList_EnemyAdd_Resp,
					"添加失败！角色不存在！");
			return;
		}

		if (player.getEnemys().contains(id)) {
			replyMessage(player, 4, MsgID.MsgID_NameList_EnemyAdd_Resp,
					"添加失败！角色已经在 仇人列表中！");
			return;
		}
		ArrayList<Integer> enemys =  player.getEnemys();
		if (enemys.size() == 50) {
			replyMessage(player, 6, MsgID.MsgID_NameList_EnemyAdd_Resp,
					"添加失败！列表中角色已达到上限！");
			return;
		}

		enemys.add(id);
		replyMessage(player, 0, MsgID.MsgID_NameList_EnemyAdd_Resp, "添加成功～");
	}

	public boolean delEnemy(int id) {
		if (player.getEnemys().contains(
				EnemyNameListDelBody.INSTANCE.getRoleid())){
			player.getEnemys().remove(
					Integer.valueOf(EnemyNameListDelBody.INSTANCE.getRoleid())); 
			return true;
			}
		else if(id > 0){
			player.getEnemys().remove(
					Integer.valueOf(id)); 
			return true;
		}
		else {
			return false;
		}
		
	}
              

	private void listEnemy() {
		prepareBody();
		ArrayList<Integer> enemyList = player.getEnemys();
		if(enemyList.isEmpty() || enemyNameListBody.getEnemyIndex() >= enemyList.size()) {
			putByte((byte) 0);
		} else {
			if(enemyList.size() > 0) {
				body.put((byte) Math.ceil(enemyList.size()/((double)enemyNameListBody.getEnemyNum())));
				byte enemyNum = (byte)enemyNameListBody.getEnemyNum();
				int startIdx = enemyNameListBody.getEnemyIndex()* enemyNameListBody.getEnemyNum();
				int fill = 0;
				/** 先发在线的，后发不在线的 */
				int totalCount = enemyList.size()>(startIdx + enemyNum)?startIdx + enemyNum:enemyList.size();
				body.put((byte)(totalCount - startIdx));
				for(int i = startIdx; i < totalCount; i++) {
					int roleid = enemyList.get(i);
					
					if (!RoleCardService.INSTANCE.hasCard(roleid))
						continue;

					RoleCard card = RoleCardService.INSTANCE.getCard(roleid);

					if (!card.isRoleOnline())
						continue;
					fill++;
					putString(card.getName());
					putInt(roleid);
					putInt(0);
				}
				for(int i = startIdx; i< totalCount; i++){
					int roleid = enemyList.get(i);
					
					if (!RoleCardService.INSTANCE.hasCard(roleid))
						continue;

					RoleCard card = RoleCardService.INSTANCE.getCard(roleid);

					if (card.isRoleOnline())
						continue;

					fill++;
					putString(card.getName());
					putInt(roleid);
					putInt(0xAAAAAA);
				}
				

				if (fill > 0) {
					
					LuaService.callLuaFunction("fillEnemyNameMenu");
				}

				putShort((short) 0);
			} else {
				body.put((byte) 0);
			}
		}
		sendMsg(player, MsgID.MsgID_NameList_EnemyList_Resp);
	}
	
	@Override
	public final void handle(final DbConst eventID, final boolean flag, final DataStruct ds) {
		switch (eventID) {
		case BatchGetRoleCard:
			BatchGetRoleCard batchGetRoleCard = (BatchGetRoleCard) ds;
			Object[] sceneInfo = batchGetRoleCard.getSceneInfo();

			if (batchGetRoleCard.hasGot()) {
				RoleCard card = batchGetRoleCard.getCards().get(0);
				addEnemyHandle(card.getRoleid());
			} else
				replyMessage(player, 2, MsgID.MsgID_NameList_EnemyAdd_Resp, "角色"
						+ sceneInfo[0] + "不存在！");

			break;
		default:
			Log.error(Log.STDOUT, "handle", "unhandled msgid! : "
					+ eventID);
			break;
		}
	}
}
