package com.joyveb.tlol;

import com.joyveb.tlol.battle.BattleJavaFunc;
import com.joyveb.tlol.betAccount.BetAccountJavaFunc;
import com.joyveb.tlol.bigOrSmall.BigOrSmallJavaFunc;
import com.joyveb.tlol.buff.BuffJavaFunc;
import com.joyveb.tlol.core.IGameCharacterJavaFunc;
import com.joyveb.tlol.cycles.CyclesJavaFunc;
import com.joyveb.tlol.enemy.EnemyJavaFunc;
import com.joyveb.tlol.everydayAward.EverydayAwardJavaFunc;
import com.joyveb.tlol.fatwa.FatwaJavaFunc;
import com.joyveb.tlol.gang.GangFightJavaFunc;
import com.joyveb.tlol.gang.GangJavaFunc;
import com.joyveb.tlol.guess.GuessJavaFunc;
import com.joyveb.tlol.item.ItemJavaFunc;
import com.joyveb.tlol.javafunc.LangJavaFunc;
import com.joyveb.tlol.javafunc.MessageSendJavaFunc;
import com.joyveb.tlol.javafunc.OnlineJavaFunc;
import com.joyveb.tlol.javafunc.SubModulesJavaFunc;
import com.joyveb.tlol.javafunc.UtilJavaFunc;
import com.joyveb.tlol.mailnotice.MailNoticeJavaFunc;
import com.joyveb.tlol.map.MapJavaFunc;
import com.joyveb.tlol.protocol.ProtocolJavaFunc;
import com.joyveb.tlol.res.GameResJavaFunc;
import com.joyveb.tlol.role.RoleCardJavaFunc;
import com.joyveb.tlol.role.RoleJavaFunc;
import com.joyveb.tlol.schedule.ScheduleJavaFunc;
import com.joyveb.tlol.store.StoreJavaFunc;
import com.joyveb.tlol.task.TaskJavaFunc;


/**
 * 所有要注册的Java函数的顶层结构
 * @author Sid
 */
public enum JavaFunctions {
	/** 基本函数 */
	Lang(LangJavaFunc.values()),
	
	/** 待注册的util类相关函数*/
	Util(UtilJavaFunc.values()),
	
	/** 待注册的向客户端发送消息函数 */
	MessageSend(MessageSendJavaFunc.values()),
	
	/** 填充消息子模块 */
	SubModules(SubModulesJavaFunc.values()),
	
	/** 角色属性操作注册函数 */
	Role(RoleJavaFunc.values()),
	
	/** 物品操作注册函数 */
	Item(ItemJavaFunc.values()),
	
	/** 任务操作注册函数 */
	Task(TaskJavaFunc.values()),
	
	/** 计划任务注册函数 */
	Schedule(ScheduleJavaFunc.values()),
	
	/** 物品存储相关函数注册 */
	Store(StoreJavaFunc.values()),
	
	/** 在线角色相关函数注册 */
	Online(OnlineJavaFunc.values()),
	
	/** 角色名片相关函数注册 */
	RoleCard(RoleCardJavaFunc.values()),
	
	/** Buff相关函数注册 */
	Buff(BuffJavaFunc.values()),
	
	/** IGameCharacter相关函数注册 */
	IGameCharacter(IGameCharacterJavaFunc.values()),
	
	/** 协议相关函数注册 */
	Protocol(ProtocolJavaFunc.values()),
	
	/** 地图相关函数注册 */
	Map(MapJavaFunc.values()),
	
	/** 帮派相关函数注册 */
	Gang(GangJavaFunc.values()),
	
	/** 帮派战斗相关函数注册 */
	GangFight(GangFightJavaFunc.values()),
	
	/** GameResource相关函数注册 */
	GameRes(GameResJavaFunc.values()),
	
	/** BigOrSmallJavaFunc相关函数注册 */
	BigOrSmall(BigOrSmallJavaFunc.values()),
	
	/** CyclesJavaFunc相关函数注册 */
	Cycles(CyclesJavaFunc.values()),
	
	/** BattleJavaFunc相关函数注册 */
	Battle(BattleJavaFunc.values()),
	
	/** BetAccountJavaFunc相关函数注册 */
	BetAccount(BetAccountJavaFunc.values()),
	
	/** EverydayAwardJavaFunc相关函数注册 */
	EverydayAward(EverydayAwardJavaFunc.values()),
	
	/** GuessJavaFunc相关函数注册 */
	Guess(GuessJavaFunc.values()),
	
	/** NoticeJavaFunc相关函数注册 */
	MailNotice(MailNoticeJavaFunc.values()),
	
	/** FatwaJavaFunc相关函数注册 */
	Fatwa(FatwaJavaFunc.values()),
	
	/** EnemyJavaFunc相关函数注册 */
	Enemy(EnemyJavaFunc.values());
	
	/** 模块内所有要注册的函数 */
	private final TLOLJavaFunction[] tlolJavaFuncs;
	
	/**
	 * @param tlolJavaFuncs 模块内所有要注册的函数
	 */
	private JavaFunctions(final TLOLJavaFunction[] tlolJavaFuncs) {
		this.tlolJavaFuncs = tlolJavaFuncs;
	}
	
	/**
	 * 注册Java函数
	 */
	public static void register() {
		for(JavaFunctions javaFuncs : JavaFunctions.values()) 
			for(TLOLJavaFunction tlolJavaFunc : javaFuncs.tlolJavaFuncs)
				tlolJavaFunc.register();
	}
}
