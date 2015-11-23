package com.joyveb.tlol.gang;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.joyveb.tlol.Benefit;
import com.joyveb.tlol.LuaService;
import com.joyveb.tlol.MessageSend;
import com.joyveb.tlol.OnlineService;
import com.joyveb.tlol.SubModules;
import com.joyveb.tlol.Watchable;
import com.joyveb.tlol.billboard.TopTributeGang;
import com.joyveb.tlol.db.DataHandler;
import com.joyveb.tlol.db.DataStruct;
import com.joyveb.tlol.db.parser.BatchGetRoleCard;
import com.joyveb.tlol.db.parser.CommonParser;
import com.joyveb.tlol.db.parser.DbConst;
import com.joyveb.tlol.db.parser.GangUpdater;
import com.joyveb.tlol.protocol.MsgID;
import com.joyveb.tlol.role.RoleBean;
import com.joyveb.tlol.role.RoleCard;
import com.joyveb.tlol.role.RoleCardService;
import com.joyveb.tlol.schedule.MinTickHandler;
import com.joyveb.tlol.schedule.OneOffSchedule;
import com.joyveb.tlol.schedule.ScheduleManager;
import com.joyveb.tlol.server.ServerMessage;
import com.joyveb.tlol.util.Cardinality;
import com.joyveb.tlol.util.CompareListGangFight;
import com.joyveb.tlol.util.CompareListTribute;
import com.joyveb.tlol.util.Log;

public class Gang implements DataHandler, MinTickHandler, Watchable {

	/** 数据库回写间隔 */
	public static int Write_Back_Interval = 10;
	/** 排序间隔 */
	public static int Sort_Interval = 5;
	/** 单页面显示数量 */
	public static byte Per_Page = 10;

	private int nextWriteBack;
	// private int nextSort;

	/** 是否有数据变动 */
	private boolean changed = false;

	private long id;

	private String name;

	/** 创建日期 */
	private Date created;

	private int level = 1;

	/** 帮会公告 **/
	private String bulletin;

	/** 帮会通缉令 */
	private String catchOrder;

	/** 修改类型 */
	private byte modifyType;

	public byte getModifyType() {
		return modifyType;
	}

	public void setModifyType(byte modifyType) {
		this.modifyType = modifyType;
	}

	public String getCatchOrder() {
		return catchOrder;
	}

	public void setCatchOrder(String catchOrder) {
		this.catchOrder = catchOrder;
	}

	/** 总帮贡 */
	private int tribute;

	/** 帮主 */
	private int leader;

	/** 副帮主 */
	private HashSet<Integer> viceLeader = new HashSet<Integer>(2);

	/** 长老 */
	private HashSet<Integer> presbyter = new HashSet<Integer>(5);

	/** 成员 */
	private ArrayList<RoleCard> members = new ArrayList<RoleCard>();

	/** 个人贡献统计 */
	private HashMap<Integer, Integer> tributeStat = new HashMap<Integer, Integer>();

	/** 帮派卡片 */
	private GangCard gangCard = new GangCard();

	/** 数据是否完整，可能正在加载中 */
	private boolean intact;

	private final Comparator<RoleCard> comparator = new Comparator<RoleCard>() {
		@Override
		public int compare(RoleCard card1, RoleCard card2) {

			// 在线玩家排名靠前、职位高排名靠前、等级高排名靠前、经验多排名靠前
			if (OnlineService.getOnline(card2.getRoleid()) != null && OnlineService.getOnline(card1.getRoleid()) == null)
				return 1;
			else if (OnlineService.getOnline(card1.getRoleid()) != null && OnlineService.getOnline(card2.getRoleid()) == null)
				return -1;
			else if (getJobTitle(card2).getJobTitleVaule() > getJobTitle(card1).getJobTitleVaule())
				return 1;
			else if (getJobTitle(card2).getJobTitleVaule() < getJobTitle(card1).getJobTitleVaule())
				return -1;
			else if (card2.getLevel() > card1.getLevel())
				return 1;
			else if (card2.getLevel() < card1.getLevel())
				return -1;
			else if (card2.getExp() > card1.getExp())
				return 1;
			else if (card2.getExp() < card1.getExp())
				return -1;
			else
				return 0;

			// //在线的比不在线的靠钱
			// if(OnlineService.getOnline(card2.getRoleid()) != null &&
			// OnlineService.getOnline(card2.getRoleid()) == null)
			// return 1;
			//
			// System.out.println("asdasdad");
			// //职位高的靠前
			// if(getJobTitle(card2).getJobTitleVaule() >
			// getJobTitle(card1).getJobTitleVaule())
			// return 1;
			//
			// //职位低的靠后
			// if(getJobTitle(card2).getJobTitleVaule() <
			// getJobTitle(card1).getJobTitleVaule())
			// return -1;
			//
			// //同职位按照等级、经验排序
			// if(card2.getLevel() > card1.getLevel())
			// return 1;
			// else if(card2.getLevel() < card1.getLevel())
			// return -1;
			// else if(card2.getExp() > card1.getExp())
			// return 1;
			// else if(card2.getExp() < card1.getExp())
			// return -1;
			// else
			// return 0;
		}
	};

	public Gang() {
		int curMinute = Cardinality.INSTANCE.getMinute();
		nextWriteBack = curMinute + Write_Back_Interval;
	}

	public boolean contains(RoleBean role) {
		return tributeStat.containsKey(role.getRoleid());
	}

	public boolean fullStrength() {
		return LuaService.call4Int("gangUpperLimit", level) <= members.size();
	}

	public void addMember(RoleCard card) {
		members.add(card);
		tributeStat.put(card.getRoleid(), 0);
	}

	/** 是否需要全帮派通知有人加入 */
	public void addMember(RoleCard card, boolean notify) {
		members.add(card);
		tributeStat.put(card.getRoleid(), 0);
		this.changed = true;

		if (notify) {
			final String message = "恭喜" + card.getName() + "加入本帮～";
			ScheduleManager.INSTANCE.offerTask(new OneOffSchedule() {
				public void execute() {
					ServerMessage.batchSendSysPrompt(members, message);
				}
			});
		}
	}

	/** 适用于游戏内的删除成员 */
	public void removeMember(RoleCard card) {
		if (card == null)
			return;

		GangJobTitle jobTitle = getJobTitle(card);
		;
		if (jobTitle == GangJobTitle.Leader)
			return;

		// 解除职务
		if (jobTitle == GangJobTitle.ViceLeader)
			viceLeader.remove(Integer.valueOf(card.getRoleid()));
		else if (jobTitle == GangJobTitle.Presbyter)
			presbyter.remove(Integer.valueOf(card.getRoleid()));

		members.remove(card);// 移除成员
		tributeStat.remove(card.getRoleid());
		this.changed = true;

		final String message = card.getName() + "已离开本帮～";
		ScheduleManager.INSTANCE.offerTask(new OneOffSchedule() {
			public void execute() {
				ServerMessage.batchSendSysPrompt(members, message);
			}
		});
	}

	/**
	 * 获取某人在帮派中的职位
	 * 
	 * @param role
	 * @return
	 */
	public GangJobTitle getJobTitle(RoleBean role) {
		return getJobTitle(role.getRoleid());
	}

	/** 获取成员职位 */
	public GangJobTitle getJobTitle(RoleCard card) {
		return getJobTitle(card.getRoleid());
	}

	/** 获取成员职位 */
	public GangJobTitle getJobTitle(int roleid) {
		if (roleid == leader)
			return GangJobTitle.Leader;

		if (viceLeader.contains(roleid))
			return GangJobTitle.ViceLeader;

		if (presbyter.contains(roleid))
			return GangJobTitle.Presbyter;

		if (tributeStat.containsKey(roleid))
			return GangJobTitle.Member;

		return GangJobTitle.NULL;
	}

	/** 根据id列表初始化成员名片，如果尚未加载则从数据库中批量取出 */
	public Gang loadCard(ArrayList<Integer> roleids) {
		Iterator<Integer> it = roleids.iterator();
		while (it.hasNext()) {
			int roleid = it.next();
			if (RoleCardService.INSTANCE.hasCard(roleid)) {
				members.add(RoleCardService.INSTANCE.getCard(roleid));
				it.remove();
			}
		}

		if (roleids.isEmpty()) {
			intact = true;
			ScheduleManager.INSTANCE.offerTask(new OneOffSchedule() {
				public void execute() {
					loadedNotify();
				}
			});
		} else {
			CommonParser.getInstance().postTask(DbConst.BatchGetRoleCard, this, new BatchGetRoleCard(roleids), true);
			while (!intact) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return this;
	}

	private void loadedNotify() {
		for (RoleCard card : members) {
			if (card.isRoleOnline()) {
				RoleBean role = OnlineService.getOnline(card.getRoleid());
				role.setJobTitle(this.getJobTitle(role));

				MessageSend.prepareBody();
				SubModules.fillGangJobTitle(role.getJobTitle(), this.name + role.getJobTitle().getDesAtTitle());
				MessageSend.putShort((short) 0);
				MessageSend.sendMsg(role, MsgID.MsgID_Special_Train);
			}
		}
	}

	/** 帮派解散时广播给成员 */
	public void dismissedNotify() {
		for (RoleCard card : members) {
			if (card.isRoleOnline()) {
				RoleBean role = OnlineService.getOnline(card.getRoleid());
				role.setGangid(0);
				role.setJobTitle(GangJobTitle.NULL);

				MessageSend.prepareBody();
				SubModules.fillGangJobTitle(GangJobTitle.NULL, null);
				LuaService.callLuaFunction("fillPrompt", "您所在的帮会已解散！");
				MessageSend.putShort((short) 0);
				MessageSend.sendMsg(role, MsgID.MsgID_Special_Train);
			}
		}
	}

	/** 从数据库中取出所有未加载成员名片的回调 */
	@Override
	public void handle(DbConst eventID, boolean flag, DataStruct ds) {
		BatchGetRoleCard batch = (BatchGetRoleCard) ds;
		members.addAll(batch.getCards().values());
		this.intact = true;
		this.loadedNotify();
	}

	@Override
	public void minTick(int curMin) {

		if (intact && curMin > nextWriteBack) {
			if (changed) {
				if (curMin == Integer.MAX_VALUE)
					Log.info(Log.STDOUT, "重启回写帮派");
				CommonParser.getInstance().postTask(DbConst.Gang_Update, null, new GangUpdater(this));
			}

			changed = false;
			nextWriteBack = curMin + Write_Back_Interval;
		}
	}

	public int benefit(Benefit bnf, int rawValue) {
		return LuaService.call4Int("gangBenefit", level, bnf.getId(), rawValue);
	}

	public void resetLevel(int level) {
		this.level = level;

		final String message = "恭喜帮派【" + this.getName() + "】升级至" + level + "级～";
		ScheduleManager.INSTANCE.offerTask(new OneOffSchedule() {
			public void execute() {
				ServerMessage.sendBulletin(message);
			}
		});

		this.changed = true;
	}

	public void resetBulletin(String bulletin) {
		this.bulletin = bulletin;

		ScheduleManager.INSTANCE.offerTask(new OneOffSchedule() {
			public void execute() {
				ServerMessage.batchSendSysPrompt(members, "帮派公告已修改～");
			}
		});

		this.changed = true;
	}

	public void resetCatchOrder(String catchOrder) {
		this.catchOrder = catchOrder;

		ScheduleManager.INSTANCE.offerTask(new OneOffSchedule() {
			public void execute() {
				ServerMessage.batchSendSysPrompt(members, "帮派通缉令已修改～");
			}
		});
		this.changed = true;
	}

	public void resetTribute(int tribute) {
		this.tribute = tribute;
		this.gangCard.setTribute(tribute);

		TopTributeGang.INSTANCE.update(this.gangCard);

		this.changed = true;
	}

	/**
	 * 更新个人帮贡
	 * 
	 * @param role
	 * @param tri
	 *            更新个人帮贡的数量
	 */
	public void updateTribute(RoleBean role, int tri) {
		if (!contains(role))
			return;
		try {
			tributeStat.put(role.getRoleid(), tributeStat.get(role.getRoleid()) + tri);

			this.resetTribute(tribute + tri);

			if (tributeStat.size() > 4) {
				List<TributeTop> tributeTopList = new ArrayList<TributeTop>();
				Set<Integer> set = tributeStat.keySet();
				for (Iterator<Integer> it = set.iterator(); it.hasNext();) {
					TributeTop tributeTop = new TributeTop();
					int key = it.next();
					if (key != leader) {
						tributeTop.setRoleId(key);
						tributeTop.setTribute(tributeStat.get(key));
						tributeTopList.add(tributeTop);
					}
				}

				CompareListTribute sortList = new CompareListTribute();
				Collections.sort(tributeTopList, sortList);

				HashSet<Integer> set1 = new HashSet<Integer>();
				set1.add(tributeTopList.get(0).getRoleId());
				this.setViceLeader(set1);

				HashSet<Integer> set2 = new HashSet<Integer>();
				set2.add(tributeTopList.get(1).getRoleId());
				set2.add(tributeTopList.get(2).getRoleId());
				set2.add(tributeTopList.get(3).getRoleId());

				this.setPresbyter(set2);

				this.setModifyType((byte) 2);
				this.changed = true;

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * 更新不在线的个人帮贡
	 * */
	public void updateOffLineTribute(int roleid, int tri) {

		if (!tributeStat.containsKey(roleid)) {
			return;
		}
		tributeStat.put(roleid, tributeStat.get(roleid) + tri);

		this.resetTribute(tribute + tri);
	}

	public String getDescribe() {
		return "帮会等级：" + level + "/总帮贡值：" + tribute + LuaService.call4String("getGangDes", level);
	}

	@Override
	public void watch() {
		Log.info(Log.STDOUT, "查看帮派");
		Log.info(Log.STDOUT, "id  " + id);
		Log.info(Log.STDOUT, "名称  " + name);
		Log.info(Log.STDOUT, "创建时间  " + created);
		Log.info(Log.STDOUT, "等级  " + level);
		Log.info(Log.STDOUT, "公告  " + bulletin);
		Log.info(Log.STDOUT, "总帮贡  " + tribute);

		RoleCard leaderCard = RoleCardService.INSTANCE.getCard(leader);

		Log.info(Log.STDOUT, "帮主：" + (leaderCard == null ? leader : leaderCard.getName()));
		Log.info(Log.STDOUT, "副帮主：" + viceLeader);
		Log.info(Log.STDOUT, "长老：" + presbyter);
		Log.info(Log.STDOUT, "成员：");

		for (RoleCard member : members)
			Log.info(Log.STDOUT, member);
	}

	public long getId() {
		return id;
	}

	public Gang setId(long id) {
		this.id = id;
		this.gangCard.setId(id);
		return this;
	}

	public String getName() {
		return name;
	}

	public Gang setName(String name) {
		this.name = name;
		this.gangCard.setName(name);
		return this;
	}

	public Date getCreated() {
		return created;
	}

	public Gang setCreated(Date created) {
		this.created = created;
		return this;
	}

	public int getLevel() {
		return level;
	}

	public Gang setLevel(int level) {
		this.level = level;
		return this;
	}

	public int getLeader() {
		return leader;
	}

	public Gang setLeader(int leader) {
		this.leader = leader;
		return this;
	}

	public Gang setViceLeader(HashSet<Integer> viceLeader) {
		this.viceLeader = viceLeader;
		return this;
	}

	public HashSet<Integer> getViceLeader() {
		return viceLeader;
	}

	public Gang setPresbyter(HashSet<Integer> presbyter) {
		this.presbyter = presbyter;
		return this;
	}

	public HashSet<Integer> getPresbyter() {
		return presbyter;
	}

	public ArrayList<RoleCard> getMembers() {
		return members;
	}

	public Gang setBulletin(String bulletin) {
		this.bulletin = bulletin;
		return this;
	}

	public String getBulletin() {
		return bulletin;
	}

	public Gang setTributeStat(HashMap<Integer, Integer> tributeStat) {
		this.tributeStat = tributeStat;
		return this;
	}

	public HashMap<Integer, Integer> getTributeStat() {
		return tributeStat;
	}

	public Gang setTribute(int tribute) {
		this.tribute = tribute;
		this.gangCard.setTribute(tribute);

		return this;
	}

	/** 返回总帮贡 */
	public int getTribute() {
		return tribute;
	}

	public GangCard getGangCard() {
		return gangCard;
	}

	public Gang setIntact(boolean intact) {
		this.intact = intact;
		return this;
	}

	/** 返回数据是否完整，可能正在加载中 */
	public boolean isIntact() {
		return intact;
	}

	public void sortMembers() {
		Collections.sort(members, comparator);
	}

}