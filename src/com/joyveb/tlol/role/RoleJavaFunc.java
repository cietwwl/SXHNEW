package com.joyveb.tlol.role;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.keplerproject.luajava.LuaException;

import com.joyveb.tlol.DefaultJavaFunc;
import com.joyveb.tlol.LuaService;
import com.joyveb.tlol.MessageSend;
import com.joyveb.tlol.OnlineService;
import com.joyveb.tlol.TLOLJavaFunction;
import com.joyveb.tlol.auction.AuctionHouse;
import com.joyveb.tlol.gang.GangService;
import com.joyveb.tlol.map.Coords;
import com.joyveb.tlol.map.GridMapSys;
import com.joyveb.tlol.team.Team;
import com.joyveb.tlol.transfer.TransferManager;

/**
 * 角色属性操作注册函数
 */
public enum RoleJavaFunc implements TLOLJavaFunction {
	/**
	 * Store.getPack()
	 * 
	 * @param 参数1：RoleBean
	 * @param 参数2：int 存储位索引
	 */
	GetPack(new DefaultJavaFunc("_GetPack") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((RoleBean) this.getParam(2).getObject()).getStore().getPack(
					(int) this.getParam(3).getNumber()));
			return 1;
		}
	}),

	
	/**
	 * Store.getPack()
	 * 
	 * @param 参数1：RoleBean
	 * @param 参数2：int 存储位索引
	 */
	RoleGetStore(new DefaultJavaFunc("_RoleGetStore") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((RoleBean) this.getParam(2).getObject()).getStore());
			return 1;
		}
	}),
	
	/**
	 * MessageSend.putString(RoleBean.getNick())
	 * 
	 * @param 参数1：RoleBean
	 */
	PutRoleNick(new DefaultJavaFunc("_PutRoleNick") {
		@Override
		public int execute() throws LuaException {
			MessageSend.putString(((RoleBean) this.getParam(2).getObject()).getNick());
			return 0;
		}
	}),
	/**
	 * 	LuaService.push(RoleBean.getTeam().getMember())
	 * 
	 * @param 参数1：RoleBean
	 */
	GetTeamNum(new DefaultJavaFunc("_GetTeamNum") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((RoleBean) this.getParam(2).getObject()).getTeam().getMember().size());
			return 1;
		}
	}),
	
	GetMarryTeamOtherId(new DefaultJavaFunc("_GetMarryTeamOtherId") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((RoleBean)(this.getParam(2).getObject())).getTeam().getMarryTeamRoleBean().getRoleid());
			return 1;
		}
	}),

	/**
	 * RoleBean.getLevel()
	 * 
	 * @param 参数1：RoleBean
	 */
	GetRoleLevel(new DefaultJavaFunc("_GetRoleLevel") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((RoleBean) this.getParam(2).getObject()).getLevel());
			return 1;
		}
	}),
	
	/**
	 * RoleBean.getSkillLv()
	 * 
	 * @param 参数1：RoleBean
	 */
	RoleGetSkillLevel(new DefaultJavaFunc("_RoleGetSkillLevel") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((RoleBean) this.getParam(2).getObject()).getSkillLv((long)this.getParam(3).getNumber()));
			return 1;
		}
	}),
	
	/**
	 * RoleBean.getOnlineSec()
	 * 
	 * @param 参数1：RoleBean
	 */
	RoleGetOnlineSec(new DefaultJavaFunc("_RoleGetOnlineSec") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((RoleBean) this.getParam(2).getObject()).getOnlineSec());
			return 1;
		}
	}),

	/**
	 * RoleBean.getRegdate()
	 * 
	 * @param 参数1：RoleBean
	 */
	GetRegdate(new DefaultJavaFunc("_GetRegdate") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((RoleBean) this.getParam(2).getObject()).getRegdate());
			return 1;
		}
	}),

	/**
	 * RoleBean.getEXP()
	 * 
	 * @param 参数1：RoleBean
	 */
	RoleGetExp(new DefaultJavaFunc("_RoleGetExp") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((RoleBean) this.getParam(2).getObject()).getEXP());
			return 1;
		}
	}),

	/**
	 * RoleBean.setEXP()
	 * 
	 * @param 参数1：RoleBean
	 * @param 参数2：int
	 */
	RoleSetExp(new DefaultJavaFunc("_RoleSetExp") {
		@Override
		public int execute() throws LuaException {
			((RoleBean) this.getParam(2).getObject()).setEXP((int) this.getParam(3).getNumber());
			return 0;
		}
	}),
	
	/**
	 * RoleBean.setEXP()
	 * 
	 * @param 参数1：RoleBean
	 * @param 参数2：int
	 */
	RoleAddSkill(new DefaultJavaFunc("_RoleAddSkill") {
		@Override
		public int execute() throws LuaException {
			((RoleBean) this.getParam(2).getObject()).addSkill((int) this.getParam(3).getNumber(), (int) this.getParam(4).getNumber());
			return 0;
		}
	}),

	/**
	 * RoleBean.setLevel()
	 * 
	 * @param 参数1：RoleBean
	 * @param 参数2：int
	 */
	SetRoleLevel(new DefaultJavaFunc("_SetRoleLevel") {
		@Override
		public int execute() throws LuaException {
			((RoleBean) this.getParam(2).getObject()).setLevel((short) this.getParam(3).getNumber());
			return 0;
		}
	}),
	/**
	 * 徒弟达到50级自动解除关系，发邮件
	 */
	LevelRmMaster(new DefaultJavaFunc("_LevelRmMaster") {
		@Override
		public int execute() throws LuaException {
			((RoleBean) this.getParam(2).getObject()).levelRmMaster();
			return 0;
		}
	})
	,
	/**
	 * 升级满血满蓝
	 */
	UpLevelAddHPandMp(new DefaultJavaFunc("_upLevelAddHPandMp") {
		@Override
		public int execute() throws LuaException {
			((RoleBean) this.getParam(2).getObject()).upLevelAddHPandMp();
			return 0;
		}
	}),

	
	
	
	/**
	 * RoleBean.getCharm()
	 * 
	 * @param 参数1：RoleBean
	 */
	RoleGetCharm(new DefaultJavaFunc("_RoleGetCharm") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((RoleBean) this.getParam(2).getObject()).getCharm());
			return 1;
		}
	}),
	
	/**
	 * RoleBean.getTeam()
	 * 
	 * @param 参数1：RoleBean
	 */
	RoleGetTeam(new DefaultJavaFunc("_RoleGetTeam") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((RoleBean) this.getParam(2).getObject()).getTeam());
			return 1;
		}
	}),
	
	/**
	 * 得到第三个队友的RoleBean
	 * 
	 * @param 参数1：RoleBean
	 */
	GetMarryTeamThreeRoleBean(new DefaultJavaFunc("_GetMarryTeamThreeRloeBean") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((RoleBean)(this.getParam(2).getObject())).getTeam().getTeamThreeRoleBean());
			return 1;
		}
	}),

	
	RoleGetTeamMemebers(new DefaultJavaFunc("_RoleGetTeamMemebers") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((RoleBean) this.getParam(2).getObject()).getTeam().getMember());
			return 1;
		}
	}),

	/**
	 * RoleBean.setCharm()
	 * 
	 * @param 参数1：RoleBean
	 * @param 参数2：int
	 */
	RoleSetCharm(new DefaultJavaFunc("_RoleSetCharm") {
		@Override
		public int execute() throws LuaException {
			((RoleBean) this.getParam(2).getObject()).setCharm((int) this.getParam(3).getNumber());
			return 0;
		}
	}),

	/**
	 * RoleBean.getMark()
	 * 
	 * @param 参数1：RoleBean
	 */
	RoleGetMark(new DefaultJavaFunc("_RoleGetMark") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((RoleBean) this.getParam(2).getObject()).getMark());
			return 1;
		}
	}),

	/**
	 * RoleBean.setMark()
	 * 
	 * @param 参数1：RoleBean
	 * @param 参数2：int
	 */
	RoleSetMark(new DefaultJavaFunc("_RoleSetMark") {
		@Override
		public int execute() throws LuaException {
			((RoleBean) this.getParam(2).getObject()).setMark((int) this.getParam(3).getNumber());
			return 0;
		}
	}),

	/**
	 * RoleBean.getRoleid()
	 * 
	 * @param 参数1：RoleBean
	 * @param 参数2：int
	 */
	RoleGetId(new DefaultJavaFunc("_RoleGetId") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((RoleBean) this.getParam(2).getObject()).getRoleid());
			return 1;
		}
	}),

	/**
	 * RoleBean.getNick()
	 * 
	 * @param 参数1：RoleBean
	 * @param 参数2：int
	 */
	RoleGetNick(new DefaultJavaFunc("_RoleGetNick") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((RoleBean) this.getParam(2).getObject()).getNick());
			return 1;
		}
	}),
	/**
	 * 玩家转服
	 * 
	 * @param 参数1：RoleBean
	 * @param 参数2：int
	 */
	ZhuanFu_Role(new DefaultJavaFunc("_ZhuanFu_Role") {
		@Override
		public int execute() throws LuaException {
			OnlineService.sendHttpZhuanFu((RoleBean) this.getParam(2).getObject());
			return 0;
		}
	}),
	
	/**
	 * 查看邮件
	 * 
	 * @param 参数1：RoleBean
	 * @param 参数2：int
	 */
	ZhuanFu_Mail(new DefaultJavaFunc("_ZhuanFu_Mail") {
		@Override
		public int execute() throws LuaException {
			if (((RoleBean) this.getParam(2).getObject()).getMailAgent().getMailList() != null) {
				LuaService.push(((RoleBean) this.getParam(2).getObject()).getMailAgent().getMailList().size());
			} else {
				LuaService.push(0);
			}
			return 1;
		}
	}),
	/**
	 * 查看拍卖行
	 * 
	 * @param 参数1：RoleBean
	 * @param 参数2：int
	 */
	ZhuanFu_Auction(new DefaultJavaFunc("_ZhuanFu_Auction") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(AuctionHouse.INSTANCE.ifHaveAuction(((RoleBean) this.getParam(2).getObject())));
			return 1;
		}
	}), /**
	 * 踢人
	 * 
	 * @param 参数2
	 *            ：int
	 */
	KickDown(new DefaultJavaFunc("_KickDown") {
		@Override
		public int execute() throws LuaException {
			((RoleBean) this.getParam(2).getObject()).kickDown();
			return 0;
		}
	}), /**
	 * 查看是否有帮派
	 * 
	 * @param 参数1
	 *            ：RoleBean
	 * @param 参数2
	 *            ：int
	 */
	ZhuanFu_Gang(new DefaultJavaFunc("_ZhuanFu_Gang") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((RoleBean) this.getParam(2).getObject()).getJob());
			return 1;
		}
	}), /**
	 * 转派申请过后，5分钟不允许登陆本服
	 * 
	 * @param 参数1
	 *            ：RoleBean
	 * @param 参数2
	 *            ：int
	 */
	ZhuanFu_DontLogon(new DefaultJavaFunc("_ZhuanFu_DontLogon") {
		@Override
		public int execute() throws LuaException {
			TransferManager.getInstance().add(((RoleBean) this.getParam(2).getObject()).getUserid());
			return 0;
		}
	}), /**
	 * 转派申请去除排行榜
	 * 
	 * @param 参数1
	 *            ：RoleBean
	 * @param 参数2
	 *            ：int
	 */
	ZhuanFu_Qupai(new DefaultJavaFunc("_ZhuanFu_Qupai") {
		@Override
		public int execute() throws LuaException {

			((RoleBean) this.getParam(2).getObject()).removeTop();
			return 0;
		}
	}),

	
	

	

	/**
	 * RoleBean.getUserid()
	 * 
	 * @param 参数1：RoleBean
	 */
	RoleGetUserId(new DefaultJavaFunc("_RoleGetUserId") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((RoleBean) this.getParam(2).getObject()).getUserid());
			return 1;
		}
	}),

	/**
	 * RoleBean.getJoyid()
	 * 
	 * @param 参数1：RoleBean
	 */
	RoleGetJoyId(new DefaultJavaFunc("_RoleGetJoyId") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((RoleBean) this.getParam(2).getObject()).getJoyid());
			return 1;
		}
	}),

	/**
	 * RoleBean.getJoyid()
	 * 
	 * @param 参数1：RoleBean
	 */
	RoleGetYuanBaoOp(new DefaultJavaFunc("_RoleGetYuanBaoOp") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((RoleBean) this.getParam(2).getObject()).getYuanBaoOp());
			return 1;
		}
	}),

	/**
	 * RoleBean.getVocation()
	 * 
	 * @param 参数1：RoleBean
	 */
	RoleGetVocation(new DefaultJavaFunc("_RoleGetVocation") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((RoleBean) this.getParam(2).getObject()).getVocation());
			return 1;
		}
	}),

	/**
	 * RoleBean.setAnimeGroup()
	 * 
	 * @param 参数1：RoleBean
	 * @param 参数2：short 动画组
	 */
	RoleSetAnimeGroup(new DefaultJavaFunc("_RoleSetAnimeGroup") {
		@Override
		public int execute() throws LuaException {
			((RoleBean) this.getParam(2).getObject()).setAnimeGroup((short) this.getParam(3).getNumber());
			return 0;
		}
	}),

	/**
	 * RoleBean.getAnimeGroup()
	 * 
	 * @param 参数1：RoleBean
	 */
	RoleGetAnimeGroup(new DefaultJavaFunc("_RoleGetAnimeGroup") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((RoleBean) this.getParam(2).getObject()).getAnimeGroup());
			return 1;
		}
	}),

	/**
	 * RoleBean.setAnime()
	 * 
	 * @param 参数1：RoleBean
	 * @param 参数2：short 动画组
	 */
	RoleSetAnime(new DefaultJavaFunc("_RoleSetAnime") {
		@Override
		public int execute() throws LuaException {
			((RoleBean) this.getParam(2).getObject()).setAnime((short) this.getParam(3).getNumber());
			return 0;
		}
	}),

	/**
	 * RoleBean.getAnime()
	 * 
	 * @param 参数1：RoleBean
	 */
	RoleGetAnime(new DefaultJavaFunc("_RoleGetAnime") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((RoleBean) this.getParam(2).getObject()).getAnime());
			return 1;
		}
	}),

	/**
	 * RoleBean.getSex()
	 * 
	 * @param 参数1：RoleBean
	 */
	RoleGetSex(new DefaultJavaFunc("_RoleGetSex") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((RoleBean) this.getParam(2).getObject()).getSex());
			return 1;
		}
	}),
	
	/**
	 * RoleBean.getSeatId()
	 * 
	 * @param 参数1：RoleBean
	 */
	RoleGetSeatId(new DefaultJavaFunc("_RoleGetSeatId") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((RoleBean) this.getParam(2).getObject()).getSeatId());
			return 1;
		}
	}),
	
	/**
	 * RoleBean.getNameCatalog()
	 * 
	 * @param 参数1：RoleBean
	 */
	RoleGetNameCatalog(new DefaultJavaFunc("_RoleGetNameCatalog") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((RoleBean) this.getParam(2).getObject()).getNameCatalog());
			return 1;
		}
	}),
	
	/**
	 * RoleBean.setResetKillPlayerNumLeftTime()
	 * 
	 * @param 参数1：RoleBean
	 */
	RoleSetResetKillPlayerNumLeftTime(new DefaultJavaFunc("_RoleSetResetKillPlayerNumLeftTime") {
		@Override
		public int execute() throws LuaException {
			((RoleBean) this.getParam(2).getObject()).setResetKillPlayerNumLeftTime((long)this.getParam(3).getNumber());
			return 1;
		}
	}),
	
	/**
	 * RoleBean.setKillPlayerNum()
	 * 
	 * @param 参数1：RoleBean
	 */
	RoleSetKillPlayerNum(new DefaultJavaFunc("_RoleSetKillPlayerNum") {
		@Override
		public int execute() throws LuaException {
			((RoleBean) this.getParam(2).getObject()).setKillPlayerNum((short)this.getParam(3).getNumber());
			return 1;
		}
	}),

	/**
	 * RoleBean.getStrength()
	 * 
	 * @param 参数1：RoleBean
	 */
	RoleGetStrength(new DefaultJavaFunc("_RoleGetBasicStrength") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((RoleBean) this.getParam(2).getObject()).getBasicStrength());
			return 1;
		}
	}),

	/**
	 * RoleBean.getAgility()
	 * 
	 * @param 参数1：RoleBean
	 */
	RoleGetAgility(new DefaultJavaFunc("_RoleGetBasicAgility") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((RoleBean) this.getParam(2).getObject()).getBasicAgility());
			return 1;
		}
	}),

	/**
	 * RoleBean.getIntellect()
	 * 
	 * @param 参数1：RoleBean
	 * @param 参数2：int
	 */
	RoleGetIntellect(new DefaultJavaFunc("_RoleGetBasicIntellect") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((RoleBean) this.getParam(2).getObject()).getBasicIntellect());
			return 1;
		}
	}),

	/**
	 * RoleBean.getVitality()
	 * 
	 * @param 参数1：RoleBean
	 */
	RoleGetVitality(new DefaultJavaFunc("_RoleGetBasicVitality") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((RoleBean) this.getParam(2).getObject()).getBasicVitality());
			return 1;
		}
	}),

	/**
	 * RoleBean.setStrength()
	 * 
	 * @param 参数1：RoleBean
	 * @param 参数2：int
	 */
	RoleSetStrength(new DefaultJavaFunc("_RoleSetStrength") {
		@Override
		public int execute() throws LuaException {
			((RoleBean) this.getParam(2).getObject()).setStrength((int) this.getParam(3).getNumber());
			return 0;
		}
	}),

	/**
	 * RoleBean.setAgility()
	 * 
	 * @param 参数1：RoleBean
	 * @param 参数2：int
	 */
	RoleSetAgility(new DefaultJavaFunc("_RoleSetAgility") {
		@Override
		public int execute() throws LuaException {
			((RoleBean) this.getParam(2).getObject()).setAgility((int) this.getParam(3).getNumber());
			return 0;
		}
	}),

	/**
	 * RoleBean.setIntellect()
	 * 
	 * @param 参数1：RoleBean
	 * @param 参数2：int
	 */
	RoleSetIntellect(new DefaultJavaFunc("_RoleSetIntellect") {
		@Override
		public int execute() throws LuaException {
			((RoleBean) this.getParam(2).getObject()).setIntellect((int) this.getParam(3).getNumber());
			return 0;
		}
	}),

	/**
	 * RoleBean.setVitality()
	 * 
	 * @param 参数1：RoleBean
	 */
	RoleSetVitality(new DefaultJavaFunc("_RoleSetVitality") {
		@Override
		public int execute() throws LuaException {
			((RoleBean) this.getParam(2).getObject()).setVitality((int) this.getParam(3).getNumber());
			return 1;
		}
	}),

	/**
	 * RoleBean.getCoords()
	 * 
	 * @param 参数1：RoleBean
	 * @param 参数2：int
	 */
	RoleGetCoords(new DefaultJavaFunc("_RoleGetCoords") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((RoleBean) this.getParam(2).getObject()).getCoords());
			return 1;
		}
	}),

	/**
	 * RoleBean.getGold()
	 * 
	 * @param 参数1：RoleBean
	 */
	RoleGetGold(new DefaultJavaFunc("_RoleGetGold") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((RoleBean) this.getParam(2).getObject()).getGold());
			return 1;
		}
	}),
	
	/**
	 * RoleBean.getMaxHP()
	 * 
	 * @param 参数1：RoleBean
	 */
	RoleGetMaxHP(new DefaultJavaFunc("_RoleGetMaxHP") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((RoleBean) this.getParam(2).getObject()).getMaxHP());
			return 1;
		}
	}),
	
	/**
	 * RoleBean.getMaxMP()
	 * 
	 * @param 参数1：RoleBean
	 */
	RoleGetMaxMP(new DefaultJavaFunc("_RoleGetMaxMP") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((RoleBean) this.getParam(2).getObject()).getMaxMP());
			return 1;
		}
	}),

	/**
	 * RoleBean.setGold()
	 * 
	 * @param 参数1：RoleBean
	 * @param 参数2：int
	 */
	RoleSetGold(new DefaultJavaFunc("_RoleSetGold") {
		@Override
		public int execute() throws LuaException {
			((RoleBean) this.getParam(2).getObject()).setGold((int) this.getParam(3).getNumber());
			return 0;
		}
	}),
	
	/**
	 * RoleBean.setGold()
	 * 
	 * @param 参数1：RoleBean
	 * @param 参数2：int
	 */
	RoleSetEpithet(new DefaultJavaFunc("_RoleSetEpithet") {
		@Override
		public int execute() throws LuaException {
			((RoleBean) this.getParam(2).getObject()).setEpithet(this.getParam(3).getString());
			return 0;
		}
	}),
	
	/**
	 * RoleBean.setHP()
	 * 
	 * @param 参数1：RoleBean
	 * @param 参数2：int
	 */
	RoleSetHP(new DefaultJavaFunc("_RoleSetHP") {
		@Override
		public int execute() throws LuaException {
			((RoleBean) this.getParam(2).getObject()).setHP((int) this.getParam(3).getNumber());
			return 0;
		}
	}),
	
	/**
	 * RoleBean.getHP()
	 * 
	 * @param 参数1：RoleBean
	 */
	RoleGetHP(new DefaultJavaFunc("_RoleGetHP") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((RoleBean) this.getParam(2).getObject()).getHP());
			return 1;
		}
	}),
	
	/**
	 * RoleBean.setMP()
	 * 
	 * @param 参数1：RoleBean
	 * @param 参数2：int
	 */
	RoleSetMP(new DefaultJavaFunc("_RoleSetMP") {
		@Override
		public int execute() throws LuaException {
			((RoleBean) this.getParam(2).getObject()).setMP((int) this.getParam(3).getNumber());
			return 0;
		}
	}),
	
	/**
	 * RoleBean.setCommunity()
	 * 
	 * @param 参数1：RoleBean
	 * @param 参数2：int
	 */
	RoleSetCommunity(new DefaultJavaFunc("_RoleSetCommunity") {
		@Override
		public int execute() throws LuaException {
			((RoleBean) this.getParam(2).getObject()).setCommunity((long)this.getParam(3).getNumber());
			return 0;
		}
	}),
	
	/**
	 * RoleBean.getMP()
	 * 
	 * @param 参数1：RoleBean
	 */
	RoleGetMP(new DefaultJavaFunc("_RoleGetMP") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((RoleBean) this.getParam(2).getObject()).getMP());
			return 1;
		}
	}),

	/**
	 * RoleBean.getGangid()
	 * 
	 * @param 参数1：RoleBean
	 */
	RoleGetGangid(new DefaultJavaFunc("_RoleGetGangid") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((RoleBean) this.getParam(2).getObject()).getGangid());
			return 1;
		}
	}),

	/**
	 * GangService.INSTANCE.getGang(RoleBean.getGangid()).getLevel())
	 * 
	 * @param 参数1：RoleBean
	 */
	RoleGetGangLevel(new DefaultJavaFunc("_RoleGetGangLevel") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(GangService.INSTANCE.getGang(((RoleBean) this.getParam(2).getObject()).getGangid())
					.getLevel());
			return 1;
		}
	}),

	/**
	 * RoleBean.color
	 * 
	 * @param 参数1：RoleBean
	 */
	RoleGetColor(new DefaultJavaFunc("_RoleGetColor") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((RoleBean) this.getParam(2).getObject()).color);
			return 1;
		}
	}),
	
	/**
	 * RoleBean.color
	 * 
	 * @param 参数1：RoleBean
	 */
	RoleGetCommunity(new DefaultJavaFunc("_RoleGetCommunity") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((RoleBean) this.getParam(2).getObject()).getCommunity());
			return 1;
		}
	}),
	
	/**
	 * RoleBean.CanChangeName
	 * 
	 * @param 参数1：RoleBean
	 */
	RoleGetCanChangeName(new DefaultJavaFunc("_RoleGetCanChangeName") {
		@Override
		public final int execute() throws LuaException {
			LuaService.push(((RoleBean) this.getParam(2).getObject()).getCanChangeName());
			return 1;
		}
	}),
	
	/**
	 * RoleBean.buffType
	 * 
	 * @param 参数1：RoleBean
	 */
	RoleHasBuff(new DefaultJavaFunc("_RoleHasBuff") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((RoleBean) this.getParam(2).getObject()).hasBuff((byte) this.getParam(3).getNumber()));
			return 1;
		}
	}),
	
	/**
	 * RoleBean.fixValueAfterBuff
	 * 
	 * @param 参数1：RoleBean
	 */
	RoleFixValueAfterBuff(new DefaultJavaFunc("_RoleFixValueAfterBuff") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((RoleBean) this.getParam(2).getObject())
					.fixValueAfterBuff((byte)this.getParam(3).getNumber(), (int)this.getParam(4).getNumber()));
			return 1;
		}
	}),
	
	
	/**
	 * RoleBean.buffType
	 * 
	 * @param 参数1：RoleBean
	 */
	RoleGetBuff(new DefaultJavaFunc("_RoleGetBuff") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((RoleBean) this.getParam(2).getObject()).getBuff((byte) this.getParam(3).getNumber()));
			return 1;
		}
	}),
	/**
	 * RoleBean.buffType
	 * 
	 * @param 参数1：RoleBean
	 */
	RoleGetBuffLevel(new DefaultJavaFunc("_RoleGetBuffLevel") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((RoleBean) this.getParam(2).getObject()).getBuff((byte) this.getParam(3).getNumber())
					.getBuffLevel());
			return 1;
		}
	}),
	/**
	 * RoleBean.money
	 * 
	 * @param 参数1：RoleBean
	 */
	RoleGetMoney(new DefaultJavaFunc("_RoleGetMoney") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((RoleBean) this.getParam(2).getObject()).getMoney());
			return 1;
		}
	}),
	
	/**
	 * RoleBean.money
	 * 
	 * @param 参数1：RoleBean
	 */
	RoleGetSkill(new DefaultJavaFunc("_RoleGetSkill") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((RoleBean) this.getParam(2).getObject()).getSkill());
			return 1;
		}
	}),
	
	/**
	 * RoleBean.money
	 * 
	 * @param 参数1：RoleBean
	 */
	RoleSetMoney(new DefaultJavaFunc("_RoleSetMoney") {
		@Override
		public int execute() throws LuaException {
			((RoleBean) this.getParam(2).getObject()).setMoney((int) this.getParam(3).getNumber());
			return 1;
		}
	}),
	
	GetChallengeRecord(new DefaultJavaFunc("_GetChallengeRecord") {
		@Override
		public final int execute() throws LuaException {
			LuaService.push(((RoleBean) this.getParam(2).getObject()).getChallengeRecord());
			return 1;
		}
	}),
	
	GetBattle(new DefaultJavaFunc("_GetBattle") {
		@Override
		public final int execute() throws LuaException {
			LuaService.push(((RoleBean) this.getParam(2).getObject()).getBattle());
			return 1;
		}
	}),
	
	GetAcceptRecord(new DefaultJavaFunc("_GetAcceptRecord") {
		@Override
		public final int execute() throws LuaException {
			LuaService.push(((RoleBean) this.getParam(2).getObject()).getAcceptRecord());
			return 1;
		}
	}),
	
	/**
	 * 检查最后偷袭时间
	 * RoleBean.getTasks().getMonitored().add(int)
	 * 
	 * @param 参数1：RoleBean
	 * @param 参数2：int
	 */
	TaskCheckBattle(new DefaultJavaFunc("_TaskCheckBattle") {
		@Override
		public int execute() throws LuaException {
			((RoleBean) this.getParam(2).getObject()).checkLastBattleTime();
			return 0;
		}
	}),
	
	/**
	 * 清红名
	 * RoleBean.getTasks().getMonitored().add(int)
	 * 
	 * @param 参数1：RoleBean
	 * @param 参数2：int
	 */
	SetEvil(new DefaultJavaFunc("_SetEvil") {
		@Override
		public int execute() throws LuaException {
			((RoleBean) this.getParam(2).getObject()).lessenEvil(((RoleBean) this.getParam(2).getObject()).getEvil());
			return 0;
		}
	}),
	
	/**
	 * 清红名
	 * RoleBean.getTasks().getMonitored().add(int)
	 * 
	 * @param 参数1：RoleBean
	 * @param 参数2：int
	 */
	ClearEvil(new DefaultJavaFunc("_ClearEvil") {
		@Override
		public int execute() throws LuaException {
			((RoleBean) this.getParam(2).getObject()).lessenEvil((int) this.getParam(3).getNumber());
			return 0;
		}
	}),
	
	/**
	 * 清每日偷袭次数
	 * RoleBean.getTasks().getMonitored().add(int)
	 * 
	 * @param 参数1：RoleBean
	 * @param 参数2：int
	 */
	SetSneakAttackNum(new DefaultJavaFunc("_SetSneakAttackNum") {
		@Override
		public int execute() throws LuaException {
			((RoleBean) this.getParam(2).getObject()).changeSneakAttackNum((int) this.getParam(3).getNumber());
			return 0;
		}
	}),
	
	/**
	 * RoleBean.getHonor()
	 * 
	 * @param 参数1：RoleBean
	 */
	RoleGetHonor(new DefaultJavaFunc("_RoleGetHonor") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((RoleBean) this.getParam(2).getObject()).getHonor());
			return 1;
		}
	}),
	/**
	 * 写入结婚
	 * 
	 * @param 参数1：RoleBean
	 */
	RoleSetMarry(new DefaultJavaFunc("_RoleSetMarry") {
		@Override
		public int execute() throws LuaException {
			((RoleBean) this.getParam(2).getObject()).addMarry((int) this.getParam(3).getNumber());
			return 0;
		}
	}),
	/**
	 * 通过组员ID得到组员RoleBean
	 * 
	 * @param 参数1：RoleBean
	 */
	IdGetRole(new DefaultJavaFunc("_IdGetRole") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((RoleBean) this.getParam(2).getObject()).getTeam().getMarryTeamRoleBean());
			return 1;
		}
	}),
	/**
	 * 得到配偶的ID
	 * 
	 * @param 参数1：RoleBean
	 */
	GetRoleSpouseId(new DefaultJavaFunc("_GetRoleSpouseId") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((RoleBean) this.getParam(2).getObject()).getMarryId());
			return 1;
		}
	}),
	/**
	 * 得到师傅的ID
	 * 
	 * @param 参数1：RoleBean
	 */
	GetMasterId(new DefaultJavaFunc("_GetMasterId") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((RoleBean) this.getParam(2).getObject()).getMasterId());
			return 1;
		}
	}),
	/**
	 * 判断组员是否有结过婚
	 * 
	 * @param 参数1：RoleBean
	 */
	GetIfMarry(new DefaultJavaFunc("_GetIfMarry") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((RoleBean) this.getParam(2).getObject()).getIfMarry());
			return 1;
		}
	}),
	/**
	 * 得到徒弟数量
	 * 
	 * @param 参数1：RoleBean
	 */
	GetApprenticeNum(new DefaultJavaFunc("_GetApprenticeNum") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((RoleBean) this.getParam(2).getObject()).getApprenticeNum());
			return 1;
		}
	}),
	/**
	 * 得到徒弟列表
	 * 
	 * @param 参数1：RoleBean
	 */
	GetApprentice(new DefaultJavaFunc("_GetApprentice") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((RoleBean) this.getParam(2).getObject()).getApprentice());
			return 1;
		}
	}),
	/**
	 * 写入徒弟
	 * 
	 * @param 参数1：RoleBean
	 */
	RoleSetApprentice(new DefaultJavaFunc("_RoleSetApprentice") {
		@Override
		public int execute() throws LuaException {
			((RoleBean) this.getParam(2).getObject()).addApprentice((int) this.getParam(3).getNumber());
			return 0;
		}
	}),
	/**
	 * 写入师傅
	 * 
	 * @param 参数1：RoleBean
	 */
	RoleSetMaster(new DefaultJavaFunc("_RoleSetMaster") {
		@Override
		public int execute() throws LuaException {
			((RoleBean) this.getParam(2).getObject()).addMaster((int) this.getParam(3).getNumber());
			return 0;
		}
	}),
	/**
	 * 检查要收的徒弟（组员）是否有师傅
	 * 
	 * @param 参数1：RoleBean
	 */
	GetIfHaveMaster(new DefaultJavaFunc("_GetIfHaveMaster") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((RoleBean) this.getParam(2).getObject()).getIfMaster());
			return 1;
		}
	}),
	/**
	 * 检查是否有徒弟
	 * 
	 * @param 参数1：RoleBean
	 */
	GetIfHaveApprentice(new DefaultJavaFunc("_GetIfHaveApprentice") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((RoleBean) this.getParam(2).getObject()).getIfApprentice());
			return 1;
		}
	}),
	/**
	 * 检查是否有徒弟
	 * 
	 * @param 参数1：RoleBean
	 */
	GetAppNameById(new DefaultJavaFunc("_GetAppNameById") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((RoleBean) this.getParam(2).getObject()).getAppNameById((int)this.getParam(3).getNumber()));
			return 1;
		}
	}),
	
	/**
	 * 离婚
	 * 
	 * @param 参数1：RoleBean
	 */
	RemoveMarry(new DefaultJavaFunc("_RemoveMarry") {
		@Override
		public int execute() throws LuaException {
			((RoleBean) this.getParam(2).getObject()).removeMarry();
			return 0;
		}
	}),
	/**
	 * 解除师傅
	 * 
	 * @param 参数1：RoleBean
	 */
	RemoveRoleMaster(new DefaultJavaFunc("_RemoveRoleMaster") {
		@Override
		public int execute() throws LuaException {
			((RoleBean) this.getParam(2).getObject()).removeRoleMaster();
			return 0;
		}
	}),
	/**
	 * 解除徒弟
	 * 
	 * @param 参数1：RoleBean
	 */
	RemoveApprentice(new DefaultJavaFunc("_RemoveApprentice") {
		@Override
		public int execute() throws LuaException {
			((RoleBean) this.getParam(2).getObject()).removeApprentice((int) this.getParam(3).getNumber());
			return 0;
		}
	}),
	/**
	 * 强制离婚
	 * 
	 * @param 参数1：RoleBean
	 */
	ForceRemoveRoleMarry(new DefaultJavaFunc("_ForceRemoveRoleMarry") {
		@Override
		public int execute() throws LuaException {
			((RoleBean) this.getParam(2).getObject()).addForceRemoveMarry();
			return 0;
		}
	}),
	/**
	 * 得到传过来的ID是否为自己的徒弟
	 * 
	 * @param 参数1：RoleBean
	 */
	GetIfEqualsApprentice(new DefaultJavaFunc("_GetIfEqualsApprentice") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((RoleBean) this.getParam(2).getObject()).getIfEqualsApprentice((int) this.getParam(3).getNumber()));
			return 1;
		}
	}),
	/**
	 * 得到强制离婚时，查配偶是否在线,在线则删除自己ID，同时返回真假
	 * */
	GetIfOnlineToRemoveSelf(new DefaultJavaFunc("_GetIfOnlineToRemoveSelf") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((RoleBean) this.getParam(2).getObject()).isSpouseOnlineToRemoveMarry());
			return 1;
		}
	}),
	/**
	 * 得到强制师傅时，查师傅是否在线,在线则删除自己ID，同时返回真假
	 * */
	GetIfMasterOnlineToRemoveSelf(new DefaultJavaFunc("_GetIfMasterOnlineToRemoveSelf") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((RoleBean) this.getParam(2).getObject()).isMasterOnlineToRemoveMaster(1));
			return 1;
		}
	}),
	
	/**
	 * 得到强制删除徒弟时，查徒弟是否在线,在线互相删除，同时返回真假
	 * */
	GetIfAppOnlineToRemove(new DefaultJavaFunc("_GetIfAppOnlineToRemove") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((RoleBean) this.getParam(2).getObject()).isAppOnlineToRemoveApp((int)this.getParam(3).getNumber()));
			return 1;
		}
	}),
	
	/**
	 * 强制解除师傅
	 * 
	 * @param 参数1：RoleBean
	 */
	ForceRemoveRoleMaster(new DefaultJavaFunc("_ForceRemoveRoleMaster") {
		@Override
		public int execute() throws LuaException {
			((RoleBean) this.getParam(2).getObject()).addForceRemoveMaster(1);
			return 0;
		}
	}),
	
	/**
	 * 强制解除徒弟
	 * 
	 * @param 参数1：RoleBean
	 */
	ForceRemoveRoleApp(new DefaultJavaFunc("_ForceRemoveRoleApp") {
		@Override
		public int execute() throws LuaException {
			((RoleBean) this.getParam(2).getObject()).addForceRemoveApprentice((int)this.getParam(3).getNumber());
			return 0;
		}
	}),
	
	/**
	 * 使用结婚戒指  判断夫妻在不在线
	 * 
	 * @param 参数1：RoleBean
	 */
	GetIfSpouseOnline(new DefaultJavaFunc("_GetIfSpouseOnline") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((RoleBean) this.getParam(2).getObject()).isSpouseOnline());
			return 1;
		}
	}),
	/**
	 * 使用结婚戒指  取得夫妻RoleBean
	 * 
	 * */
	GetSpouseRole(new DefaultJavaFunc("_GetSpouseRole") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((RoleBean) this.getParam(2).getObject()).GetSpouseRole());
			return 1;
		}
	}),
	/**
	 * 查看系统结婚戒指到时间可以使用否
	 * 
	 * */
	GetRingCanUse(new DefaultJavaFunc("_GetRingCanUse") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((RoleBean) this.getParam(2).getObject()).getIfUseRing((int)this.getParam(3).getNumber()));
			return 1;
		}
	}),
	/**
	 * 查看解除师傅限制时间过了没有。为空返回真，过了返回真，未到时间返回假
	 * 	
	 * */
	GetCanMaster(new DefaultJavaFunc("_GetCanMaster") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((RoleBean) this.getParam(2).getObject()).canMaster());
			return 1;
		}
	}),
	/**
	 * 查看是否24小时内，解除过师傅
	 * 	
	 * */
	GetCanAssessMaster(new DefaultJavaFunc("_GetCanAssessMaster") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((RoleBean) this.getParam(2).getObject()).canAssessMaster());
			return 1;
		}
	}),
	
	
	/**
	 * 查看解除徒弟限制时间过了没有。为空返回真，过了返回真，未到时间返回假
	 * 	
	 * */
	GetCanApprentice(new DefaultJavaFunc("_GetCanApprentice") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((RoleBean) this.getParam(2).getObject()).canApprentice());
			return 1;
		}
	}),
	/**
	 * 查看背包里是否含有些物品，结婚时用在检查是否有此ID物品
	 * 	
	 * */
	CheckBag(new DefaultJavaFunc("_CheckBag") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((RoleBean) this.getParam(2).getObject()).checkBag((int)this.getParam(3).getNumber()));
			return 1;
		}
	}),
	
	/**
	 * 接收背包的物品ID，返回戒指的ID
	 * 	
	 * */
	GetItem(new DefaultJavaFunc("_GetItem") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((RoleBean) this.getParam(2).getObject()).getItemId((int)this.getParam(3).getNumber()));
			return 1;
		}
	}),
	


	
	
	
	
	
	/**
	 * RoleBean.setHonor()
	 * 
	 * @param 参数1：RoleBean
	 */
	RoleSetHonor(new DefaultJavaFunc("_RoleSetHonor") {
		@Override
		public int execute() throws LuaException {
			((RoleBean) this.getParam(2).getObject()).setHonor((int) this.getParam(3).getNumber());
			return 0;
		}
	}),
	
	/**
	 * 进入狮王争霸赛比赛名单
	 */
	RoleGetInHegemony(new DefaultJavaFunc("_RoleGetInHegemony") {
		@Override
		public int execute() throws LuaException {
			com.joyveb.tlol.OnlineService.getInHegemony((RoleBean)this.getParam(2).getObject());
			return 0;
		}
	}),
	
	RoleGetHegemonyAcceptLevel(new DefaultJavaFunc("_RoleGetHegemonyAcceptLevel") {
		@Override
		public int execute() throws LuaException {
			int level =  (int)this.getParam(2).getNumber();
			LuaService.push( (level / 10) % 5 + 1);
			return 1;
		}
	}),
	GetNowHour(new DefaultJavaFunc("_GetNowHour") {
		@Override
		public int execute() throws LuaException {
			Calendar c = Calendar.getInstance();
			c.setTime(new Date());
			LuaService.push(c.get(Calendar.HOUR_OF_DAY));
			return 1;
		}
	}),
	GetNowMin(new DefaultJavaFunc("_GetNowMin") {
		@Override
		public int execute() throws LuaException {
			Calendar c = Calendar.getInstance();
			c.setTime(new Date());
			LuaService.push(c.get(Calendar.MINUTE));
			return 1;
		}
	}),
	
	GetTopHegemony(new DefaultJavaFunc("_GetTopHegemony") {
		@Override
		public int execute() throws LuaException {
			int mapId = (int)this.getParam(2).getNumber();
			int startNum = (int)this.getParam(3).getNumber();
			String info = OnlineService.getTopHegemonys(mapId,startNum);
			LuaService.push(info);
			return 1;
		}
	}),
	
	GetTopHegemonyNum(new DefaultJavaFunc("_GetTopHegemonyNum") {
		@Override
		public int execute() throws LuaException {
			int mapId = (int)this.getParam(2).getNumber();
			int count = OnlineService.getTopHegemonyNum(mapId);
			LuaService.push(count);
			return 1;
		}
	}),
	
	IsTopHegemonyNull(new DefaultJavaFunc("_IsTopHegemonyNull") {
		@Override
		public int execute() throws LuaException {
			String  info = this.getParam(2).getString();
			boolean flag = "争霸榜还未有人获得名次".equals(info);
			LuaService.push(flag);
			return 1;
		}
	}),
	
	
	
	SetHegemonyComplete(new DefaultJavaFunc("_SetHegemonyComplete") {
		@Override
		public int execute() throws LuaException {
			RoleBean role = (RoleBean)this.getParam(2).getObject();
			if(OnlineService.getHegemonys().containsKey(role.getId()))
				OnlineService.getHegemonys().remove(OnlineService.getHegemonys(role.getId()).getSelfId());
			return 0;
		}
	}),
	
	
	RoleGetToken(new DefaultJavaFunc("_RoleGetToken") {
		@Override
		public int execute() throws LuaException {
			RoleBean role = (RoleBean)this.getParam(2).getObject();
			int taskId = (int)this.getParam(3).getNumber();
			LuaService.push(role.getToken(taskId));
			return 1;
		}
	}),
	
	RoleSetToken(new DefaultJavaFunc("_RoleSetToken") {
		@Override
		public int execute() throws LuaException {
			RoleBean role = (RoleBean)this.getParam(2).getObject();
			int taskId = (int)this.getParam(3).getNumber();
			int token = (int) this.getParam(4).getNumber();
			role.putToken(taskId,token);
			return 0;
		}
	}),
	
	GetNowTimeString(new DefaultJavaFunc("_GetNowTimeString") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss")
					.format(new Date()));
			return 1;
		}
	}),
	
	ForceChangeMap(new DefaultJavaFunc("_ForceChangeMap") {
		@Override
		public int execute() throws LuaException {
			RoleBean role = (RoleBean)this.getParam(2).getObject();
			Coords coords = (Coords)this.getParam(3).getObject();
			GridMapSys.INSTANCE.changeGrid(role, coords);
			return 0;
		}
	}),
	
	ForceDeleteTeam(new DefaultJavaFunc("_ForceDeleteTeam") {
		@Override
		public int execute() throws LuaException {
			RoleBean role = (RoleBean)this.getParam(2).getObject();
			Team team = role.getTeam();
			if(team != null) {
				if(team.getLeader() == role) {
					role.getTeamAgent().dissmissTeam();
				} else {
					role.getTeamAgent().requestQuit();
				}
			}
			return 0;
		}
	})
	;

	
	/**
	 * 实现默认的可注册Java函数
	 */
	private final DefaultJavaFunc jf;

	/**
	 * @param jf 可注册Java函数
	 */
	private RoleJavaFunc(final DefaultJavaFunc jf) {
		this.jf = jf;
	}

	@Override
	public void register() {
		jf.register();
	}

}
