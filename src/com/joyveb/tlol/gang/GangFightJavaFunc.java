package com.joyveb.tlol.gang;

import java.util.ArrayList;
import java.util.List;

import org.keplerproject.luajava.LuaException;

import com.joyveb.tlol.DefaultJavaFunc;
import com.joyveb.tlol.GangFightService;
import com.joyveb.tlol.boss.*;
import com.joyveb.tlol.LuaService;
import com.joyveb.tlol.TLOLJavaFunction;
import com.joyveb.tlol.role.RoleBean;

/**
 * 帮派相关函数注册
 * 
 * @author Sid
 */
public enum GangFightJavaFunc implements TLOLJavaFunction {
	/**
	 * 建立帮战
	 * 
	 * @param 参数1
	 *            ：RoleBean
	 */
	LeaderSetGangFight(new DefaultJavaFunc("_LeaderSetGangFight") {
		@Override
		public int execute() throws LuaException {
			List<GangFight> list = new ArrayList<GangFight>();
			GangFightService.gangFightMap.put((int) this.getParam(2).getNumber(), list);
			return 0;
		}
	}),

	/**
	 * 取得帮战
	 * 
	 * @param 参数1
	 *            ：RoleBean
	 */
	GetGangFight(new DefaultJavaFunc("_GetGangFight") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(GangFightService.gangFightMap.keySet());
			return 1;
		}
	}),

	/**
	 * 取得帮主们的名字
	 * 
	 * @param 参数1
	 *            ：RoleBean
	 */
	GetGangFightName(new DefaultJavaFunc("_GetGangFightName") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(GangFightService.getGangFightLeaderName((int) this.getParam(2).getNumber()));
			return 1;
		}
	}),
	/**
	 * 存玩家属性
	 * 
	 * @param 参数1
	 *            ：roleid
	 */
	PutProperty(new DefaultJavaFunc("_PutProperty") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(GangFightService.putGangProperty((int) this.getParam(2).getNumber(), (int) this.getParam(3).getNumber()));
			return 1;
		}
	})

	,

	/**
	 * 是否有帮
	 * 
	 * @param 参数1
	 *            ：RoleBean
	 */
	GetIfGang(new DefaultJavaFunc("_GetIfGang") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(GangFightService.ifHasGang((RoleBean) this.getParam(2).getObject()));
			return 1;
		}
	}),

	/**
	 * 帮的数目
	 * 
	 * @param 参数1
	 *            ：RoleBean
	 */
	GetGangCount(new DefaultJavaFunc("_GetGangCount") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(GangFightService.gangFightMap.size());
			return 1;
		}
	}),

	/**
	 * 判断是否属于这个帮
	 * 
	 * @param 参数1
	 *            ：RoleBean
	 */
	IfIsInTheGang(new DefaultJavaFunc("_IfIsInTheGang") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(GangFightService.ifIsInTheGang((RoleBean) this.getParam(2).getObject(), (int) this.getParam(3).getNumber()));
			return 1;
		}
	}),

	/**
	 * 取得帮主id
	 * 
	 * @param 参数1
	 *            ：RoleBean
	 */
	GetLeader(new DefaultJavaFunc("_GetLeader") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(GangFightService.getGangLeader((RoleBean) this.getParam(2).getObject()));
			return 1;
		}
	}),

	/**
	 * 取得帮派等级
	 * 
	 * @param 参数1
	 *            ：RoleBean
	 */
	GetGangLevel(new DefaultJavaFunc("_GetGangLevel") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(GangFightService.getGangLevel((RoleBean) this.getParam(2).getObject()));
			return 1;
		}
	}),

	/**
	 * 是否报过名了
	 * 
	 * @param 参数1
	 *            ：RoleBean
	 */
	GetIfHas(new DefaultJavaFunc("_GetIfHas") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(GangFightService.getIfHas((RoleBean) this.getParam(2).getObject()));
			return 1;
		}
	}),
	/**
	 * 参加击杀世界BOSS
	 * 
	 * @param 参数1
	 *            ：RoleBean
	 */
	JoinBoss(new DefaultJavaFunc("_JoinBoss") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(BossService.join((RoleBean) this.getParam(2).getObject()));
			return 1;
		}
	}),
	/**
	 * 花费元宝参加世界BOSS战斗
	 * 
	 * @param 参数1
	 *            ：RoleBean
	 */
	BuyBoss(new DefaultJavaFunc("_BuyBoss") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(BossService.addNoFreezeList((RoleBean) this.getParam(2).getObject()));
			return 1;
		}
	}),
	/**
	 * 判断是否在买过BOSS世界的列表中
	 * 
	 * @param 参数1
	 *            ：RoleBean
	 */
	GetIfInBuyBoss(new DefaultJavaFunc("_GetIfInBuyBoss") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(BossService.getIfNoFreezeList((RoleBean) this.getParam(2).getObject()));
			return 1;
		}
	}),

	/**
	 * 判断帮战名单是否满了
	 * 
	 * @param 参数1
	 *            ：RoleBean
	 */
	ifGangFightEnough(new DefaultJavaFunc("_ifGangFightEnough") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(GangFightService.ifGangFightEnough((int) this.getParam(2).getNumber()));
			return 1;
		}
	});

	/**
	 * 实现默认的可注册Java函数
	 */
	private final DefaultJavaFunc jf;

	/**
	 * @param jf
	 *            可注册Java函数
	 */
	private GangFightJavaFunc(final DefaultJavaFunc jf) {
		this.jf = jf;
	}

	@Override
	public void register() {
		jf.register();
	}

}
