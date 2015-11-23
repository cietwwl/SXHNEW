package com.joyveb.tlol.protocol;

import java.util.HashMap;

import com.joyveb.tlol.action.ActionAgent;
import com.joyveb.tlol.auction.AuctionAgent;
import com.joyveb.tlol.battle.BattleAgent;
import com.joyveb.tlol.billboard.Billboard;
import com.joyveb.tlol.charge.ChargeAgent;
import com.joyveb.tlol.chat.ChatAgent;
import com.joyveb.tlol.core.AgentProxy;
import com.joyveb.tlol.enemy.EnemyAgent;
import com.joyveb.tlol.fee.FeeAgent;
import com.joyveb.tlol.frenemy.FrenemyAgent;
import com.joyveb.tlol.gang.GangAgent;
import com.joyveb.tlol.heartbeat.HeartBeatAgent;
import com.joyveb.tlol.item.ItemAgent;
import com.joyveb.tlol.mail.MailAgent;
import com.joyveb.tlol.map.MapAgent;
import com.joyveb.tlol.marry.MarryAgent;
import com.joyveb.tlol.npc.NPCAgent;
import com.joyveb.tlol.res.ResourceManager;
import com.joyveb.tlol.role.RoleAgent;
import com.joyveb.tlol.task.TaskAgent;
import com.joyveb.tlol.team.TeamAgent;

public enum MsgCatIDs {
	CAT_ROLE(2, RoleAgent.class),
	CAT_MAP(3, MapAgent.class),
	CAT_RES(4, ResourceManager.class),
	CAT_FIGHT(5, BattleAgent.class),
	CAT_TASK(6, TaskAgent.class),
	CAT_TEAM(7, TeamAgent.class),
	CAT_ITEM(8, ItemAgent.class),
	CAT_NAMELIST(9, FrenemyAgent.class),
	CAT_MESSAGE(10, ChatAgent.class),
	CAT_ORDER(11, null),
	CAT_HEARTBEAT(12, HeartBeatAgent.class),
	CAT_NPC(13, NPCAgent.class),
	CAT_BULLETIN(14, Billboard.class),
	CAT_MAIL(15, MailAgent.class),
	CAT_SHOP(16, null),
	CAT_CHARGE(17, ChargeAgent.class),
	CAT_GANG(18, GangAgent.class),
	CAT_ACTION(19, ActionAgent.class),
	CAT_AUCTION(20, AuctionAgent.class),
	CAT_MARRY(21,MarryAgent.class),
	CAT_ENEMY(22, EnemyAgent.class),
	CAT_FEE(23,FeeAgent.class);
	
	
	private static HashMap<Short, MsgCatIDs> mapping = new HashMap<Short, MsgCatIDs>();
	
	private final short msgid;
	private final Class<? extends AgentProxy> agent;

	private MsgCatIDs(final int msgid, final Class<? extends AgentProxy> agent) {
		this.msgid = (short) msgid;
		this.agent = agent;
	}

	public static MsgCatIDs getInstance(final short msgid) {
		if(mapping.isEmpty())
			for (MsgCatIDs instance : MsgCatIDs.values())
				mapping.put(instance.msgid, instance);
		
		return mapping.get(msgid);
	}

	public short getMsgid() {
		return msgid;
	}

	public Class<? extends AgentProxy> getAgent() {
		return agent;
	}
	
}
