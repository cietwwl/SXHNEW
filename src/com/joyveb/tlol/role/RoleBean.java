package com.joyveb.tlol.role;

import java.awt.Color;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumMap;
import java.util.EventListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.TreeSet;

import com.joyveb.tlol.LuaService;
import com.joyveb.tlol.MessageSend;
import com.joyveb.tlol.OnlineService;
import com.joyveb.tlol.SubModules;
import com.joyveb.tlol.TianLongServer;
import com.joyveb.tlol.action.ActionAgent;
import com.joyveb.tlol.battle.Battle;
import com.joyveb.tlol.battle.BattleAgent;
import com.joyveb.tlol.battle.FightOne;
import com.joyveb.tlol.buff.Buff;
import com.joyveb.tlol.buff.BuffManager;
import com.joyveb.tlol.charge.WapPayAgent;
import com.joyveb.tlol.charge.YuanBaoOp;
import com.joyveb.tlol.community.Community;
import com.joyveb.tlol.community.Communitys;
import com.joyveb.tlol.core.AgentProxy;
import com.joyveb.tlol.core.IGameCharacter;
import com.joyveb.tlol.cycles.CyclesMessage;
import com.joyveb.tlol.db.DataHandler;
import com.joyveb.tlol.db.DataStruct;
import com.joyveb.tlol.db.parser.CommonParser;
import com.joyveb.tlol.db.parser.DbConst;
import com.joyveb.tlol.db.parser.MailListData;
import com.joyveb.tlol.db.parser.MallRecordData;
import com.joyveb.tlol.db.parser.RoleData;
import com.joyveb.tlol.db.parser.SendMailListData;
import com.joyveb.tlol.db.parser.UserInfoData;
import com.joyveb.tlol.enemy.EnemyAgent;
import com.joyveb.tlol.fatwa.Fatwa;
import com.joyveb.tlol.fatwa.FatwaManager;
import com.joyveb.tlol.fatwa.FatwaTable;
import com.joyveb.tlol.gang.GangJobTitle;
import com.joyveb.tlol.heartbeat.HeartBeatAgent;
import com.joyveb.tlol.item.Item;
import com.joyveb.tlol.listener.HeartBeatSender;
import com.joyveb.tlol.listener.RoleDataSaver;
import com.joyveb.tlol.listener.RoleMinListenerMan;
import com.joyveb.tlol.mail.Mail;
import com.joyveb.tlol.mail.MailAgent;
import com.joyveb.tlol.mail.MailManager;
import com.joyveb.tlol.mailnotice.MailNoticeManager;
import com.joyveb.tlol.map.Coords;
import com.joyveb.tlol.map.MapAgent;
import com.joyveb.tlol.marry.ApprenticeManager;
import com.joyveb.tlol.marry.MarryAgent;
import com.joyveb.tlol.marry.MarryManager;
import com.joyveb.tlol.marry.MasterManager;
import com.joyveb.tlol.net.HoldNetHandler;
import com.joyveb.tlol.net.NetHandler;
import com.joyveb.tlol.protocol.MsgCatIDs;
import com.joyveb.tlol.protocol.MsgID;
import com.joyveb.tlol.racing.RacingManager;
import com.joyveb.tlol.role.Ilisteners.LoginListener;
import com.joyveb.tlol.role.events.LoginEvent;
import com.joyveb.tlol.role.listeners.LoginResponder;
import com.joyveb.tlol.schedule.MinTickHandler;
import com.joyveb.tlol.skill.PlayerSkillAgent;
import com.joyveb.tlol.store.Store;
import com.joyveb.tlol.task.Task;
import com.joyveb.tlol.team.Team;
import com.joyveb.tlol.team.TeamAgent;
import com.joyveb.tlol.trigger.RoleEvent;
import com.joyveb.tlol.util.Cardinality;
import com.joyveb.tlol.util.Log;

/** 用户创建的角色属性 */
public final class RoleBean extends Role implements IGameCharacter, DataHandler, HoldNetHandler, MinTickHandler, RoleAccessible {

	public static final byte NAME_WHITE = 0;
	public static final byte NAME_RED = 1;
	public static final byte NAME_BLUE = 2;
	public static final byte NAME_GRAY = 3;
	public long fightTime = 0;

	/**
	 * @return the fightTime
	 */
	public long getFightTime() {
		return fightTime;
	}

	/**
	 * @param fightTime
	 *            the fightTime to set
	 */
	public void setFightTime(long fightTime) {
		this.fightTime = fightTime;
	}

	// PVP保护时间45秒
	public static long PVP_PROTECT_TIME = 45000;

	/** 玩家分类 红名 蓝名 白名 */
	private byte nameCatalog = NAME_WHITE;

	/** 名称颜色 红名 蓝名 白名 灰名 对应的颜色 */
	protected int color = 0x01340f;// 0x1d953f;

	private long killPlayerNumActiveTime = 0;

	/** PVP保护生效时间 单位毫秒 */
	private long pvPProtectTime = 0;

	private boolean isWriteBacking = false;

	/** 技能 */
	protected HashMap<Integer, Integer> skill = new HashMap<Integer, Integer>();

	/** 角色性别 */
	private byte sex;

	/** 角色职业 */
	private Vocation vocation; // 0 侠客 1 术士 2刺客

	/** 登陆时间 */
	private long loginTime;

	/** 用户的连接 */
	private NetHandler netHandler;

	/** 所在地图及坐标 */
	private Coords coords = new Coords();

	/** 用户拥有的物品 */
	private Store store;

	/** 用户任务状态 */
	private Task tasks;

	/** 应战记录vector */
	private TreeSet<CyclesMessage> acceptRecordTreeSet = new TreeSet<CyclesMessage>();
	/** 挑战记录vector */
	private TreeSet<CyclesMessage> challengeRecordTreeSet = new TreeSet<CyclesMessage>();

	private ArrayList<CyclesMessage> cyclesMessageArrayList = new ArrayList<CyclesMessage>();

	private HashSet<EventListener> roleBeanEventListeners = new HashSet<EventListener>();

	/**
	 * 帮派职位
	 */
	private GangJobTitle jobTitle = GangJobTitle.NULL;

	private boolean isVIP = false;
	private byte VIPLevel = 0;
	/**
	 * 人物代币信息
	 * */

	private Map<Integer, Integer> taskToken = new HashMap<Integer, Integer>();
	/** 好友列表 */
	private ArrayList<Integer> friends = new ArrayList<Integer>();
	/** 敌人列表 */
	private ArrayList<Integer> foes = new ArrayList<Integer>();
	/** 仇人列表 */
	private ArrayList<Integer> enemys = new ArrayList<Integer>();
	private ArrayList<Integer> marry = new ArrayList<Integer>();
	private ArrayList<Integer> master = new ArrayList<Integer>();
	private ArrayList<Integer> apprentice = new ArrayList<Integer>();
	private ArrayList<Integer> appdelmaster = new ArrayList<Integer>();
	private final EnumMap<MsgCatIDs, AgentProxy> agents = new EnumMap<MsgCatIDs, AgentProxy>(MsgCatIDs.class);

	private final PlayerSkillAgent skillAgent = new PlayerSkillAgent(this);
	private final WapPayAgent wapPayAgent = new WapPayAgent(this);
	private UnHandledAsk unHandledAsk = null;
	private final BuffManager buffManager = new BuffManager(this);
	private final YuanBaoOp yuanBaoOp = new YuanBaoOp(this);

	private RoleMinListenerMan minLinstenerMan = new RoleMinListenerMan();
	private RoleDataSaver dataSaver = new RoleDataSaver(this);
	// 登录的时间
	private long logOnTime = 0;
	// 心跳的次数
	private int heartNum = 0;

	/** 动态属性管理 */
	private final PropertyMan propertyMan = new PropertyMan(this);

	/** 规定时间48小时 */
	public static final long restrainTime = 2 * 24 * 60 * 60;

	/** 是否被追杀 */
	public boolean isNotFatwa;

	public RoleBean() {
		minLinstenerMan.offer(new HeartBeatSender(this));

		roleBeanEventListeners.add(new LoginResponder());
	}

	public void onOnline(LoginEvent loginEvent) {
		for (EventListener eventListener : roleBeanEventListeners) {
			if (eventListener instanceof LoginListener) {
				((LoginListener) eventListener).onLogin(loginEvent);
			}
		}
	}

	public long getPvPProtectTime() {
		return pvPProtectTime;
	}

	/**
	 * 判断是否无帮派
	 * 
	 * @return 0无帮 1有
	 */
	public int getJob() {
		if (this.getJobTitle() == jobTitle.NULL) {
			return 0;
		}
		return 1;
	}

	public void removeTop() {

		RoleEvent.LevelEvent.removeEvent(this);
		RoleEvent.CharmEvent.removeEvent(this);
		RoleEvent.GoldEvent.removeEvent(this);
		RoleEvent.MarkEvent.removeEvent(this);
		RoleEvent.TotalHonor.removeEvent(this);

	}

	public void setPvPProtectTime(final long pvPProtectTime) {
		this.pvPProtectTime = pvPProtectTime;
	}

	/**
	 * 增加totalKillPlayerNum, killPlayerNum 并且按照规则重置resetKillPlayerNumLeftTime时间
	 * 如果玩家非红名设置为红名
	 * 
	 * @param num
	 *            增加的杀人数
	 * */
	public void increaseKillPlayerNum(final byte num) {
		RoleEvent.TotalKillNumEvent.handleEvent(this);

		setEvil(getEvil() + 100);
		if (evil >= 200) {
			setNameCatalog(NAME_RED);
		}

		if (killPlayerNum == 1) {
			setResetKillPlayerNumLeftTime(10 * 60 * 1000);
		} else if (killPlayerNum < 10) {
			setResetKillPlayerNumLeftTime(60 * 60 * 1000);
		} else if (killPlayerNum >= 10 && killPlayerNum < 20) {
			setResetKillPlayerNumLeftTime(1440 * 60 * 1000);
		} else if (killPlayerNum >= 20 && killPlayerNum < 50) {
			setResetKillPlayerNumLeftTime(4320 * 60 * 1000);
		} else if (killPlayerNum >= 50 && killPlayerNum <= 100) {
			setResetKillPlayerNumLeftTime(10080 * 60 * 1000);
		} else {
			Log.error(Log.ERROR, "发现非正常killPlayerNum: " + killPlayerNum);
		}

	}

	public int getTotalKillPlayerNum() {
		return totalKillPlayerNum;
	}

	public void setTotalKillPlayerNum(final int totalKillPlayerNum) {
		this.totalKillPlayerNum = totalKillPlayerNum;
	}

	public long getResetKillPlayerNumLeftTime() {
		return resetKillPlayerNumLeftTime;
	}

	public void setResetKillPlayerNumLeftTime(final long resetKillPlayerNumLeftTime) {
		this.resetKillPlayerNumLeftTime = resetKillPlayerNumLeftTime;
		if (resetKillPlayerNumLeftTime > 0)
			this.killPlayerNumActiveTime = System.currentTimeMillis();
		else
			this.killPlayerNumActiveTime = 0;
	}

	public byte getNameCatalog() {
		return nameCatalog;
	}

	public void setNameCatalog(final byte nameCatalog) {
		this.nameCatalog = nameCatalog;

		if (nameCatalog == NAME_BLUE) {
			this.color = 0x0000ff;
		} else if (nameCatalog == NAME_RED) {
			this.color = 0xff0000;
		} else if (nameCatalog == NAME_GRAY) {
			this.color = 0x01340f;
		} else {
			this.color = 0x01340f;
		}
	}

	public short getKillPlayerNum() {
		return killPlayerNum;
	}

	public void setKillPlayerNum(final short killPlayerNum) {
		this.killPlayerNum = killPlayerNum;
		if (evil >= 200)
			setNameCatalog(NAME_RED);
		else if (evil < 200)
			setNameCatalog(NAME_WHITE);
	}

	public YuanBaoOp getYuanBaoOp() {
		return yuanBaoOp;
	}

	public boolean isVIP() {
		return isVIP;
	}

	public void setVIP(final boolean isVIP) {
		this.isVIP = isVIP;
	}

	public byte getVIPLevel() {
		return VIPLevel;
	}

	public void setVIPLevel(final byte vIPLevel) {
		VIPLevel = vIPLevel;
	}

	public BuffManager getBuffManager() {
		return buffManager;
	}

	public Team getTeam() {
		return getTeamAgent().getTeam();
	}

	public void setTeam(final Team team) {
		getTeamAgent().setTeam(team);
	}

	public TeamAgent getTeamAgent() {
		return (TeamAgent) this.getAgent(MsgCatIDs.CAT_TEAM);
	}

	public HeartBeatAgent getHeartBeatAgent() {
		return (HeartBeatAgent) this.getAgent(MsgCatIDs.CAT_HEARTBEAT);
	}

	public BattleAgent getBattleAgent() {
		return (BattleAgent) this.getAgent(MsgCatIDs.CAT_FIGHT);
	}

	public MarryAgent getMarryAgent() {

		return (MarryAgent) this.getAgent(MsgCatIDs.CAT_MARRY);
	}

	/** @return 剩余未加点数 */
	public int getLeftPoint() {
		int leftPoint = level * 7 - 3 - strength - agility - intellect - vitality;
		return leftPoint > 0 ? leftPoint : 0;
	}

	public void setZoneid(final short zoneid) {
		this.zoneid = zoneid;
	}

	public short getZoneid() {
		return zoneid;
	}

	public int getRoleid() {
		return roleid;
	}

	public String getJoyid() {
		return joyid;
	}

	public void setJoyid(final String joyid) {
		this.joyid = joyid;
	}

	public boolean isWriteBacking() {
		return isWriteBacking;
	}

	public void setWriteBacking(final boolean isWriteBacking) {
		this.isWriteBacking = isWriteBacking;
	}

	public void setRoleid(final int roleid) {
		this.roleid = roleid;
	}

	public void setUserid(final int userid) {
		this.userId = userid;
	}

	public int getUserid() {
		return userId;
	}

	public void setSex(final byte sex) {
		this.sex = sex;
	}

	public byte getSex() {
		return sex;
	}

	@Override
	public void setNick(final String nick) {
		this.name = nick;
	}

	@Override
	public String getNick() {
		return name;
	}

	@Override
	public void setAnimeGroup(final short animeGroup) {
		this.animeGroup = animeGroup;
	}

	@Override
	public short getAnimeGroup() {
		return animeGroup;
	}

	@Override
	public void setAnime(final short anime) {
		this.anime = anime;
	}

	@Override
	public short getAnime() {
		return anime;
	}

	public void setLoginTime(final long loginTime) {
		this.loginTime = loginTime;
	}

	public long getLoginTime() {
		return loginTime;
	}

	@Override
	public NetHandler getNetHandler() {
		return netHandler;
	}

	public void setNetHandler(final NetHandler netHandler) {
		if (this.netHandler != null)
			this.netHandler.close(NetHandler.STATE_CLOSED_BY_APP_LEVEL);
		this.netHandler = netHandler;
	}

	public void setNetHandler(final NetHandler netHandler, Boolean needClose) {
		if (this.netHandler != null && needClose)
			this.netHandler.close(NetHandler.STATE_CLOSED_BY_APP_LEVEL);
		this.netHandler = netHandler;
	}

	public void setHP(final int red) {
		int max = (int) propertyMan.getDynamicProperty(Property.MaxHp);
		if (red > max)
			this.HP = max;
		else if (red < 0)
			this.HP = 0;
		else
			this.HP = red;
	}

	public void changeHP(final double d) {
		setHP((int) (this.HP + d));
	}

	public int getHP() {
		return HP;
	}

	public void setMP(final int blue) {
		int max = (int) propertyMan.getDynamicProperty(Property.MaxMp);
		if (blue > max)
			this.MP = max;
		else if (blue < 0)
			this.MP = 0;
		else
			this.MP = blue;
	}

	public void changeMP(final double blue) {
		setMP((int) (this.MP + blue));
	}

	@Override
	public int getMP() {
		return MP;
	}

	public void setEXP(final int eXP) {
		if (eXP > 0)
			EXP = eXP;
		else
			EXP = 0;

		if (OnlineService.getOnline(this.roleid) != null)
			RoleEvent.ExpEvent.handleEvent(this);
	}

	public int getEXP() {
		return EXP;
	}

	public int getMaxEXP() {
		return LuaService.call4Int("getUpgradeRequire", this.level);
	}

	/**
	 * @param onlineSec
	 *            在线时长，单位：秒
	 */
	public void setOnlineSec(final int onlineSec) {
		this.onlineSec = onlineSec;
	}

	/**
	 * 在线时长，单位：秒
	 * 
	 * @return 在线时长
	 */
	public int getOnlineSec() {
		return onlineSec;
	}

	/**
	 * @param gold
	 *            金币，打怪掉落、出售物品等渠道获得
	 */
	public void setGold(final int gold) {
		if (gold < 0) {
			Log.error(Log.ERROR, "setGold", new Exception("设置金币小于0"));
			this.gold = 0;
		} else if (gold > 99999999)
			this.gold = 99999999;
		else
			this.gold = gold;

		if (OnlineService.getOnline(this.roleid) != null)
			RoleEvent.GoldEvent.handleEvent(this);
	}

	public void addMoney(final int money) {
		this.money += money;
	}

	public void decreaseGold(final int gold) {
		if (this.gold < gold)
			this.gold = 0;
		else
			this.gold -= gold;

		if (OnlineService.getOnline(this.roleid) != null)
			RoleEvent.GoldEvent.handleEvent(this);
	}

	/**
	 * @return 金币，打怪掉落、出售物品等渠道获得
	 */
	public int getGold() {
		return gold;
	}

	/**
	 * @param mark
	 *            武林大会总积分
	 */
	public void setMark(final int mark) {
		if (mark >= 99999999)
			this.mark = 99999999;
		else if (mark <= 0)
			this.mark = 0;
		else
			this.mark = mark;

		if (OnlineService.getOnline(this.roleid) != null)
			RoleEvent.MarkEvent.handleEvent(this);
	}

	/**
	 * @return 武林大会总积分
	 */
	public int getMark() {
		return mark;
	}

	/**
	 * @param money
	 *            游戏币
	 */
	public void setMoney(final int money) {
		this.money = money;
	}

	/**
	 * 游戏币，用人民币购买
	 * 
	 * @return 游戏币
	 */
	public int getMoney() {
		return money;
	}

	@Override
	public boolean decreaseHP(final int var) {
		if (this.HP < var)
			this.HP = 0;
		else
			this.HP -= var;
		return true;
	}

	@Override
	public int getId() {
		return this.roleid;
	}

	@Override
	public short getLevel() {
		return this.level;
	}

	public String getName() {
		return this.name;
	}

	@Override
	public byte getType() {
		return 0;
	}

	public ArrayList<Integer> getMarry() {
		return marry;
	}

	public void setMarry(ArrayList<Integer> marry) {
		this.marry = marry;
	}

	public ArrayList<Integer> getMaster() {
		return master;
	}

	public void setMaster(ArrayList<Integer> master) {
		this.master = master;
	}

	public ArrayList<Integer> getApprentice() {
		return apprentice;
	}

	public void setApprentice(ArrayList<Integer> apprentice) {
		this.apprentice = apprentice;
	}

	public ArrayList<Integer> getAppdelmaster() {
		return appdelmaster;
	}

	public void setAppdelmaster(ArrayList<Integer> appdelmaster) {
		this.appdelmaster = appdelmaster;
	}

	public void sulp(final int gold) {
		if (gold < 0)
			return;
		else if (this.gold + gold > 99999999)
			this.gold = 0;
		else
			this.gold += gold;
	}

	@Override
	public void increaseExp(final int exp) {
		this.EXP += exp;

		if (OnlineService.getOnline(this.roleid) != null)
			RoleEvent.ExpEvent.handleEvent(this);
	}

	public void decreaseExp(final int exp) {
		if (this.EXP > exp)
			this.EXP -= exp;
		else
			this.EXP = 0;
		if (OnlineService.getOnline(this.roleid) != null)
			RoleEvent.ExpEvent.handleEvent(this);
	}

	@Override
	public void increaseGold(final int gold) {
		if (gold < 0)
			return;
		else if (this.gold + gold > 99999999)
			this.gold = 99999999;
		else
			this.gold += gold;

		if (OnlineService.getOnline(this.roleid) != null)
			RoleEvent.GoldEvent.handleEvent(this);
	}

	public void upLevelAddHPandMp() {
		increaseHP(999999);
		increaseMP(999999);
	}

	@Override
	public boolean increaseHP(final int var) {
		int maxHp = this.getMaxHP();
		if (var > 0)
			HP = HP + var > maxHp ? maxHp : HP + var;
		return true;
	}

	@Override
	public boolean increaseMP(final int var) {
		int maxMp = this.getMaxMP();
		if (var > 0)
			MP = MP + var > maxMp ? maxMp : MP + var;
		return true;
	}

	@Override
	public List<FightOne> physicalAttack(final List<IGameCharacter> targetList) {
		ArrayList<FightOne> fightList = new ArrayList<FightOne>();

		LuaService.callLuaFunction("phyAtk", this, targetList, fightList);

		return fightList;
	}

	@Override
	public void relive(final byte type) {
		if (type == Battle.BATTLE_PVE || type == Battle.BATTLE_PVP) {
			HP = this.getMaxHP() * 15 / 100;
			MP = this.getMaxMP() * 15 / 100;
		} else if (type == Battle.BATTLE_RELIVE_FULL) {
			HP = this.getMaxHP();
			MP = this.getMaxMP();
		} else {
			HP = 1;
			MP = 1;
		}
	}

	@Override
	public void setLevel(final short level) {
		if (OnlineService.getOnline(this.roleid) != null)
			RoleEvent.LevelEvent.handleEvent(this, this.level, level);

		this.propertyMan.changeProperty(Property.Level, level - this.level);
		this.level = level;

	}

	public void levelRmMaster() {

		if (this.master.size() > 0) {
			if (OnlineService.getOnline(this.getMasterId()) != null) {
				isMasterOnlineToRemoveMaster(0);
			} else {
				addForceRemoveMaster(0);
			}
			MailManager.getInstance().sendSysMail(this.roleid, "师徒奖励", "你已50级，已与师傅解除师徒关系，请在1小时之内，到师徒NPC处给师傅评价，领取奖励，过时则不能领取奖励", 0, null);
			Log.info(Log.MARRY, "6#$" + this.getRoleid() + "#$" + this.getNick() + "#$" + this.getMasterId() + "#$#$#$0#$" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "#$"
					+ TianLongServer.srvId);
		}
	}

	public void setName(final String name) {
		if (OnlineService.getOnline(this.roleid) != null)
			RoleCardService.INSTANCE.synchronous(this);

		this.name = name;
	}

	public boolean setState(final int state) {
		return false;
	}

	@Override
	public List<FightOne> skillAttack(final long skillId, final List<IGameCharacter> targetList) {
		List<FightOne> fightList = new ArrayList<FightOne>();
		ArrayList<Number> dataList = LuaService.call4List("skillAtk", this, skillId, getSkillLv(skillId), targetList);
		for (int i = 0; i < dataList.size() / 7; i++) {
			FightOne fightOne = new FightOne();

			fightOne.setFightType((dataList.get(i * 7)).byteValue());
			fightOne.setFightSeat((dataList.get(i * 7 + 1)).byteValue());
			fightOne.setFightGroupId((dataList.get(i * 7 + 2)).shortValue());
			fightOne.setFightAnimId((dataList.get(i * 7 + 3)).shortValue());
			fightOne.setFightValueType((dataList.get(i * 7 + 4)).byteValue());
			fightOne.setFightValueMethod((dataList.get(i * 7 + 5)).byteValue());
			fightOne.setFightValue((dataList.get(i * 7 + 6)).intValue());

			switch (fightOne.getFightValueType()) {
			case 0: // hp
				if (fightOne.getFightValueMethod() == 0) // increase
					targetList.get(i).increaseHP(fightOne.getFightValue());
				else
					targetList.get(i).decreaseHP(fightOne.getFightValue());
				break;
			case 1: // mp
				if (fightOne.getFightValueMethod() == 0)
					targetList.get(i).increaseMP(fightOne.getFightValue());
				else
					targetList.get(i).decreaseMP(fightOne.getFightValue());
				break;
			// 策划没需求 以后再加
			// case 2: //def
			// if(fightOne.getFightValueMethod() == 0)
			// targetList.get(i).increaseAbility(index,
			// fightOne.getFightValue());
			// else
			// targetList.get(i).decreaseHP(fightOne.getFightValue());
			// break;
			// case 3: //atk
			// if(fightOne.getFightValueMethod() == 0)
			// targetList.get(i).increaseAbility(index,
			// fightOne.getFightValue());
			// else
			// targetList.get(i).increaseAbility(index,
			// fightOne.getFightValue());
			// break;
			default:
				Log.error(Log.STDOUT, "skillAttack", "unhandled msgid! : " + fightOne.getFightValueType());
				break;
			}

			fightList.add(fightOne);
		}

		// 技能释放完毕 扣掉mp
		// 注 这并没有做释放检查 而是放在了fightChoose阶段去做
		int costMp = LuaService.call4Int("getManaCost", skillId, getSkillLv(skillId));
		this.MP = costMp > this.MP ? 0 : this.MP - costMp;

		return fightList;
	}

	/**
	 * @param value
	 *            力量
	 */
	public void setStrength(final int value) {
		Property.Strength.changeValue(this, value - this.strength);
		this.strength = value;
	}

	/**
	 * @param value
	 *            力量
	 */
	public void updateStrength(final int value) {
		Property.Strength.changeValue(this, value);
		this.strength += value;
	}

	/**
	 * @return 力量
	 */
	public int getStrength() {
		return (int) propertyMan.getDynamicProperty(Property.Strength);
	}

	/**
	 * @return 基础力量
	 */
	public int getBasicStrength() {
		return strength;
	}

	/**
	 * @param value
	 *            敏捷
	 */
	public void setAgility(final int value) {
		Property.Agility.changeValue(this, value - this.agility);
		this.agility = value;
	}

	/**
	 * @param value
	 *            敏捷
	 */
	public void updateAgility(final int value) {
		this.agility += value;
		Property.Agility.changeValue(this, value);
	}

	/**
	 * @return 敏捷
	 */
	public int getAgility() {
		return (int) propertyMan.getDynamicProperty(Property.Agility);
	}

	/**
	 * @return 基础敏捷
	 */
	public int getBasicAgility() {
		return agility;
	}

	/**
	 * @param value
	 *            智力
	 */
	public void setIntellect(final int value) {
		Property.Intellect.changeValue(this, value - this.intellect);
		this.intellect = value;
	}

	/**
	 * @param value
	 *            智力
	 */
	public void updateIntellect(final int value) {
		this.intellect += value;
		Property.Intellect.changeValue(this, value);
	}

	/**
	 * @return 智力
	 */
	public int getIntellect() {
		return (int) propertyMan.getDynamicProperty(Property.Intellect);
	}

	/**
	 * @return 基础智力
	 */
	public int getBasicIntellect() {
		return intellect;
	}

	/**
	 * @param value
	 *            体力
	 */
	public void setVitality(final int value) {
		Property.Vitality.changeValue(this, value - this.vitality);
		this.vitality = value;
	}

	/**
	 * @param value
	 *            体力
	 */
	public void updateVitality(final int value) {
		this.vitality += value;
		Property.Vitality.changeValue(this, value);
	}

	/**
	 * @return 体力
	 */
	public int getVitality() {
		return (int) propertyMan.getDynamicProperty(Property.Vitality);
	}

	/**
	 * @return 基础体力
	 */
	public int getBasicVitality() {
		return vitality;
	}

	@Override
	public void setBattle(final Battle battle) {
		((BattleAgent) this.getAgent(MsgCatIDs.CAT_FIGHT)).setBattle(battle);
	}

	@Override
	public int getSpeed() {
		return (int) propertyMan.getDynamicProperty(Property.AtkSpd);
	}

	/**
	 * @return 技能存储字符串
	 */
	public String getSkillStr() {
		StringBuilder builder = new StringBuilder();
		Iterator<Entry<Integer, Integer>> it = skill.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Integer, Integer> skillInfo = it.next();
			builder.append(skillInfo.getKey() + "," + skillInfo.getValue() + ";");
		}
		return builder.toString();
	}

	@Override
	public int getColor() {
		return color;
	}

	public void setColor(final int color) {
		this.color = color;
	}

	public void setFrends(final ArrayList<Integer> frends) {
		this.friends = frends;
	}

	public ArrayList<Integer> getFrends() {
		return friends;
	}

	public void setFoes(final ArrayList<Integer> foes) {
		this.foes = foes;
	}

	public ArrayList<Integer> getFoes() {
		return foes;
	}

	public ArrayList<Integer> getEnemys() {
		return enemys;
	}

	public void setEnemys(final ArrayList<Integer> enemys) {
		this.enemys = enemys;
	}

	public boolean ifHasEnemys() {
		if (enemys.size() == 0) {
			return false;
		}
		return true;
	}

	public void addFriend(final int roleid) {
		friends.add(roleid);
	}

	public void addFoe(final int roleid) {
		foes.add(roleid);
	}

	public void addEnemy(final int roleid) {
		enemys.add(roleid);
	}

	public String getFriendsString() {
		StringBuffer sb = new StringBuffer();
		for (int f : friends)
			sb.append(f + " ");
		return sb.toString();
	}

	public String getFoesString() {
		StringBuffer sb = new StringBuffer();
		for (int f : foes)
			sb.append(f + " ");
		return sb.toString();
	}

	// ------------------------------------夫妻师徒等
	// 开始----------------------------------------------------
	public void addMarry(final int roleid) {
		marry.add(roleid);
	}

	public void addMaster(final int roleid) {
		master.add(roleid);
	}

	public void addApprentice(final int roleid) {
		apprentice.add(roleid);
	}

	public void addAppdelmaster(final int roleid) {
		appdelmaster.add(roleid);
	}

	public void removeMarry() {
		marry.remove(0);
		setMarryringtime(null);
	}

	public void removeRoleMaster() {
		master.remove(0);
	}

	public void removeApprentice(int roleId) {
		for (int i = 0; i < apprentice.size(); i++) {
			if (roleId == apprentice.get(i)) {
				apprentice.remove(i);
			}
		}
	}

	public void removeAppdelmaster(int roleId) {
		for (int i = 0; i < appdelmaster.size(); i++) {
			if (roleId == appdelmaster.get(i)) {
				appdelmaster.remove(i);
			}
		}
	}

	@SuppressWarnings("unused")
	public int getMarryId() {
		int m = 0;
		for (int i : marry) {
			m = marry.get(0);
		}
		return m;
	}

	@SuppressWarnings("unused")
	public int getMasterId() {
		int m = 0;
		for (int i : master) {
			m = master.get(0);
		}
		return m;
	}

	/**
	 * 传入组队的徒弟的ID，对比是否为自己徒弟
	 * */
	public boolean getIfEqualsApprentice(int roleId) {
		for (int i = 0; i < apprentice.size(); i++) {
			if (roleId == apprentice.get(i)) {
				return true;
			}
		}
		return false;
	}

	public String getMarryString() {
		StringBuffer sb = new StringBuffer();
		for (int f : marry)
			sb.append(f + " ");
		return sb.toString();
	}

	public String getMasterString() {
		StringBuffer sb = new StringBuffer();
		for (int f : master)
			sb.append(f + " ");
		return sb.toString();
	}

	public String getApprenticeString() {
		StringBuffer sb = new StringBuffer();
		for (int f : apprentice)
			sb.append(f + " ");
		return sb.toString();
	}

	public String getAppdelmasterString() {
		StringBuffer sb = new StringBuffer();
		for (int f : appdelmaster)
			sb.append(f + " ");
		return sb.toString();
	}

	public int getApprenticeNum() {
		return apprentice.size();
	}

	public boolean getIfMarry() {
		if (marry.size() == 0) {
			return false;
		}
		return true;
	}

	public boolean getIfMaster() {
		if (master.size() == 0) {
			return false;
		}
		return true;
	}

	public boolean getIfApprentice() {
		if (apprentice.size() == 0) {
			return false;
		}
		return true;
	}

	public String getAppNameById(int roleid) {
		MarryAgent marryAgent = new MarryAgent(this);
		String str = marryAgent.getApprenticeNameById(roleid);
		return str;

	}

	/** 强制离婚时，把不在线的配偶加入到列表中，配偶上线则会自动删除自己,并发双方邮件 */
	public void addForceRemoveMarry() {
		MarryManager.getInstance().addMarryList(this.getMarryId());
		MailManager.getInstance().sendSysMail(this.roleid, "离婚成功", "你已与配偶离婚", 0, null);
		MailManager.getInstance().sendSysMail(this.getMarryId(), "离婚成功", "你的配偶已与你离婚", 0, null);
		removeMarry();// 删除配偶
		this.setMarryringtime(null);

	}

	/** 强制离婚时，查配偶是否在线,在线则删除自己ID，并发双方邮件 */
	public boolean isSpouseOnlineToRemoveMarry() {
		if (OnlineService.getOnline(this.getMarryId()) != null) {
			RoleBean spouseRoleBean = OnlineService.getOnline(this.getMarryId());
			MailManager.getInstance().sendSysMail(this.roleid, "离婚成功", "你已与配偶离婚", 0, null);
			MailManager.getInstance().sendSysMail(this.getMarryId(), "离婚成功", "你的配偶已与你离婚", 0, null);
			spouseRoleBean.removeMarry();
			this.removeMarry();
			spouseRoleBean.setMarryringtime(null);
			this.setMarryringtime(null);
			return true;
		}
		return false;
	}

	/** 强制解除师傅时，把不在线的师傅加入到列表中，师傅上线则会自动删除自己,并发双方邮件 */
	public void addForceRemoveMaster(int chushi) {
		Date date = new Date();
		MasterManager.getInstance().addMasterList(this.getMasterId(), this.roleid);
		if (chushi == 0) {
			MailManager.getInstance().sendSysMail(this.getMasterId(), "你的徒弟已出师", "你的徒弟  " + this.getNick() + "  已达到50级，自动出师，请提醒他请给予你评价，评价直接影响到你们的奖励物品哦", 0, null);

		} else {
			MailManager.getInstance().sendSysMail(this.roleid, "解除师傅成功", "你已解除师傅", 0, null);
			MailManager.getInstance().sendSysMail(this.getMasterId(), "你被徒弟解除关系", "你的徒弟 " + this.getNick() + " 已与你解除关系", 0, null);
		}
		this.setRemovemastertime(date);// 添加解除师傅时间
		if (chushi != 0) {
			this.removeRoleMaster();// 删除师傅
		}
	}

	/** 强制解除徒弟时，把不在线的徒弟加入到列表中，徒弟上线则会自动删除自己 */
	public void addForceRemoveApprentice(int appRoleId) {
		Date date = new Date();
		ApprenticeManager.getInstance().addAppList(appRoleId);

		MailManager.getInstance().sendSysMail(this.getRoleid(), "强制解除徒弟成功", "你已成功解除徒弟，将不会得到师傅奖励", 0, null);
		MailManager.getInstance().sendSysMail(appRoleId, "你被师傅逐出师门", "你已被师傅逐出师门，将不会得到徒弟奖励", 0, null);
		this.setRemoveapptime(date);// 添加解除徒弟时间
		this.removeApprentice(appRoleId);// 删除徒弟

	}

	/** 强制解除师傅时，查师傅是否在线,在线则删除自己ID，并发双方邮件 */
	public boolean isMasterOnlineToRemoveMaster(int chushi) {
		if (OnlineService.getOnline(this.getMasterId()) != null) {
			Date date = new Date();
			RoleBean masterRoleBean = OnlineService.getOnline(this.getMasterId());
			if (chushi == 0) {
				MailManager.getInstance().sendSysMail(this.getMasterId(), "你的徒弟已出师", "你的徒弟  " + this.getNick() + "  已达到50级，自动出师，请提醒他请给予你评价，评价直接影响到你们的奖励物品哦", 0, null);

			} else {
				MailManager.getInstance().sendSysMail(this.roleid, "解除师傅成功", "你已解除师傅", 0, null);
				MailManager.getInstance().sendSysMail(this.getMasterId(), "你被徒弟解除关系", "你的徒弟 " + this.getNick() + " 已与你解除关系", 0, null);
			}
			this.setRemovemastertime(date);// 添加解除师傅时间
			masterRoleBean.removeApprentice(this.roleid);// 师傅删除徒弟
			if (chushi != 0) {
				this.removeRoleMaster();// 徒弟删除师傅
			}
			return true;
		}
		return false;
	}

	/** 强制解除徒弟时，查徒弟是否在线,在线则删除自己徒弟，徒弟删除自己，并发双方邮件 */
	public boolean isAppOnlineToRemoveApp(int appRoleId) {
		if (OnlineService.getOnline(appRoleId) != null) {
			Date date = new Date();
			RoleBean appRoleBean = OnlineService.getOnline(appRoleId);

			MailManager.getInstance().sendSysMail(this.getRoleid(), "强制解除徒弟成功", "你已成功解除徒弟，将不会得到师傅奖励", 0, null);
			MailManager.getInstance().sendSysMail(appRoleId, "你被师傅逐出师门", "你已被师傅逐出师门，将不会得到徒弟奖励", 0, null);
			this.setRemoveapptime(date);// 添加解除徒弟时间
			this.removeApprentice(appRoleId);
			appRoleBean.removeRoleMaster();

			return true;
		}
		return false;
	}

	/**
	 * 判断解除师傅时间是否满足拜师条件。
	 * 
	 * */
	public boolean canMaster() {
		if (this.removemastertime == null) {
			return true;
		}
		Date date = new Date();
		if (((date.getTime() - removemastertime.getTime()) / (1000 * 60 * 60)) >= 48) {
			return true;
		}
		return false;
	}

	/**
	 * 判断是否 在1小时内有解除师傅行为（给师傅评价时，做为判断条件之一，
	 * 
	 * 另一个条件是必须有师傅，因为实际，徒弟50级自动解除师傅，是没有删除师傅字段的
	 * 
	 * 设置1小时的意思，
	 * 
	 * 是区分 强制解除了师傅的人,强制解除师傅存有解除师傅时间，但48小时是无法重新拜师的
	 * 
	 * 所以设置了1小时，不存在 1小时之内 有解除师傅时间并且拜了师的人）。
	 * 
	 * */
	public boolean canAssessMaster() {
		if (this.removemastertime == null) {
			return false;
		}
		Date date = new Date();
		if (((date.getTime() - removemastertime.getTime()) / (1000 * 60 * 60)) <= 1) {
			return true;
		}
		return false;
	}

	/**
	 * 判断解除徒弟时间是否满足拜师条件。
	 * 
	 * */
	public boolean canApprentice() {
		if (this.removeapptime == null) {
			return true;
		}
		Date date = new Date();
		if (((date.getTime() - removeapptime.getTime()) / (1000 * 60 * 60)) >= 48) {
			return true;
		}
		return false;
	}

	/** 查看是否能使用结婚戒指 */
	public boolean getIfUseRing(final int timelimit) {
		Date date = new Date();
		if (this.marryringtime == null) {
			this.setMarryringtime(date);
			return true;
		}
		if (((date.getTime() - marryringtime.getTime()) / (1000 * 60 * 60)) >= timelimit) {

			this.setMarryringtime(date);
			return true;
		}
		return false;
	}

	/**
	 * 判断夫妻在不在线，不在返回FALSE
	 * 
	 * *
	 */
	public boolean isSpouseOnline() {
		if (OnlineService.getOnline(this.getMarryId()) != null) {
			return true;
		}
		return false;
	}

	/** 取得配偶的ROLEBEAN */
	public RoleBean GetSpouseRole() {
		RoleBean spouseRoleBean = OnlineService.getOnline(this.getMarryId());
		return spouseRoleBean;
	}

	/** 检查背包里是否有此物品 */
	public boolean checkBag(int itemId) {
		if (store.getBag().checkItem(itemId)) {
			return true;
		}
		return false;
	}

	/** 根据发来的物品ID号，返回相应的戒指礼包 */
	public int getItemId(int itemId) {
		if (itemId == 31118)
			return 5155;
		if (itemId == 31117)
			return 5154;
		if (itemId == 31116)
			return 5153;
		return -1;
	}

	// -----------------------------------夫妻师徒等
	// 结束-------------------------------------

	public String getEnemyString() {
		StringBuffer sb = new StringBuffer();
		for (int f : enemys)
			sb.append(f + " ");
		return sb.toString();
	}

	@Override
	public void setSeatId(final byte seatId) {
		this.getBattleAgent().setSeatId(seatId);
	}

	public void updateRole() {
		long writebackStartTime = System.currentTimeMillis();
		CommonParser.getInstance().postTask(DbConst.ROLE_UPDATE, null, this.toData().getRoleDataStruct());
		dataSaver.resetNextSave();

		Log.performance("writeBackDB", "[" + this.getUserid() + "]" + "[" + this.getRoleid() + "]" + "[" + this.getNick() + "]", writebackStartTime);
	}

	public void mallRecord(final Item item, final int cost) {
		long startTime = System.currentTimeMillis();

		MallRecordData mallRecordData = new MallRecordData(userId, joyid, roleid, cost, item);

		CommonParser.getInstance().postTask(DbConst.ROLE_MALL_RECORD, null, mallRecordData);

		Log.performance("mallRecord", "[" + this.getUserid() + "]" + "[" + this.getRoleid() + "]" + "[" + this.getNick() + "]", startTime);
	}

	@Override
	public boolean isConnected() {
		if (netHandler != null && netHandler.getState() == NetHandler.STATE_NORMAL)
			return true;
		return false;
	}

	public Battle getBattle() {
		return this.getBattleAgent().getBattle();
	}

	@Override
	public int getSkillLv(final long skillId) {
		if (skill.get((int) skillId) != null)
			return skill.get((int) skillId);
		else
			return 0;
	}

	public void addSkill(final int skillId, final int skillLv) {
		skill.put(skillId, skillLv);
	}

	public HashMap<Integer, Integer> getSkill() {
		return skill;
	}

	public void loadSkill(String skillStr) {
		if (skillStr == null || skillStr.equals(""))
			return;

		String[] skills = skillStr.trim().split(";");
		for (int i = 0; i < skills.length; i++) {
			String[] skillFrag = skills[i].split(",");
			skill.put(Integer.parseInt(skillFrag[0]), Integer.parseInt(skillFrag[1]));
		}
	}

	@Override
	public List<FightOne> fightItemUse(final long itemId, final List<IGameCharacter> targetList) {
		List<FightOne> fightList = new ArrayList<FightOne>();
		ArrayList<Number> dataList = LuaService.call4List("fightItemUse", this, itemId, targetList);
		for (int i = 0; i < dataList.size() / 7; i++) {
			FightOne fightOne = new FightOne();

			fightOne.setFightType((dataList.get(i * 7)).byteValue());
			fightOne.setFightSeat((dataList.get(i * 7 + 1)).byteValue());
			fightOne.setFightGroupId((dataList.get(i * 7 + 2)).shortValue());
			fightOne.setFightAnimId((dataList.get(i * 7 + 3)).shortValue());
			fightOne.setFightValueType((dataList.get(i * 7 + 4)).byteValue());
			fightOne.setFightValueMethod((dataList.get(i * 7 + 5)).byteValue());
			fightOne.setFightValue((dataList.get(i * 7 + 6)).intValue());

			switch (fightOne.getFightValueType()) {
			case 0: // hp
				if (fightOne.getFightValueMethod() == 0) // increase
					targetList.get(i).increaseHP(fightOne.getFightValue());
				else
					targetList.get(i).decreaseHP(fightOne.getFightValue());
				break;
			case 1: // mp
				if (fightOne.getFightValueMethod() == 0)
					targetList.get(i).increaseMP(fightOne.getFightValue());
				else
					targetList.get(i).decreaseMP(fightOne.getFightValue());
				break;
			// 策划没需求 以后再加
			// case 2: //def
			// if(fightOne.getFightValueMethod() == 0)
			// targetList.get(i).increaseAbility(index,
			// fightOne.getFightValue());
			// else
			// targetList.get(i).decreaseHP(fightOne.getFightValue());
			// break;
			// case 3: //atk
			// if(fightOne.getFightValueMethod() == 0)
			// targetList.get(i).increaseAbility(index,
			// fightOne.getFightValue());
			// else
			// targetList.get(i).increaseAbility(index,
			// fightOne.getFightValue());
			// break;
			default:
				Log.error(Log.STDOUT, "fightItemUse", "unhandled msgid! : " + fightOne.getFightValueType());
				break;
			}

			// 使用完毕删除物品
			// 已经在fightItemUse脚本中做过

			fightList.add(fightOne);
		}

		return fightList;
	}

	// public List<Skill> getSkills() {
	// return skillAgent.getSkillList();
	// }

	public void setStore(final Store store) {
		this.store = store;
	}

	public Store getStore() {
		return store;
	}

	public void setTasks(final Task tasks) {
		this.tasks = tasks;
	}

	/**
	 * 角色相关任务
	 * 
	 * @return 角色相关任务
	 */
	public Task getTasks() {
		return tasks;
	}

	@Override
	public int getMinPAtk() {
		return (int) this.propertyMan.getDynamicProperty(Property.MinPAtk);
	}

	@Override
	public int getMaxPAtk() {
		return (int) this.propertyMan.getDynamicProperty(Property.MaxPAtk);
	}

	@Override
	public int getMinMAtk() {
		return (int) this.propertyMan.getDynamicProperty(Property.MinMAtk);
	}

	@Override
	public int getMaxMAtk() {
		return (int) this.propertyMan.getDynamicProperty(Property.MaxMAtk);
	}

	@Override
	public int getpDef() {
		return (int) this.propertyMan.getDynamicProperty(Property.PDef);
	}

	@Override
	public int getmDef() {
		return (int) this.propertyMan.getDynamicProperty(Property.MDef);
	}

	@Override
	public int getHit() {
		return (int) this.propertyMan.getDynamicProperty(Property.Hit);
	}

	@Override
	public int getEvade() {
		return (int) this.propertyMan.getDynamicProperty(Property.Evade);
	}

	@Override
	public int getCrit() {
		return (int) this.propertyMan.getDynamicProperty(Property.Crit);
	}

	public int getAtkSpd() {
		return (int) propertyMan.getDynamicProperty(Property.AtkSpd);
	}

	@Override
	public int getMaxHP() {
		return (int) propertyMan.getDynamicProperty(Property.MaxHp);
	}

	@Override
	public int getMaxMP() {
		return (int) propertyMan.getDynamicProperty(Property.MaxMp);
	}

	public ArrayList<Integer> getFriends() {
		return friends;
	}

	public void setFriends(final ArrayList<Integer> friends) {
		this.friends = friends;
	}

	public UnHandledAsk getUnHandledAsk() {
		return unHandledAsk;
	}

	public void addUnHandledAsk(final int playerId, final MsgID cmd) {
		unHandledAsk = new UnHandledAsk(playerId, cmd);
	}

	public void setUnHandledAsk(final UnHandledAsk unHandledAsk) {
		this.unHandledAsk = unHandledAsk;
	}

	/**
	 * 检查是否有停服徒弟离线删除师傅失败的，师傅没有删除徒弟 将师傅中徒弟字段（APPRENTICE）与叛逃徒弟字段（APPDELMASTER）进行对比
	 * 若有相同的，则删除徒弟字段中的相应ID，同时删除叛逃徒弟字段
	 * */
	public void checkAppreticeAndAppdelMater() {
		if (appdelmaster.size() > 0) {

			for (int i = 0; i < appdelmaster.size(); i++) {
				if (apprentice.contains(appdelmaster.get(i))) {
					this.removeApprentice(appdelmaster.get(i));
					this.removeAppdelmaster(appdelmaster.get(i));

				}
			}
		}
	}

	/**
	 * 踢下线
	 */
	public void kickDown() {
		System.out.println("踢下线了");
		this.getNetHandler().close(NetHandler.STATE_CLOSED_BY_APP_LEVEL);
	}

	@Override
	public void handle(final DbConst eventID, final boolean flag, final DataStruct ds) {
		if (flag) {
			switch (eventID) {
			case MAIL_LIST:
				MailListData mailListData = (MailListData) ds;

				MailAgent mailAgent = this.getMailAgent();

				mailAgent.initMailList(mailListData.getMailList());
				// 检查是否发送邮件公告
				MailNoticeManager.getInstance().checkMailSend(this.roleid);
				// 是否有强制离婚协议
				MarryManager.getInstance().checkMarryList(this);
				// 是否有强制解除师傅
				MasterManager.getInstance().checkMasterList(this);
				// 是否有离线删除停机后存为叛逃徒弟的，有则删除
				this.checkAppreticeAndAppdelMater();
				// 是否有强制解除徒弟
				ApprenticeManager.getInstance().checkAppList(this);
				// 检查是否被人追杀
				FatwaManager.getInstance().checkFatwaMap(this);
				// int mapId = this.getCoords().getMap();
				if (onlineSec == 0) {
					// UserInfoData userInfoData = new UserInfoData();
					// userInfoData.setUserId(userId);
					// CommonParser.getInstance().postTask(DbConst.USER_GET_INFO,
					// this, userInfoData);
					this.setOnlineSec(1);
					Item item = LuaService.callOO4Object(2, Item.LUA_CONTAINER, 8015, "creatJavaItemSingle");
					MailManager.getInstance().sendSysMail(roleid, "系统邮件", "英雄你好：天龙八部武侠世界的大门已为您开启，请允许我邀请您一起踏入江湖，带好我送您的江湖好礼，祝您在天龙世界里一展雄风!", 0, item);
				}
				if (mailAgent.getMailNum() >= 50) {
					mailAgent.sendMailNotify(this, Mail.STATE_FULL);
				} else if (mailAgent.containsUnreadMail()) {
					mailAgent.sendMailNotify(this, Mail.STATE_NEW);
				}

				if (this.getApprenticeNum() > 2) { // 如果徒弟大于2个，就清空
					this.getApprentice().clear();
				}

				if (this.getMarry().size() > 1) { // 如果夫妻大于1个，就清空
					this.getMarry().clear();
				}

				if (this.getMaster().size() > 1) { // 如果师父大于1，就清空
					this.getMaster().clear();
				}

				break;
			case SEND_MAILL_LIST:
				SendMailListData sentMailListData = (SendMailListData) ds;
				this.getMailAgent().initSendMailList(sentMailListData.getSendMailList());
				break;
			case ROLE_UPDATE:
				isWriteBacking = false;

				if (!this.isConnected()) {
					// 这可能会产生内存泄露
					// 如果角色在队伍中 是队长 解散队伍 不是队长则移除队伍
					if (this.getTeamAgent().getTeam() != null) {
						if (this.getTeamAgent().getTeam().getLeader() == this)
							this.getTeamAgent().dissmissTeam();
						else
							this.getTeamAgent().kick(this);
					}

					if (this.getBattle() == null) {
						OnlineService.removeOnlinePlayer(this.roleid);
						Log.info(Log.STDOUT, "【" + getRoleid() + "】【" + getName() + "】下线了");
					}
				}

				break;
			case USER_GET_INFO:
				UserInfoData userInfo = (UserInfoData) ds;
				String title = "用户注册信息";
				String content = "请妥善保管您的注册信息, 祝您游戏愉快!/" + "您的用户名为: " + userInfo.getUname() + "/" + "密码: " + userInfo.getPasswd() + "/" + "绑定手机号: " + userInfo.getTel() + "/" + "服务器: "
						+ TianLongServer.serverName + "/" + "职业: " + vocation + "/" + "性别: " + (sex == 0 ? "男" : "女") + "/";
				MailManager.getInstance().sendSysMail(roleid, title, content, 0, null);
				break;
			case BatchGetRoleCard:
				RoleCardService.INSTANCE.cleanDiscarded(friends);
				RoleCardService.INSTANCE.cleanDiscarded(foes);
				RoleCardService.INSTANCE.cleanDiscarded(marry);
				RoleCardService.INSTANCE.cleanDiscarded(master);
				RoleCardService.INSTANCE.cleanDiscarded(apprentice);
				RoleCardService.INSTANCE.cleanDiscarded(enemys);
			default:
				// Log.error(Log.STDOUT, "handle", "unhandled msgid! : " +
				// eventID);
				break;
			}
		}
	}

	public MailAgent getMailAgent() {
		return (MailAgent) this.getAgent(MsgCatIDs.CAT_MAIL);
	}

	@Override
	public int fixValueAfterBuff(final byte buffType, final int value) {
		return buffManager.fixValueAfterBuff(buffType, value);
	}

	public int getRoleState() {
		int state = 0;
		TeamAgent teamAgent = this.getTeamAgent();
		if (teamAgent.getTeam() != null) {
			state += 1 << 0;
			if (teamAgent.getTeam().getLeader() == this)
				state += 1 << 1;
		}

		if (this.getBattleAgent().getBattle() != null)
			state += 1 << 2;

		return state;
	}

	public void freshCoords(final Coords coords) {
		this.coords.setMap(coords.getMap()).setX(coords.getX()).setY(coords.getY());
	}

	public Coords getCoords() {
		return coords;
	}

	@Override
	public RoleBean toRole() {
		return this;
	}

	public RoleData toData() {
		RoleData data = new RoleData();
		data.setRoleid(roleid);
		data.setZoneid(this.getZoneid());
		data.setUserId(this.getUserid());

		data.setName(this.getName());
		data.setSex(this.getSex());
		data.setVocation((byte) this.getVocation().ordinal());
		data.setAnimeGroup(this.getAnimeGroup());
		data.setAnime(this.getAnime());
		data.setLevel(this.getLevel());
		data.setGold(this.getGold());
		data.setMark(this.getMark());
		data.setMoney(this.getMoney());
		data.setLastmap(this.getCoords().getMap());
		data.setLastmapx(this.getCoords().getX());
		data.setLastmapy(this.getCoords().getY());
		data.setHP(this.getHP());
		data.setMP(this.getMP());
		data.setStrength(this.strength);
		data.setAgility(this.agility);
		data.setIntellect(this.intellect);
		data.setVitality(this.vitality);
		data.setOnlineSec(this.getOnlineSec());
		data.setEXP(this.getEXP());
		data.setTasks(this.getTasks().toString());
		data.setFriends(this.getFriendsString());
		data.setFoes(this.getFoesString());
		data.setSkills(this.getSkillStr());
		data.setStoreStr(this.store.serialize());
		data.setBuff(this.getBuffManager().serialize());
		data.setJoyid(this.getJoyid());
		data.setEpithet(epithet);
		data.setCommunity(community);
		data.setRegdate(regdate);
		data.setLogoff(logoff);
		data.setCharm(charm);
		data.setGangid(gangid);
		data.setJobTitle(jobTitle.getJobTitleVaule());
		data.setTotalKillPlayerNum(totalKillPlayerNum);
		data.setKillPlayerNum(killPlayerNum);
		long leftTime = resetKillPlayerNumLeftTime - (System.currentTimeMillis() - killPlayerNumActiveTime);
		data.setResetKillPlayerNumLeftTime(leftTime > 0 ? leftTime : 0);
		data.setRacingMarks(racingMarks);
		data.setHonor(honor);
		data.setEvil(evil);
		data.setSneakAttackNum(sneakAttackNum);
		data.setLastBattleTime(lastBattleTime);
		data.setMaster(this.getMasterString());
		data.setMarry(this.getMarryString());
		data.setApprentice(this.getApprenticeString());
		data.setAppdelmaster(this.getAppdelmasterString());
		data.setMarryringtime(marryringtime);
		data.setRemovemastertime(removemastertime);
		data.setRemoveapptime(removeapptime);
		data.setEnemies(this.getEnemyString());
		data.setIsNotFatwa(this.getIsNotFatwa());
		return data;
	}

	@Override
	public boolean hasBuff(final byte buffType) {
		return buffManager.hasBuff(buffType);
	}

	public Buff getBuff(final byte buffType) {
		return buffManager.getBuff(buffType);
	}

	public void setEpithet(final String epithet) {
		this.epithet = epithet;
	}

	public String getEpithet() {
		if (epithet != null && epithet.length() > 0)
			return epithet;

		if (community > 0) {
			Community comm = Communitys.INSTANCE.getCommunity(community);
			if (comm != null)
				return comm.getCname();
		}

		return "";
	}

	public WapPayAgent getWapPayAgent() {
		return wapPayAgent;
	}

	public void setCommunity(final long community) {
		this.community = community;
	}

	public long getCommunity() {
		return community;
	}

	public RoleCard makeCard() {
		return new RoleCard().setRoleid(roleid).setName(name).setLevel(level).setGold(gold).setCharm(charm).setExp(EXP).setMark(mark).setEvil(evil).setHonor(honor);
	}

	public void setRegdate(final Date regdate) {
		this.regdate = regdate;
	}

	public Date getRegdate() {
		return regdate;
	}

	public void setMarryringtime(final Date marryringtime) {
		this.marryringtime = marryringtime;
	}

	public Date getMarryringtime() {
		return marryringtime;
	}

	public void setRemovemastertime(final Date removemastertime) {
		this.removemastertime = removemastertime;
	}

	public Date getRemovemastertime() {
		return removemastertime;
	}

	public void setRemoveapptime(final Date removeapptime) {
		this.removeapptime = removeapptime;
	}

	public Date getRemoveapptime() {
		return removeapptime;
	}

	/** 查看是否能拜师 */
	// public boolean getIfAddMaster(){
	// Date date = new Date();
	// if(this.removemastertime==null){
	// this.setRemovemastertime(date);
	// return true;
	// }
	// if(rmmasterifforce==1){
	// if(((date.getTime()-removemastertime.getTime())/(1000*60*60))>48){
	// this.setRemovemastertime(date);
	// return true;
	// }
	// }
	// return false;
	// }
	//

	public void setLogoff(final Date logoff) {
		this.logoff = logoff;
	}

	public Date getLogoff() {
		return logoff;
	}

	public void setCharm(final int charm) {
		if (charm >= 99999999)
			this.charm = 99999999;
		else if (charm <= 0)
			this.charm = 0;
		else
			this.charm = charm;

		if (OnlineService.getOnline(this.roleid) != null)
			RoleEvent.CharmEvent.handleEvent(this);
	}

	public int getCharm() {
		return charm;
	}

	public void setGangid(final long gangid) {
		this.gangid = gangid;
	}

	/**
	 * 返回帮派ID
	 */
	public long getGangid() {
		return gangid;
	}

	@Override
	public void minTick(final int curMin) {
		minLinstenerMan.execute(curMin);

		this.store.minTick(curMin);
	}

	public void setJobTitle(final GangJobTitle jobTitle) {
		this.jobTitle = jobTitle;
	}

	public GangJobTitle getJobTitle() {
		return jobTitle;
	}

	public ActionAgent getActionAgent() {
		return (ActionAgent) this.getAgent(MsgCatIDs.CAT_ACTION);
	}

	public EnemyAgent getEnemyAgent() {
		return (EnemyAgent) this.getAgent(MsgCatIDs.CAT_ENEMY);
	}

	@Override
	public RoleBean getRole() {
		return this;
	}

	@Override
	public boolean isRoleOnline() {
		return OnlineService.getOnline(roleid) != null;
	}

	@Override
	public void tick() {
		checkPVPProtectTime();

		dataSaver.mainLoopTick();

		this.getMapAgent().flushNearby();
		buffManager.tick();

		// int curMinute = Cardinality.INSTANCE.getMinute();
		// this.getRacingManager().minTick(curMinute,this);

		MailAgent mailAgent = this.getMailAgent();
		mailAgent.inboxCheckTimeOut();
		mailAgent.sentBoxCheckTimeOut();
		// checkResetKillPlayerNumLeftTime();

		if (unHandledAsk != null)
			unHandledAsk.tick(this);

		this.getBattleAgent().checkTimeOut();
	}

	public MapAgent getMapAgent() {
		return (MapAgent) this.getAgent(MsgCatIDs.CAT_MAP);
	}

	/**
	 * 减少邪恶值（系统每分钟会被调用一次，每分钟减少1点）
	 * 
	 * @param num
	 */
	public void lessenEvil(long num) {
		if (evil > 0) {
			if (num > evil) {
				num = evil;
			}
			evil -= num;
		}
		if (num > 1) {
			if (OnlineService.getOnline(roleid) != null)
				RoleEvent.TotalKillNumEvent.handleEvent(this);
		}
		if (evil < 200) {
			setNameCatalog(NAME_WHITE);
			// 刷新基本属性
			MessageSend.prepareBody();
			SubModules.fillAttributes(this);
			MessageSend.putShort((short) 0);
			MessageSend.sendMsg(this, MsgID.MsgID_Special_Train);
		}

	}

	/**
	 * 添加罪恶值
	 * 
	 * @param num
	 */
	public void addEvil(int num) {

		setEvil(getEvil() + num);
		if (evil >= 200) {
			setNameCatalog(NAME_RED);
		}
	}

	@Override
	public void setEvil(int evil) {
		this.evil = evil;

		if (OnlineService.getOnline(roleid) != null)
			RoleEvent.TotalKillNumEvent.handleEvent(this);
	}

	@Override
	public void setHonor(int honor) {
		this.honor = honor;

		if (OnlineService.getOnline(roleid) != null)
			RoleEvent.TotalHonor.handleEvent(this);
	}

	/**
	 * 减少每日偷袭次数
	 * 
	 * @param num
	 */
	public void changeSneakAttackNum(int num) {
		int newNum = this.getSneakAttackNum() - num;
		this.setSneakAttackNum(newNum >= 0 ? newNum : 0);
	}

	// private void checkResetKillPlayerNumLeftTime() {
	// if(resetKillPlayerNumLeftTime > 0) {
	// if(resetKillPlayerNumLeftTime <= (System.currentTimeMillis() -
	// killPlayerNumActiveTime)) {
	// resetKillPlayerNumLeftTime = 0;
	// killPlayerNum = 0;
	// killPlayerNumActiveTime = 0;
	// setNameCatalog(NAME_WHITE);
	//
	// // 刷新基本属性
	// MessageSend.prepareBody();
	// SubModules.fillAttributes(this);
	// MessageSend.putShort((short) 0);
	// MessageSend.sendMsg(this, MsgID.MsgID_Special_Train);
	// }
	//
	// }
	// }

	private void checkPVPProtectTime() {
		if (pvPProtectTime > 0 && System.currentTimeMillis() - pvPProtectTime > PVP_PROTECT_TIME)
			pvPProtectTime = 0;
	}

	public void setMinLinstenerMan(final RoleMinListenerMan minLinstenerMan) {
		this.minLinstenerMan = minLinstenerMan;
	}

	public RoleMinListenerMan getMinLinstenerMan() {
		return minLinstenerMan;
	}

	public void setGangid(final int gangid) {
		this.gangid = gangid;
	}

	@Override
	public boolean decreaseMP(final int MP) {
		return false;
	}

	/** @return 动态属性管理 */
	public PropertyMan getPropertyMan() {
		return propertyMan;
	}

	public boolean getIsNotFatwa() {

		return isNotFatwa;
	}

	public void setIsNotFatwa(boolean isNotFatwa) {
		this.isNotFatwa = isNotFatwa;
	}

	public Boolean getCanChangeName() {
		return canChangeName;
	}

	public void setCanChangeName(final Boolean canChangeName) {
		this.canChangeName = canChangeName;
	}

	@Override
	public Vocation getVocation() {
		return vocation;
	}

	public void setVocation(Vocation vocation) {
		this.vocation = vocation;
	}

	public void addChallengeRecord(int inning, int challengeRoleid, int acceptRoleid, String acceptName, int gold, String win) {
		CyclesMessage cyclesMessage = new CyclesMessage(inning, challengeRoleid, acceptRoleid, acceptName, gold, win);
		challengeRecordTreeSet.add(cyclesMessage);
	}

	public void addAcceptRecord(int inning, int challengeRoleid, int acceptRoleid, String challengeName, int gold, String win) {
		CyclesMessage cyclesMessage = new CyclesMessage(inning, challengeRoleid, acceptRoleid, challengeName, gold, win);
		acceptRecordTreeSet.add(cyclesMessage);
	}

	public ArrayList<CyclesMessage> getChallengeRecord() {
		if (challengeRecordTreeSet != null) {
			cyclesMessageArrayList.clear();
			Iterator<CyclesMessage> challengeTreeSet = challengeRecordTreeSet.iterator();
			int i = 1;
			while (challengeTreeSet.hasNext() && i <= 5) {
				CyclesMessage cyclesMessage = challengeTreeSet.next();
				cyclesMessageArrayList.add(cyclesMessage);
				i++;
			}
		}
		return cyclesMessageArrayList;
	}

	public ArrayList<CyclesMessage> getAcceptRecord() {
		if (acceptRecordTreeSet != null) {
			cyclesMessageArrayList.clear();
			Iterator<CyclesMessage> acceptTreeSet = acceptRecordTreeSet.iterator();
			int i = 1;
			while (acceptTreeSet.hasNext() && i <= 5) {
				CyclesMessage cyclesMessage = acceptTreeSet.next();
				cyclesMessageArrayList.add(cyclesMessage);
				i++;
			}
		}
		return cyclesMessageArrayList;
	}

	@Override
	public byte jobTitle() {
		return this.jobTitle.getJobTitleVaule();
	}

	@Override
	public String getStoreStr() {
		return this.store.serialize();
	}

	@Override
	public DataStruct getRoleDataStruct() {
		return this.toData().getRoleDataStruct();
	}

	@Override
	public byte getVocationCode() {
		return (byte) this.vocation.ordinal();
	}

	public void seconedTick(int curSeconed) {
		this.tasks.seconedTick(curSeconed);
	}

	/** 侦听追杀令的到期时间 */
	public void seconedFatwa(long curSeconed) {
		TreeMap<Integer, Fatwa> fatwas = FatwaTable.INSTANCE.getFatwas();
		Iterator<Integer> fatIter = fatwas.keySet().iterator();
		while (fatIter.hasNext()) {
			int key = fatIter.next();
			Fatwa fatwa = fatwas.get(key);
			RoleBean roleBean = OnlineService.getOnline(key);
			if (curSeconed > fatwa.getTimeOut()) {
				// 发送邮件
				MailManager.getInstance().sendSysMail(fatwa.getPromulgatorId(), "武林追杀令", "追杀失败！", 0, null);
				// 删除“杀”标记
				if (roleBean != null) {
					roleBean.delKillIcon();
					roleBean.setIsNotFatwa(false);
				}
				// 删除追杀状态
				fatwas.remove(key);
			}
		}
	}

	public AgentProxy getAgent(MsgCatIDs msgCatID) {
		if (!msgCatID.equals(MsgCatIDs.CAT_HEARTBEAT) && !msgCatID.equals(MsgCatIDs.CAT_MAP))
			System.currentTimeMillis(); // for breakpoint

		AgentProxy agent = agents.get(msgCatID);
		if (agent == null) {
			Class<? extends AgentProxy> agentClass = msgCatID.getAgent();
			if (agentClass == null)
				return null;
			try {
				agent = agentClass.getConstructor(RoleBean.class).newInstance(this);
				agents.put(msgCatID, agent);
			} catch (Exception e) {
				Log.error(Log.ERROR, "无法生成代理对象", e);
			}
		}
		return agent;
	}

	@Override
	public byte getSeatId() {
		return this.getBattleAgent().getSeatId();
	}

	public PlayerSkillAgent getSkillAgent() {
		return this.skillAgent;
	}

	public RoleAgent getRoleAgent() {
		return (RoleAgent) this.getAgent(MsgCatIDs.CAT_ROLE);
	}

	private RacingManager racingManager = RacingManager.getInstance();

	public RacingManager getRacingManager() {
		return racingManager;
	}

	public void setRacingManager(RacingManager racingManager) {
		this.racingManager = racingManager;
	}

	/**
	 * 赛马战报
	 */
	private String racingMessage = "无战报";

	public String getRacingMessage() {
		return racingMessage;
	}

	public void setRacingMessage(String racingMessage) {
		this.racingMessage = racingMessage;
	}

	/**
	 * 检查最后偷袭时间
	 */
	public void checkLastBattleTime() {
		try {
			Date date = new Date();
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date lastBattleTime = this.getLastBattleTime();
			Date newDate = format.parse((new SimpleDateFormat("yyyy-MM-dd")).format(date) + " 04:00:00");
			if ((newDate.getTime() - lastBattleTime.getTime()) > (24 * 3600 * 1000) || (newDate.compareTo(lastBattleTime) > 0 && newDate.compareTo(date) < 0)) {
				this.setSneakAttackNum(0);
				this.setLastBattleTime(date);
			}

		} catch (Exception e) {
			Log.error(Log.ERROR, "检查最后偷袭时间", e);
		}
	}

	/**
	 * 发布任务时的逻辑判断
	 */
	private int[] decreaseGold = new int[] { 50000, 100000, 200000, 500000 };
	boolean flag;

	public String doFatwa(int eid) {
		TreeMap<Integer, Fatwa> fatwas = FatwaTable.INSTANCE.getFatwas();
		String returnStr = "";

		// 是否在线
		if (OnlineService.isRoleOnline(eid)) {

			// 在线
			RoleBean roleBean = OnlineService.getOnline(eid);
			// 是否已经被追杀
			if (roleBean.getIsNotFatwa()) {// 是
				returnStr = "目标已被通缉！已有其他玩家追杀此人，请稍后再试！";
			} else {// 否
				returnStr = this.onLineFatwa(roleBean);
				if (flag == true) {
					this.updateState(roleBean);
					roleBean.setIsNotFatwa(true);
					roleBean.addKillIcon();
					roleBean.sendSystemMsg(this.getNick());
				}
			}
		} else {
			// 不在线

			// 是否已经被追杀
			if (fatwas.containsKey(eid)) {// 是
				returnStr = "目标已被通缉！已有其他玩家追杀此人，请稍后再试！";
			} else {// 否
					// 是否在仇人列表中
				if (RoleCardService.INSTANCE.hasCard(eid)) {// 是
					RoleCard card = RoleCardService.INSTANCE.getCard(eid);

					// 把被追杀者存起来
					returnStr = this.outLineFatwa(card);
					if (flag == true) {
						this.updateState(card);
					}

				} else {// 否
					returnStr = "目标不存在！老夫从未听过此人，你确定是这个名字么？";
				}
			}
		}
		return returnStr;
	}

	// 设置追杀方法
	public String onLineFatwa(RoleBean roleBean) {
		String returnStr = "";
		if (this.getFatwaRoleNum() == 3) {
			returnStr = "同时只能发布三条追杀令！";
			flag = false;
			return returnStr;
		}
		if (roleBean.getLevel() < 60) {
			returnStr = "目标等级不足60！老夫只对等级超过60级的家伙出手！";
			flag = false;
			return returnStr;
		} else if (roleBean.getLevel() < 80) {
			if (this.getGold() < decreaseGold[0]) {
				returnStr = "金钱不足！要杀如此高手最少也要5金，你还是先去赚钱吧！";
				flag = false;
				return returnStr;
			} else {
				this.setGold(this.getGold() - decreaseGold[0]);
				returnStr = "追杀" + roleBean.getName() + "成功!扣除5金!";
				flag = true;
				return returnStr;
			}
		} else if (roleBean.getLevel() < 90) {
			if (this.getGold() < decreaseGold[1]) {
				returnStr = "金钱不足！要杀如此高手最少也要10金，你还是先去赚钱吧！";
				flag = false;
				return returnStr;
			} else {
				this.setGold(this.getGold() - decreaseGold[1]);
				returnStr = "追杀" + roleBean.getName() + "成功!扣除10金!";
				flag = true;
				return returnStr;
			}
		} else if (roleBean.getLevel() < 100) {
			if (this.getGold() < decreaseGold[2]) {
				returnStr = "金钱不足！要杀如此高手最少也要20金，你还是先去赚钱吧！";
				flag = false;
				return returnStr;
			} else {
				this.setGold(this.getGold() - decreaseGold[2]);
				returnStr = "追杀" + roleBean.getName() + "成功!扣除20金!";
				flag = true;
				return returnStr;
			}
		} else {
			if (this.getGold() < decreaseGold[3]) {
				returnStr = "金钱不足！要杀如此高手最少也要50金，你还是先去赚钱吧！";
				flag = false;
				return returnStr;
			} else {
				this.setGold(this.getGold() - decreaseGold[3]);
				returnStr = "追杀" + roleBean.getName() + "成功!扣除50金!";
				flag = true;
				return returnStr;
			}
		}
	}

	// 设置追杀方法
	public String outLineFatwa(RoleCard card) {
		String returnStr = "";
		if (this.getFatwaRoleNum() == 3) {
			returnStr = "同时只能发布三条追杀令！";
			flag = false;
			return returnStr;
		}
		if (card.getLevel() < 60) {
			returnStr = "目标等级不足60！老夫只对等级超过60级的家伙出手！";
			flag = false;
			return returnStr;
		} else if (card.getLevel() < 80) {
			if (this.getGold() < decreaseGold[0]) {
				returnStr = "金钱不足！要杀如此高手最少也要5金，你还是先去赚钱吧！";
				flag = false;
				return returnStr;
			} else {
				this.setGold(this.getGold() - decreaseGold[0]);
				returnStr = "追杀" + card.getName() + "成功!扣除5金!";
				flag = true;
				return returnStr;
			}
		} else if (card.getLevel() < 90) {
			if (this.getGold() < decreaseGold[1]) {
				returnStr = "金钱不足！要杀如此高手最少也要10金，你还是先去赚钱吧！";
				flag = false;
				return returnStr;
			} else {
				this.setGold(this.getGold() - decreaseGold[1]);
				returnStr = "追杀" + card.getName() + "成功!扣除10金!";
				flag = true;
				return returnStr;
			}
		} else if (card.getLevel() < 100) {
			if (this.getGold() < decreaseGold[2]) {
				returnStr = "金钱不足！要杀如此高手最少也要20金，你还是先去赚钱吧！";
				flag = false;
				return returnStr;
			} else {
				this.setGold(this.getGold() - decreaseGold[2]);
				returnStr = "追杀" + card.getName() + "成功!扣除20金!";
				flag = true;
				return returnStr;
			}
		} else {
			if (this.getGold() < decreaseGold[3]) {
				returnStr = "金钱不足！要杀如此高手最少也要50金，你还是先去赚钱吧！";
				flag = false;
				return returnStr;
			} else {
				this.setGold(this.getGold() - decreaseGold[3]);
				returnStr = "追杀" + card.getName() + "成功!扣除50金!";
				flag = true;
				return returnStr;
			}
		}
	}

	/**
	 * 发布任务时的状态更新
	 */
	public void updateState(RoleBean roleBean) {
		// 更新追杀状态
		TreeMap<Integer, Fatwa> fatwas = FatwaTable.INSTANCE.getFatwas();
		Fatwa fatwa = new Fatwa();

		fatwa.setPromulgatorId(this.getRoleid());
		fatwa.setPromulgatorName(this.getNick());
		fatwa.setRoleIdByFatwa(roleBean.getRoleid());
		fatwa.setTimeOut(Cardinality.INSTANCE.getSecond() + restrainTime);

		if (!fatwas.containsKey(roleBean.getRoleid())) {

			fatwas.put(roleBean.getRoleid(), fatwa);
		}
		try {
			Log.info(Log.FATWA,
					new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "#$" + 0 + "#$" + fatwa.getPromulgatorId() + "#$" + fatwa.getPromulgatorName() + "#$" + this.getLevel() + "#$"
							+ fatwa.getRoleIdByFatwa() + "#$" + fatwa.getTimeOut() + "#$" + roleBean.getLevel() + "#$#$#$" + TianLongServer.srvId);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 发布任务时的状态更新
	 */
	public void updateState(RoleCard card) {
		// 更新追杀状态
		TreeMap<Integer, Fatwa> fatwas = FatwaTable.INSTANCE.getFatwas();
		Fatwa fatwa = new Fatwa();

		fatwa.setPromulgatorId(this.getRoleid());
		fatwa.setPromulgatorName(this.getNick());
		fatwa.setRoleIdByFatwa(card.getRoleid());
		fatwa.setTimeOut(Cardinality.INSTANCE.getSecond() + restrainTime);

		if (!fatwas.containsKey(card.getRoleid())) {

			fatwas.put(card.getRoleid(), fatwa);
		}

		try {
			Log.info(Log.FATWA,
					new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "#$" + 0 + "#$" + fatwa.getPromulgatorId() + "#$" + fatwa.getPromulgatorName() + "#$" + this.getLevel() + "#$"
							+ fatwa.getRoleIdByFatwa() + "#$" + fatwa.getTimeOut() + "#$" + card.getLevel() + "#$#$#$" + TianLongServer.srvId);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 为目标添加“杀”图标
	 */
	public void addKillIcon() {

		MessageSend.prepareBody();
		MessageSend.putShort((short) 802);
		this.colorJudgement();
		MessageSend.putString("[杀]");
		MessageSend.sendMsg(this, MsgID.MsgID_Special_Train);
	}

	/**
	 * 为目标删除“杀”图标
	 */
	public void delKillIcon() {

		MessageSend.prepareBody();
		MessageSend.putShort((short) 802);
		this.colorJudgement();
		MessageSend.putString(" ");
		MessageSend.sendMsg(this, MsgID.MsgID_Special_Train);
	}

	/** “杀”的颜色判断 */
	public void colorJudgement() {

		if (this.getLevel() <= 79) {
			MessageSend.putInt(Color.BLACK.getRGB());
		} else if (this.getLevel() >= 80 && this.getLevel() <= 89) {
			MessageSend.putInt(Color.BLUE.getRGB());
		} else if (this.getLevel() >= 90 && this.getLevel() <= 99) {
			MessageSend.putInt(Color.RED.getRGB());
		} else if (this.getLevel() >= 100) {
			Color color = new Color(178, 0, 237);
			MessageSend.putInt(color.getRGB());
		}
	}

	/** 发送系统信息 */
	public void sendSystemMsg(String name) {

		String talk = name + "对你发布了江湖追杀令。最近出门要多加小心了。";
		MessageSend.prepareBody();

		if (talk != null && !"".equals(talk)) {
			MessageSend.putShort((short) 400);
			MessageSend.putString(talk);
			MessageSend.putInt(0xff0000);
		}

		MessageSend.putShort((short) 0);

		MessageSend.sendMsg(this, MsgID.MsgID_Special_Train);
	}

	public int getFatwaRoleNum() {
		int count = 0;
		TreeMap<Integer, Fatwa> fatwas = FatwaTable.INSTANCE.getFatwas();
		Iterator<Integer> fatIter = fatwas.keySet().iterator();
		while (fatIter.hasNext()) {
			Fatwa f = fatwas.get(fatIter.next());
			if (this.getRoleid() == f.promulgatorId) {
				count++;
			}
			if (count == 3)
				return count;
		}
		return count;
	}

	public int getToken(int id) {
		return taskToken.get(id);
	}

	public void putToken(int taskId, int token) {
		this.taskToken.put(taskId, token);
	}

	public Map<Integer, Integer> getTaskToken() {
		return this.taskToken;
	}

	/**
	 * @return the logOnTime
	 */
	public long getLogOnTime() {
		return logOnTime;
	}

	/**
	 * @param logOnTime
	 *            the logOnTime to set
	 */
	public void setLogOnTime(long logOnTime) {
		this.logOnTime = logOnTime;
	}

	/**
	 * @return the heartNum
	 */
	public int getHeartNum() {
		return heartNum;
	}

	/**
	 * @param heartNum
	 *            the heartNum to set
	 */
	public void setHeartNum(int heartNum) {
		this.heartNum = heartNum;
	}

}
