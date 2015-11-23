package com.joyveb.tlol.battle;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeMap;

import com.joyveb.tlol.Benefit;
import com.joyveb.tlol.LuaService;
import com.joyveb.tlol.MessageSend;
import com.joyveb.tlol.OnlineService;
import com.joyveb.tlol.TianLongServer;
import com.joyveb.tlol.buff.Buff;
import com.joyveb.tlol.buff.BuffType;
import com.joyveb.tlol.core.AgentProxy;
import com.joyveb.tlol.core.IGameCharacter;
import com.joyveb.tlol.fatwa.Fatwa;
import com.joyveb.tlol.fatwa.FatwaTable;
import com.joyveb.tlol.gang.Gang;
import com.joyveb.tlol.gang.GangService;
import com.joyveb.tlol.hegemony.Hegemony;
import com.joyveb.tlol.item.Item;
import com.joyveb.tlol.mail.MailManager;
import com.joyveb.tlol.marry.MarryMasterAddExpGold;
import com.joyveb.tlol.protocol.MsgID;
import com.joyveb.tlol.role.RoleBean;
import com.joyveb.tlol.server.ServerMessage;
import com.joyveb.tlol.task.TaskState;
import com.joyveb.tlol.util.Log;

/*战斗对象 里面包含了战斗双方的信息*/
public class Battle extends MessageSend {
	public static final byte BATTLE_PVE = 0;
	public static final byte BATTLE_PVP = 1;
	public static final byte BATTLE_DUEL = 2;
	public static final byte BATTLE_RELIVE_FULL = 3;

	private static final byte TEAM_OFFSET = 3;
	private static int autoincreaseid;

	@SuppressWarnings("unused")
	private final int id = autoincreaseid++;

	private Map<IGameCharacter, UserFightBean> fightOprations = new HashMap<IGameCharacter, UserFightBean>();

	private byte battelType;

	private static Random random = new Random();

	private long lastRoundStartTime = System.currentTimeMillis();

	private long battleStartTime = 0;

	private ArrayList<IGameCharacter> teamLeft;
	private ArrayList<IGameCharacter> teamRight;
	private ArrayList<Item> lootList = new ArrayList<Item>();
	private DuelTmpHPMP[] duelTmp = new DuelTmpHPMP[6];
	private int increaseFatwaGold; // 追杀奖励金币

	// public Battle(byte type, IGameCharacter teamLeftCharacter, IGameCharacter
	// teamRightCharacter){
	// battelType = type;
	// battleStartTime = System.currentTimeMillis();
	// teamLeft.add(teamLeftCharacter);
	// teamRight.add(teamRightCharacter);
	// teamLeftCharacter.setSeatId((byte)1);
	// teamRightCharacter.setSeatId((byte)4);
	//
	// if(battelType == Battle.BATTLE_DUEL){
	// duelTmp[1] = new DuelTmpHPMP(teamLeftCharacter.getId(),
	// teamLeftCharacter.getHP(), teamLeftCharacter.getMP());
	// duelTmp[4] = new DuelTmpHPMP(teamLeftCharacter.getId(),
	// teamLeftCharacter.getHP(), teamLeftCharacter.getMP());
	// }
	// }

	/**
	 * 胜利方添加荣誉值
	 * 
	 * @param winTeam
	 * @param winHonor
	 */
	public void winHonor(ArrayList<IGameCharacter> winTeam, int winHonor) {
		int honor = winHonor * 2 / (winTeam.size());
		for (int index = 0; index < winTeam.size(); index++) {
			RoleBean roleBean = (RoleBean) winTeam.get(index);
			roleBean.setHonor(roleBean.getHonor() + honor);
		}
	}

	/**
	 * 失败方扣除荣誉值，并计算损失荣誉总值
	 * 
	 * @param loseTeam
	 * @return
	 */
	public int loseHonor(List<IGameCharacter> loseTeam) {
		int loseHonor = 0;
		for (int index = 0; index < loseTeam.size(); index++) {
			RoleBean roleBean = (RoleBean) loseTeam.get(index);
			if (roleBean.getNameCatalog() == RoleBean.NAME_RED) {
				roleBean.setHonor(roleBean.getHonor() - 6);
				loseHonor += 6;
			} else {
				roleBean.setHonor(roleBean.getHonor() - 3);
				loseHonor += 3;
			}
		}
		return loseHonor;
	}

	/**
	 * 计算罪恶值
	 * 
	 * @param loseTeam
	 * @return
	 */
	public int countEvil(List<IGameCharacter> loseTeam) {
		int loseEvil = 0;
		for (int index = 0; index < loseTeam.size(); index++) {
			RoleBean roleBean = (RoleBean) loseTeam.get(index);
			if (roleBean.getNameCatalog() == RoleBean.NAME_WHITE) {
				loseEvil += 100;
			}
		}
		return loseEvil;
	}

	/**
	 * 将主动攻击小队中白名人员设置成灰名
	 */
	public void setCatalogGrey() {
		for (int index = 0; index < this.teamRight.size(); index++) {
			RoleBean roleBean = (RoleBean) this.teamRight.get(index);
			if (roleBean.getNameCatalog() == RoleBean.NAME_WHITE) {
				roleBean.setNameCatalog(RoleBean.NAME_GRAY);
			}
		}
	}

	/**
	 * 将主动攻击小队中灰名人员设置成白名
	 */
	public void setCatalogWhite() {
		for (int index = 0; index < this.teamRight.size(); index++) {
			RoleBean roleBean = (RoleBean) this.teamRight.get(index);
			if (roleBean.getNameCatalog() == RoleBean.NAME_GRAY) {
				roleBean.setNameCatalog(RoleBean.NAME_WHITE);
			}
		}
	}

	public Battle(final byte type, final IGameCharacter leftCharacter, final IGameCharacter rightCharacter) {

		final ArrayList<IGameCharacter> teamLeft = new ArrayList<IGameCharacter>();
		final ArrayList<IGameCharacter> teamRight = new ArrayList<IGameCharacter>();
		teamLeft.add(leftCharacter);
		teamRight.add(rightCharacter);
		initBattle(type, teamLeft, teamRight);
	}

	public Battle(final byte type, final ArrayList<IGameCharacter> teamLeftCharacters, final ArrayList<IGameCharacter> teamRightCharacters) {

		initBattle(type, teamLeftCharacters, teamRightCharacters);
	}

	public void initBattle(final byte type, final ArrayList<IGameCharacter> teamLeftCharacters, final ArrayList<IGameCharacter> teamRightCharacters) {
		battelType = type;
		battleStartTime = System.currentTimeMillis();

		// TeamLeft 设置
		if (teamLeftCharacters.size() > 1) {
			for (int i = 0; i < teamLeftCharacters.size(); i++) {
				IGameCharacter fighter = teamLeftCharacters.get(i);

				fighter.setSeatId((byte) i);
			}
		} else if (teamLeftCharacters.size() == 1) {
			teamLeftCharacters.get(0).setSeatId((byte) 1);
		} else {
			Log.error(Log.ERROR, "设置战斗中teamLeft 异常 ====teamLeft size: " + teamLeftCharacters.size() + "====");
		}

		this.teamLeft = teamLeftCharacters;

		// TeamRight 设置
		if (teamRightCharacters.size() > 1) {
			for (int i = 0; i < teamRightCharacters.size(); i++) {
				IGameCharacter fighter = teamRightCharacters.get(i);
				fighter.setSeatId((byte) (i + TEAM_OFFSET));
			}
		} else if (teamRightCharacters.size() == 1) {
			teamRightCharacters.get(0).setSeatId((byte) 4);
		} else {
			Log.error(Log.ERROR, "设置战斗中teamRight 异常 BattleType " + this.battelType + "====teamRight size: " + teamRightCharacters.size() + "====");
		}
		this.teamRight = teamRightCharacters;

		// 如果是决斗 则记录当前血量
		if (battelType == Battle.BATTLE_DUEL) {

			RoleBean teamLeftCharacter = (RoleBean) teamLeftCharacters.get(0);
			RoleBean teamRightCharacter = (RoleBean) teamRightCharacters.get(0);

			duelTmp[1] = new DuelTmpHPMP(teamLeftCharacter.getHP(), teamLeftCharacter.getMP());
			duelTmp[4] = new DuelTmpHPMP(teamRightCharacter.getHP(), teamRightCharacter.getMP());

			teamLeftCharacter.setHP(teamLeftCharacter.getMaxHP());
			teamLeftCharacter.setMP(teamLeftCharacter.getMaxMP());

			teamRightCharacter.setHP(teamRightCharacter.getMaxHP());
			teamRightCharacter.setMP(teamRightCharacter.getMaxMP());
		}

	}

	public final ArrayList<IGameCharacter> getTeamLeft() {
		return teamLeft;
	}

	// private void setTeamLeft(ArrayList<IGameCharacter> teamLeft) {
	//
	// if(this.teamLeft.size() > 0){
	// this.teamLeft.clear();
	// Log.error(Log.ERROR, "危险 清除战斗中左边的玩家");
	// }
	//
	// if(teamLeft.size() > 1){
	// for(int i = 0; i < teamLeft.size(); i ++){
	// IGameCharacter fighter = teamLeft.get(i);
	//
	// fighter.setSeatId((byte)i);
	// }
	// }else if(teamLeft.size() == 1){
	// teamLeft.get(0).setSeatId((byte)1);
	// }else{
	// Log.error(Log.ERROR, "设置战斗中teamRight 异常 ====teamRight size: " +
	// teamRight.size() + "====");
	// }
	//
	// this.teamLeft.addAll(teamLeft);
	// }

	public final ArrayList<IGameCharacter> getTeamRight() {
		return teamRight;
	}

	// private void setTeamRight(ArrayList<IGameCharacter> teamRight) {
	//
	// if(this.teamRight.size() > 0){
	// this.teamRight.clear();
	// Log.error(Log.ERROR, "危险 清除战斗中右边边的玩家");
	// }
	// if(teamRight.size() > 1){
	// for(int i = 0; i < teamRight.size(); i ++){
	// IGameCharacter fighter = teamRight.get(i);
	// // fighter.setBattle(this);
	// fighter.setSeatId((byte)(i + teamOffset));
	// }
	// }else if(teamRight.size() == 1){
	// teamRight.get(0).setSeatId((byte)4);
	// }else{
	// Log.error(Log.ERROR, "设置战斗中teamRight 异常 ====teamRight size: " +
	// teamRight.size() + "====");
	// }
	// this.teamRight.addAll(teamRight);
	// }

	// private void setTeamRight(IGameCharacter gameCharacter) {
	//
	// if(teamRight.size() > 0){
	//
	// Log.error(Log.ERROR, "危险 清除战斗中右边边的玩家");
	//
	// Log.info(Log.STDOUT, "====================打印战斗信息=====================");
	// Log.info(Log.STDOUT, "战斗类型为: " + this.battelType);
	//
	// Log.info(Log.STDOUT, "Team Left: ");
	// for(IGameCharacter igc : getTeamLeft()){
	// Log.error(Log.STDOUT, igc.getNick() + " | " + igc.getHP() + " | " +
	// " 连接是否正常 " + igc.isConnected());
	// }
	//
	// Log.info(Log.STDOUT, "Team Right: ");
	// for(IGameCharacter igc : getTeamRight()){
	// Log.error(Log.STDOUT, igc.getNick() + " | " + igc.getHP() + " | " +
	// " 连接是否正常 " + igc.isConnected());
	// }
	// Log.info(Log.STDOUT, "====================打印战斗信息=====================");
	// teamRight.clear();
	// }
	//
	// this.teamRight.add(gameCharacter);
	//
	// //只有一个人的时候强制设置在中间
	// gameCharacter.setSeatId((byte)4);
	//
	// if(battelType == Battle.BATTLE_DUEL){
	// duelTmp[4] = new DuelTmpHPMP(gameCharacter.getId(),
	// gameCharacter.getHP(), gameCharacter.getMP());
	// }
	// }

	// private void setTeamLeft(IGameCharacter gameCharacter) {
	//
	// if(teamLeft.size() > 0){
	// this.teamLeft.clear();
	// Log.error(Log.ERROR, "危险 清除战斗中左边的玩家");
	// }
	//
	// this.teamLeft.add(gameCharacter);
	// //只有一个人的时候强制设置在中间
	// gameCharacter.setSeatId((byte)1);
	//
	// if(battelType == Battle.BATTLE_DUEL){
	// duelTmp[1] = new DuelTmpHPMP(gameCharacter.getId(),
	// gameCharacter.getHP(), gameCharacter.getMP());
	// }
	// }

	public final int getFighterNum() {
		return teamLeft.size() + teamRight.size();
	}

	public final void broadCastBattleInfo() {
		// 左边有可能是怪也有可能是人
		for (int i = 0; i < teamLeft.size(); i++) {
			teamLeft.get(i).setBattle(this);
		}
		for (int i = 0; i < teamRight.size(); i++) {
			teamRight.get(i).setBattle(this);
		}
	}

	public final void addFightOpration(final IGameCharacter gameCharacter, final UserFightBean opration) {
		fightOprations.put(gameCharacter, opration);
	}

	// 检查战斗中的玩家是否都掉线
	public final boolean isDeadBattle() {

		if (getPlayerNum() <= getOfflinePlayerNum() || System.currentTimeMillis() - battleStartTime > 1800000) {
			Log.info(Log.STDOUT, "====================死锁战斗信息=====================");
			Log.info(Log.STDOUT, "战斗类型为: " + this.battelType);

			Log.info(Log.STDOUT, "Team Left: ");
			for (IGameCharacter igc : getTeamLeft()) {
				Log.info(Log.STDOUT, igc.getNick() + " | " + igc.getHP() + " | " + " 连接是否正常 " + igc.isConnected());
			}

			Log.info(Log.STDOUT, "Team Right: ");
			for (IGameCharacter igc : getTeamRight()) {
				Log.info(Log.STDOUT, igc.getNick() + " | " + igc.getHP() + " | " + " 连接是否正常 " + igc.isConnected());
			}
			Log.info(Log.STDOUT, "====================死锁战斗信息=====================");

			close();
			return true;
		}

		return false;
	}

	/**
	 * 
	 * 
	 * @return
	 */
	private int getPlayerNum() {
		int num = 0;
		// 右边一定是人
		num += teamRight.size();

		// 判断左边是人还是怪
		if (teamLeft.size() > 0 && teamLeft.get(0).getType() == 0) {
			num += teamLeft.size();
		}
		return num;
	}

	/**
	 * @return 已死亡玩家数量
	 */
	@SuppressWarnings("unused")
	@Deprecated
	private int deadPlayerNum() {
		int num = 0;
		// 右边一定是人
		for (int i = 0; i < teamRight.size(); i++) {
			if (teamRight.get(i).getHP() == 0) {
				num++;
			}
		}

		if (teamLeft.size() > 0 && teamLeft.get(0).getType() == 0) {
			for (int i = 0; i < teamLeft.size(); i++) {
				if (teamLeft.get(i).getHP() == 0) {
					num++;
				}
			}
		}
		return num;
	}

	private List<IGameCharacter> getChooseablePlayer() {
		List<IGameCharacter> chooseablePlayer = new ArrayList<IGameCharacter>();
		// 右边一定是人
		for (int i = 0; i < teamRight.size(); i++) {
			IGameCharacter fightMember = teamRight.get(i);
			if (fightMember.isConnected() && fightMember.getHP() > 0) {
				chooseablePlayer.add(fightMember);
			}
		}

		if (teamLeft.size() > 0 && teamLeft.get(0).getType() == 0) {
			for (int i = 0; i < teamLeft.size(); i++) {
				IGameCharacter fightMember = teamLeft.get(i);
				if (fightMember.isConnected() && fightMember.getHP() > 0) {
					chooseablePlayer.add(fightMember);
				}
			}
		}

		return chooseablePlayer;
	}

	/**
	 * @param chooseablePlayer
	 *            可以选择操作的玩家
	 * @return 是否已经选择操作
	 */
	public final boolean isChose(final IGameCharacter chooseablePlayer) {
		return fightOprations.containsKey(chooseablePlayer);
	}

	/**
	 * 判断思路: 这里只需要保证所有可选的玩家已经选择过即可 即 保证在线并且没死的玩家选择过即可
	 * 
	 * @return 参与战斗的成员是否都已经选择操作
	 */
	public final boolean chooseOver() {
		// int playerOperationNum = 0;

		List<IGameCharacter> chooseablePlayers = getChooseablePlayer();

		for (int i = 0; i < chooseablePlayers.size(); i++) {
			IGameCharacter chooseablePlayer = chooseablePlayers.get(i);
			if (!isChose(chooseablePlayer)) {
				return false;
			}
		}

		return true;
	}

	private List<IGameCharacter> getTargetListByDest(final byte[] dest) {
		List<IGameCharacter> targetList = new ArrayList<IGameCharacter>();

		for (int i = 0; i <= 5; i++) {
			if (dest[i] == 1) {
				if (i < TEAM_OFFSET) {
					if (teamLeft.size() > 0) {
						if (teamLeft.size() != 1 && teamLeft.size() > i) {
							targetList.add(teamLeft.get(i));
						} else {
							targetList.add(teamLeft.get(0));
						}
					}
				} else {
					if (teamRight.size() > 0) {
						if (teamRight.size() != 1 && teamRight.size() > (i - TEAM_OFFSET)) {
							targetList.add(teamRight.get(i - TEAM_OFFSET));
						} else {
							targetList.add(teamRight.get(0));
						}
					}
				}
			}
		}
		return targetList;
	}

	private List<Entry<IGameCharacter, UserFightBean>> sortFightOprations() {
		List<Entry<IGameCharacter, UserFightBean>> oprations = new ArrayList<Entry<IGameCharacter, UserFightBean>>(fightOprations.entrySet());

		Collections.sort(oprations, new Comparator<Entry<IGameCharacter, UserFightBean>>() {
			public int compare(final Map.Entry<IGameCharacter, UserFightBean> o1, final Map.Entry<IGameCharacter, UserFightBean> o2) {
				return o2.getKey().getSpeed() - o1.getKey().getSpeed();
			}
		});

		return oprations;
	}

	private List<IGameCharacter> getOffLinePlayerList() {
		List<IGameCharacter> offlinePlayers = new ArrayList<IGameCharacter>();

		// 右边一定是人
		for (int i = 0; i < teamRight.size(); i++) {
			if (!teamRight.get(i).isConnected()) {
				offlinePlayers.add(teamRight.get(i));
			}
		}

		// 判断左边是人还是怪
		if (teamLeft.size() > 0 && teamLeft.get(0).getType() == 0) {
			for (int i = 0; i < teamLeft.size(); i++) {
				if (!teamLeft.get(i).isConnected()) {
					offlinePlayers.add(teamLeft.get(i));
				}
			}
		}

		return offlinePlayers;
	}

	private int getOfflinePlayerNum() {
		return getOffLinePlayerList().size();
	}

	/** 打斗回合开始 */
	public final void fightStart() {
		lastRoundStartTime = System.currentTimeMillis(); // 更新开打时间

		List<RoundData> roundDataList = startRoundFight();
		body.clear();
		body.putInt(0);
		body.put((byte) roundDataList.size());

		for (int i = 0; i < roundDataList.size(); i++) {
			RoundData roundData = roundDataList.get(i);

			body.put(roundData.getType());
			body.put(roundData.getSeatID());
			body.putInt(roundData.getId());
			body.put(roundData.getFightMthod());
			body.putShort(roundData.getfGroupId());
			body.putShort(roundData.getfAnimationId());
			body.putShort(roundData.geteGroupId());
			body.putShort(roundData.geteAnimationId());
			body.putShort(roundData.getbGroupId());
			body.putShort(roundData.getbAnimationId());
			body.putShort(roundData.getDetailLen());
			body.put(roundData.getDetail());
			body.put((byte) roundData.getOtherStateList().size());
			for (int j = 0; j < roundData.getOtherStateList().size(); j++) {
				OtherState state = roundData.getOtherStateList().get(j);
				body.putShort(state.getIcon());
				body.put(state.getStateType());
				body.put(state.getStateMethod());
				body.putShort(state.getStateValue());
				body.putShort(state.getOtherDataLength());
			}
			body.put((byte) roundData.getFightOneList().size());
			for (int k = 0; k < roundData.getFightOneList().size(); k++) {
				FightOne fightOne = roundData.getFightOneList().get(k);
				body.put(fightOne.getFightType());
				body.put(fightOne.getFightSeat());
				body.putShort(fightOne.getFightGroupId());
				body.putShort(fightOne.getFightAnimId());
				body.put(fightOne.getFightValueType());
				body.put(fightOne.getFightValueMethod());
				body.putInt(fightOne.getFightValue());
				body.putShort(fightOne.getOtherDataLength());
			}

			body.putShort(roundData.getOtherData());
		}

		body.putShort((short) 0);

		// 多人战斗要把此数据广播给Battle中所有的玩家
		List<IGameCharacter> playerList = getPlayerList();

		for (int i = 0; i < playerList.size(); i++) {
			if (playerList.get(i) instanceof RoleBean) {
				sendMsg((RoleBean) playerList.get(i), MsgID.MsgID_Fight_Start);
			}
		}

		ArrayList<IGameCharacter> winTeam = isFinish();
		if (winTeam != null) {
			closeBattle(winTeam);
		}
	}

	private String getBonusInfo(final BonusData data, ArrayList<IGameCharacter> winTeam) {
		StringBuffer lootInfo = new StringBuffer();

		for (int i = 0; i < data.getModifyDataList().size(); i++) {
			if (data.getModifyDataList().get(i).getModifyType() == 0) {
				if (lootInfo.length() > 0) {
					lootInfo.append("/");
				}
				if (data.getModifyDataList().get(i).getModifyAct() == 0) {
					lootInfo.append("获得经验：" + data.getModifyDataList().get(i).getModifyValue());
				} else {
					lootInfo.append("失去经验：" + data.getModifyDataList().get(i).getModifyValue());
				}
			} else {
				if (lootInfo.length() > 0) {
					lootInfo.append("/");
				}
				if (data.getModifyDataList().get(i).getModifyAct() == 0) {
					lootInfo.append("获得金币: " + LuaService.call4String("getValueDescribe", data.getModifyDataList().get(i).getModifyValue() + this.increaseFatwaGold));
				} else {
					lootInfo.append("失去金币：" + LuaService.call4String("getValueDescribe", data.getModifyDataList().get(i).getModifyValue()));
				}
			}
		}

		if (data.getTaskprop() != null) {
			lootInfo.append("/" + data.getTaskprop());
		}

		for (ModifyItemData itemData : data.getItemDataList()) {
			if (itemData.getModifyAct() == 0) {
				lootInfo.append("/得到 ");
			} else {
				lootInfo.append("/失去 ");
			}
			lootInfo.append(itemData.getItem().getName() + "×" + itemData.getItem().getStorage());
		}

		if (lootInfo.length() == 0) {
			lootInfo.append("你没有得到任何奖励...");
		}
		return lootInfo.toString();
	}

	/**
	 * 战斗结算
	 * 
	 * @param winTeam
	 *            战斗胜利的队伍
	 */
	private void closeBattle(final ArrayList<IGameCharacter> winTeam) {
		try {
			this.setCatalogGrey();
			Map<IGameCharacter, BonusData> bonus = getBonus(winTeam);
			this.setCatalogWhite();
			if (battelType == BATTLE_DUEL) {
				for (int i = 0; i < getPlayerList().size(); i++) {
					IGameCharacter iRole = getPlayerList().get(i);
					if (iRole.getSeatId() != -1) {
						((RoleBean) iRole).setHP(duelTmp[iRole.getSeatId()].getHP());
						((RoleBean) iRole).setMP(duelTmp[iRole.getSeatId()].getMP());
					}
				}
			}

			reliveAllAfterBattle();
			Iterator<Entry<IGameCharacter, BonusData>> it = bonus.entrySet().iterator();
			int point = 0;
			RoleBean r = null;
			String msg = null;
			TaskState ts = null;
			while (it.hasNext()) {
				Entry<IGameCharacter, BonusData> entry = it.next();
				// 修补从onlinemap中取不到玩家对象的BUG
				RoleBean role = (RoleBean) entry.getKey();

				ts = role.getTasks().getTaskState(24000);
				int increaseExp = 0;
				if (battelType == BATTLE_PVP) {

					if (winTeam.contains(entry.getKey())) {
						this.increaseFatwaGold = this.fatwaGold(winTeam);
					}
				}

				// 给玩家加经验 加钱
				String bonusInfo = "";
				if (ts == null || (ts != null && ts.isFinished())) {
					for (int i = 0; i < entry.getValue().getModifyDataList().size(); i++) {
						ModifyData modifyData = entry.getValue().getModifyDataList().get(i);
						if (modifyData.getModifyType() == 0) {
							if (modifyData.getModifyAct() == 0) {
								increaseExp = modifyData.getModifyValue();
							} else {
								increaseExp = -modifyData.getModifyValue();
							}
						} else {
							if (modifyData.getModifyAct() == 0) {
								role.increaseGold(modifyData.getModifyValue() + this.increaseFatwaGold);

								Log.info(Log.ITEM,
										role.getUserid() + "#$" + role.getRoleid() + "#$14#$没有获得任务物品#$物品个数为0#$" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())
												+ "#$" + TianLongServer.srvId + "#$" + modifyData.getModifyValue() + "");

							} else {
								role.decreaseGold(modifyData.getModifyValue());

								Log.info(Log.ITEM,
										role.getUserid() + "#$" + role.getRoleid() + "#$15#$没有获得任务物品#$物品个数为0#$" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())
												+ "#$" + TianLongServer.srvId + "#$" + modifyData.getModifyValue() + "");

							}
						}
					}

					bonusInfo = getBonusInfo(entry.getValue(), winTeam);
				}
				// 如果有药瓶则恢复血
				if (role.hasBuff(BuffType.AFTER_BATTLE_HP.getId())) {

					Buff buff = role.getBuff(BuffType.AFTER_BATTLE_HP.getId());
					int costHp = role.getMaxHP() - role.getHP();
					if (costHp < 0) {
						costHp = 0;
					}
					int addedHp = role.fixValueAfterBuff(BuffType.AFTER_BATTLE_HP.getId(), costHp);
					role.increaseHP(addedHp);

					bonusInfo += "/自动恢复" + addedHp + "点血, 药品剩余" + buff.getEffectValue() + "点";
				}

				if (role.hasBuff(BuffType.AFTER_BATTLE_MP.getId())) {
					Buff buff = role.getBuff(BuffType.AFTER_BATTLE_MP.getId());
					int costMp = role.getMaxMP() - role.getMP();
					if (costMp < 0) {
						costMp = 0;
					}
					int addedMp = role.fixValueAfterBuff(BuffType.AFTER_BATTLE_MP.getId(), costMp);
					role.increaseMP(addedMp);

					bonusInfo += "/自动恢复" + addedMp + "点内力, 药品剩余" + buff.getEffectValue() + "点";
				}

				if (battelType == Battle.BATTLE_PVE) {
					msg = bonusInfo;
				} else if (battelType == Battle.BATTLE_DUEL) {
					msg = LuaService.call4String("duelOver", entry.getKey(), winTeam.contains(entry.getKey()));
				} else if (battelType == Battle.BATTLE_PVP) {
					
					if (winTeam.contains(entry.getKey())) {
						if (ts != null && !ts.isFinished() && isInHegemonyMap(role.getCoords().getMap())) {
							Hegemony h = OnlineService.getHegemonys(role.getId());
							if (point > 0) {
								Log.info((byte) 0, role.getName() + "当前：" + h.getPoints());
								h.setPoints(h.getPoints() + point);
								Log.info((byte) 0, role.getName() + "剩余：" + h.getPoints());
								ServerMessage.sendSysPrompt(role, "当前争霸积分(" + h.getPoints() + ")");
							} else {
								r = role;
							}

						}
						msg = "战斗胜利/";
					} else {

						if (ts != null && !ts.isFinished() && isInHegemonyMap(role.getCoords().getMap())) {
							Hegemony h = OnlineService.getHegemonys(role.getId());
							Log.info((byte) 0, role.getName() + "当前：" + h.getPoints());
							int decreate = h.getPoints();
							decreate = (decreate >= 50) ? (decreate / 2) : decreate;
							h.setPoints(h.getPoints() - decreate);
							Log.info((byte) 0, role.getName() + "剩余：" + h.getPoints());
							if (r == null) {
								point = decreate;
							} else {
								Hegemony hWin = OnlineService.getHegemonys(r.getId());
								hWin.setPoints(hWin.getPoints() + decreate + getRandomNum());
								ServerMessage.sendSysPrompt(hWin.getHegemony(), "当前争霸积分(" + hWin.getPoints() + ")");
							}
							ServerMessage.sendSysPrompt(role, "当前争霸积分(" + h.getPoints() + ")");
							if (h.getPoints() <= 0) {
								OnlineService.failOutHegemony(h);
							}
						}

						msg = "战斗失败/";

					}
					msg += bonusInfo;
				}

				// else if(winTeam.contains(entry.getKey()))
				// msg = AgentProxy.getBytesOfUTF8("战斗胜利");
				// else
				// msg = AgentProxy.getBytesOfUTF8("战斗失败");

				prepareData();
				putString(msg);
				putInt(0);
				putShort((short) 0);
				sendMsg(role, MsgID.MsgID_Fight_Closing);
				body.clear();
				body.putInt(0);
				if (LuaService.call4Bool("tryUpgrade", role, increaseExp)) {
					LuaService.callLuaFunction("fillFreshNPCState", role);
				}

				// 更新人物属性 和 通知 获得新的物品
				for (ModifyItemData itemData : entry.getValue().getItemDataList()) {
					if (itemData.getModifyAct() == 0) {
						Log.info(Log.ITEM, role.getUserid() + "#$" + role.getRoleid() + "#$17#$" + itemData.getItem().getName() + "#$" + itemData.getItem().getStorage() + "#$"
								+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "#$" + TianLongServer.srvId + "#$" + "0" + "#$" + itemData.getItem().getTid()
								+ "#$" + itemData.getItem().getUid());
						LuaService.callLuaFunction("fillBagAdd", itemData.getItem());
					} else {
						// 添加身上物品掉落函数
						Log.info(Log.ITEM, role.getUserid() + "#$" + role.getRoleid() + "#$18#$" + itemData.getItem().getName() + "#$" + itemData.getItem().getStorage() + "#$"
								+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "#$" + TianLongServer.srvId + "#$" + "0" + "#$" + itemData.getItem().getTid()
								+ "#$" + itemData.getItem().getUid());
						LuaService.callLuaFunction("fillInuseDel", itemData.getItem());
					}
				}

				// LuaService.callLuaFunction("fillPrompt", msg);

				body.putShort((short) 0);

				sendMsg(role, MsgID.MsgID_Special_Train);
			}
			if (battelType == Battle.BATTLE_PVP) {
				if (ts == null || (ts != null && ts.isFinished())) {
					this.passivityAddEnemys(winTeam);
					this.fatwaAccounts(winTeam);
				}
			}

		} catch (Exception e) {
			Log.error(Log.ERROR, "closeBattle", e);
		} finally {
			// 防止内存泄露
			// 关闭战斗
			close();
		}

	}

	/**
	 * 玩家是否在狮王争霸地图中
	 * 
	 * @param mapId
	 *            玩家所在地图
	 * @return 是或者否
	 */
	public boolean isInHegemonyMap(int mapId) {
		if (mapId == 193 || mapId == 203 || mapId == 204 || mapId == 205) {
			return true;
		}
		return false;
	}

	/**
	 * 随机0-9，返回
	 * 
	 * @return 0-9随机的一个数
	 */
	public int getRandomNum() {
		Random r = new Random();
		return r.nextInt(9);
	}

	/**
	 * 
	 * @function 被动加入仇人列表
	 * @author LuoSR
	 * @date 2011-12-20
	 */
	public void passivityAddEnemys(ArrayList<IGameCharacter> winTeam) {
		if (winTeam == teamRight) {
			// 被动加入仇人列表
			for (int i = 0; i < teamLeft.size(); i++) {
				RoleBean leftRole = OnlineService.getOnline(teamLeft.get(i).getId());
				for (int j = 0; j < teamRight.size(); j++) {
					RoleBean rightRole = OnlineService.getOnline(teamRight.get(j).getId());
					// 判断偷袭的人是否在仇人列表
					if (leftRole.getEnemys().contains(rightRole.getRoleid())) {
						leftRole.getEnemys().remove((Integer) rightRole.getRoleid());
					}

					// 判断列表是否已满 --50人满
					if (leftRole.getEnemys().size() >= 50) {
						// 从列表中删除最早加入的仇人
						leftRole.getEnemys().remove(0);
					}
					// 向列表加入刚刚杀死自己的仇人
					leftRole.getEnemys().add(rightRole.getRoleid());
				}
			}
		}
	}

	/**
	 * @function 追杀令结算
	 * @author LuoSR
	 * @date 2011-12-20
	 */
	public void fatwaAccounts(ArrayList<IGameCharacter> winTeam) {
		TreeMap<Integer, Fatwa> fatwas = FatwaTable.INSTANCE.getFatwas();

		if (winTeam == teamRight) {
			String rightRoles = "";
			String rightLevel = "";
			for (IGameCharacter rightRole : teamRight) {
				rightRoles += rightRole.getId() + ",";
				rightLevel += rightRole.getLevel() + ",";
			}
			for (int i = 0; i < teamLeft.size(); i++) {
				RoleBean leftRole = OnlineService.getOnline(teamLeft.get(i).getId());

				// 判断是否被追杀
				if (leftRole.getIsNotFatwa()) {
					try {
						Log.info(
								Log.FATWA,
								new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "#$" + 1 + "#$" + fatwas.get(leftRole.getRoleid()).getPromulgatorId() + "#$"
										+ fatwas.get(leftRole.getRoleid()).getPromulgatorName() + "#$#$" + fatwas.get(leftRole.getRoleid()).getRoleIdByFatwa() + "#$"
										+ fatwas.get(leftRole.getRoleid()).getTimeOut() + "#$#$" + rightRoles + "#$" + rightLevel + "#$" + TianLongServer.srvId);
					} catch (Exception e) {
						e.printStackTrace();
					}
					// 扣除被追杀的人的经验
					leftRole.setEXP((int) (leftRole.getEXP() * 0.95));
					// 去掉“杀”字图标
					leftRole.delKillIcon();
					// 发送邮件
					MailManager.getInstance().sendSysMail(fatwas.get(leftRole.getRoleid()).getPromulgatorId(), "武林追杀令", "追杀成功", 0, null);
					// 互相删除追杀状态
					fatwas.remove(leftRole.getRoleid());
					leftRole.setIsNotFatwa(false);
				}
			}
		} else {

			String leftRoles = "";
			String leftLevel = "";
			for (IGameCharacter leftRole : teamRight) {
				leftRoles += leftRole.getId() + ",";
				leftLevel += leftRole.getLevel() + ",";
			}
			for (int j = 0; j < teamRight.size(); j++) {
				RoleBean rightRole = OnlineService.getOnline(teamRight.get(j).getId());
				// 判断是否被追杀
				if (rightRole.getIsNotFatwa()) {
					try {
						Log.info(Log.FATWA, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "#$" + 1 + "#$" + fatwas.get(rightRole.getRoleid()).getPromulgatorId()
								+ "#$" + fatwas.get(rightRole.getRoleid()).getPromulgatorName() + "#$#$" + fatwas.get(rightRole.getRoleid()).getRoleIdByFatwa() + "#$"
								+ fatwas.get(rightRole.getRoleid()).getTimeOut() + "#$#$" + leftRoles + "#$" + leftLevel + "#$" + TianLongServer.srvId);
					} catch (Exception e) {
						e.printStackTrace();
					}
					// 扣除被追杀的人的经验
					rightRole.setEXP((int) (rightRole.getEXP() * 0.95));
					// 去掉“杀”字图标
					rightRole.delKillIcon();

					// 发送邮件
					MailManager.getInstance().sendSysMail(fatwas.get(rightRole.getRoleid()).getPromulgatorId(), "武林追杀令", "追杀成功", 0, null);
					// 互相删除追杀状态
					fatwas.remove(rightRole.getRoleid());
					rightRole.setIsNotFatwa(false);
				}
			}
		}
	}

	/**
	 * @function 追杀令奖金
	 * @author LuoSR
	 * @return int
	 * @date 2011-12-28
	 */
	public int fatwaGold(ArrayList<IGameCharacter> winTeam) {
		int gold = 0;
		if (winTeam == teamRight) {
			for (int i = 0; i < teamLeft.size(); i++) {
				RoleBean leftRole = OnlineService.getOnline(teamLeft.get(i).getId());
				// 判断是否被追杀
				if (leftRole.getIsNotFatwa()) {
					// 追杀发放奖金
					int[] incrementGold = new int[] { 30000, 60000, 180000, 360000 };
					if (leftRole.getLevel() >= 60 && leftRole.getLevel() < 80) {
						gold = gold + incrementGold[0] / teamRight.size();
					} else if (leftRole.getLevel() < 90) {
						gold = gold + incrementGold[1] / teamRight.size();
					} else if (leftRole.getLevel() < 100) {
						gold = gold + incrementGold[2] / teamRight.size();
					} else if (leftRole.getLevel() >= 100) {
						gold = gold + incrementGold[3] / teamRight.size();
					}
				}
			}
			return gold;
		} else if (winTeam == teamLeft) {
			for (int j = 0; j < teamRight.size(); j++) {
				RoleBean rightRole = OnlineService.getOnline(teamRight.get(j).getId());
				// 判断是否被追杀
				if (rightRole.getIsNotFatwa()) {
					// 追杀发放奖金
					int[] incrementGold = new int[] { 30000, 60000, 180000, 360000 };
					if (rightRole.getLevel() >= 60 && rightRole.getLevel() < 80) {
						gold = gold + incrementGold[0] / teamLeft.size();
					} else if (rightRole.getLevel() < 90) {
						gold = gold + incrementGold[1] / teamLeft.size();
					} else if (rightRole.getLevel() < 100) {
						gold = gold + incrementGold[2] / teamLeft.size();
					} else if (rightRole.getLevel() >= 100) {
						gold = gold + incrementGold[3] / teamLeft.size();
					}
				}
			}
			return gold;
		} else {
			return gold;
		}
	}

	final List<IGameCharacter> getPlayerList() {
		List<IGameCharacter> playerList = new ArrayList<IGameCharacter>();
		if (teamRight != null) {
			playerList.addAll(teamRight);
		}

		if (teamLeft != null && teamLeft.size() > 0 && teamLeft.get(0).getType() == 0) {
			playerList.addAll(teamLeft);
		}

		return playerList;
	}

	public final List<RoundData> startRoundFight() {

		List<RoundData> roundList = new ArrayList<RoundData>();

		// 如果左边是怪将根据怪物AI生成怪的操作
		if (teamLeft.size() > 0 && teamLeft.get(0).getType() == 1) {
			monsterAI(teamLeft);
		}

		// List<IGameCharacter> offlinePlayers = getOffLinePlayerList();
		// if(offlinePlayers != null && offlinePlayers.size() != 0)
		// offLinePlayerAI(offlinePlayers);

		// 按出手速度排序
		List<Entry<IGameCharacter, UserFightBean>> oprations = sortFightOprations();
		for (int i = 0; i < oprations.size(); i++) {

			// 容错判断 防止玩家死亡后仍能攻击
			if (oprations.get(i).getKey().getHP() > 0) {
				RoundData roundData = new RoundData();
				IGameCharacter operator = oprations.get(i).getKey();
				UserFightBean opration = oprations.get(i).getValue();

				/** 选择的攻击类型 0.普通 1.技能 2.物品使用 3.逃跑 */
				byte atkType = opration.getAtkType();
				switch (atkType) {
				case 0:
					physicalAtk(roundData, operator, opration);
					break;
				case 1:
					skillAtk(roundData, operator, opration);
					break;
				case 2:
					useItem(roundData, operator, opration);
					break;
				case 3:
					// 逃跑什么也不做
					doNothing(roundData, operator, opration);
					break;
				default:
					doNothing(roundData, operator, opration);
				}
				roundList.add(roundData);
			}
		}

		fightOprations.clear();
		return roundList;
	}

	private List<IGameCharacter> getPlayerTeam(final IGameCharacter operator) {

		for (int i = 0; i < teamLeft.size(); i++) {
			if (teamLeft.get(i).getId() == operator.getId()) {
				return teamLeft;
			}
		}

		for (int i = 0; i < teamRight.size(); i++) {
			if (teamRight.get(i).getId() == operator.getId()) {
				return teamRight;
			}
		}

		return null;
	}

	// 防止客户端传过来非法的目标
	// 比如攻击同队伍目标
	// 目标修正
	private void checkPhyAtkTarget(final IGameCharacter operator, final List<IGameCharacter> targetList) {

		List<IGameCharacter> operatorTeam = getPlayerTeam(operator);

		Iterator<IGameCharacter> it = targetList.iterator();

		while (it.hasNext()) {
			IGameCharacter target = it.next();

			for (int i = 0; i < operatorTeam.size(); i++) {
				if (operatorTeam.get(i).getId() == target.getId()) {
					it.remove();
				}
			}
		}

		if (targetList.size() == 1 && targetList.get(0).getHP() == 0) {
			IGameCharacter invalidTarget = targetList.remove(0); // 移除非法目标
			// 对目标进行修正
			IGameCharacter validTarget = getTarget(getPlayerTeam(invalidTarget));
			if (validTarget != null) {
				targetList.add(validTarget);
			}
		}

	}

	private void physicalAtk(final RoundData roundData, final IGameCharacter operator, final UserFightBean opration) {
		List<IGameCharacter> targetList = getTargetListByDest(opration.getDest());

		checkPhyAtkTarget(operator, targetList);
		// 考虑击中 暴击 miss 躲闪等效果
		List<FightOne> fightList = operator.physicalAttack(targetList);

		// 完成伤害结算 开始填充协议数据
		roundData.setType(operator.getType());

		roundData.setSeatID(operator.getSeatId());
		roundData.setId(operator.getId());

		roundData.setFightMthod((byte) 1);
		// if(operator.getVocation() != IGameCharacter.SHAQ)
		// roundData.fightMthod = 0;
		// else
		// roundData.fightMthod = 1;

		List<Number> effect = LuaService.call4List("phyFightEffect", operator);
		roundData.setfGroupId((effect.get(0)).shortValue());
		if (teamLeft.contains(operator) && operator.getType() == 0) {
			roundData.setfAnimationId((short) ((effect.get(1)).shortValue() + 1));
		} else {
			roundData.setfAnimationId((effect.get(1)).shortValue());
		}

		// roundData.fAnimationId = ((Number)effect.get(1)).shortValue();
		roundData.seteGroupId((effect.get(2)).shortValue());
		roundData.seteAnimationId((effect.get(3)).shortValue());
		roundData.setbGroupId((effect.get(4)).shortValue());
		roundData.setbAnimationId((effect.get(5)).shortValue());

		byte[] msg = AgentProxy.getUTF8("nice short");
		roundData.setDetailLen((short) msg.length);
		roundData.setDetail(msg);

		roundData.setFightOneList(fightList);

	}

	private void skillAtk(final RoundData roundData, final IGameCharacter operator, final UserFightBean opration) {
		List<IGameCharacter> targetList = getTargetListByDest(opration.getDest());

		List<FightOne> fightList = operator.skillAttack(opration.getAppID(), targetList);

		// 完成伤害结算 开始填充协议数据
		roundData.setType(operator.getType());

		roundData.setSeatID(operator.getSeatId());
		roundData.setId(operator.getId());

		roundData.setFightMthod((byte) 1);

		List<Number> effect = LuaService.call4List("skillFightEffect", operator, opration.getAppID());

		if (effect != null && effect.size() > 0) {
			roundData.setfGroupId(((Number) effect.get(0)).shortValue());
			if (teamLeft.contains(operator) && operator.getType() == 0) {
				roundData.setfAnimationId((short) ((effect.get(1)).shortValue() + 1));
			} else {
				roundData.setfAnimationId((effect.get(1)).shortValue());
			}

			// roundData.fAnimationId = ((Number)effect.get(1)).shortValue();
			roundData.seteGroupId((effect.get(2)).shortValue());
			roundData.seteAnimationId((effect.get(3)).shortValue());
			roundData.setbGroupId((effect.get(4)).shortValue());
			roundData.setbAnimationId((effect.get(5)).shortValue());
		}

		byte[] msg = AgentProxy.getUTF8("nice short");
		roundData.setDetailLen((short) msg.length);
		roundData.setDetail(msg);

		roundData.getFightOneList().addAll(fightList);

	}

	private void useItem(final RoundData roundData, final IGameCharacter operator, final UserFightBean opration) {

		List<IGameCharacter> targetList = getTargetListByDest(opration.getDest());

		List<FightOne> fightList = operator.fightItemUse(opration.getAppID(), targetList);

		// 完成伤害结算 开始填充协议数据
		roundData.setType(operator.getType());

		roundData.setSeatID(operator.getSeatId());
		roundData.setId(operator.getId());

		roundData.setFightMthod((byte) 0);

		List<Number> effect = LuaService.call4List("itemUseEffect", operator, opration.getAppID());

		if (effect != null && effect.size() > 0) {
			roundData.setfGroupId((effect.get(0)).shortValue());
			if (teamLeft.contains(operator) && operator.getType() == 0) {
				roundData.setfAnimationId((short) ((effect.get(1)).shortValue() + 1));
			} else {
				roundData.setfAnimationId((effect.get(1)).shortValue());
			}

			// roundData.fAnimationId = ((Number)effect.get(1)).shortValue();
			roundData.seteGroupId((effect.get(2)).shortValue());
			roundData.seteAnimationId((effect.get(3)).shortValue());
			roundData.setbGroupId((effect.get(4)).shortValue());
			roundData.setbAnimationId((effect.get(5)).shortValue());
		}

		byte[] msg = AgentProxy.getUTF8("nice short");
		roundData.setDetailLen((short) msg.length);
		roundData.setDetail(msg);

		roundData.getFightOneList().addAll(fightList);
	}

	private void doNothing(final RoundData roundData, final IGameCharacter operator, final UserFightBean opration) {
		roundData.setType(operator.getType());

		roundData.setSeatID(operator.getSeatId());
		roundData.setId(operator.getId());

		roundData.setFightMthod((byte) 2);

		List<Number> effect = LuaService.call4List("phyFightEffect", operator);
		roundData.setfGroupId((effect.get(0)).shortValue());
		roundData.setfAnimationId((effect.get(1)).shortValue());
		roundData.seteGroupId((effect.get(2)).shortValue());
		roundData.seteAnimationId((effect.get(3)).shortValue());
		roundData.setbGroupId((effect.get(4)).shortValue());
		roundData.setbAnimationId((effect.get(5)).shortValue());

		byte[] msg = AgentProxy.getUTF8("逃跑失败");
		roundData.setDetailLen((short) msg.length);
		roundData.setDetail(msg);

	}

	private List<IGameCharacter> getEnemyTeam(final IGameCharacter character) {
		return teamLeft.contains(character) ? teamRight : teamLeft;
	}

	private List<IGameCharacter> validTargets(final List<IGameCharacter> team) {
		List<IGameCharacter> validTargets = new ArrayList<IGameCharacter>();
		for (int i = 0; i < team.size(); i++) {
			IGameCharacter teamate = team.get(i);
			if (teamate.getHP() > 0) {
				validTargets.add(teamate);
			}
		}

		return validTargets;
	}

	private IGameCharacter getTarget(final List<IGameCharacter> team) {
		IGameCharacter target = null;

		List<IGameCharacter> targets = validTargets(team);
		if (targets.size() > 0) {
			int targetIndex = 0;
			if (targets.size() > 1) {
				targetIndex = random.nextInt(targets.size());
			}

			target = targets.get(targetIndex);
		}
		return target;
	}

	@SuppressWarnings("unused")
	private void doNothingAI(final List<IGameCharacter> characterList) {
		for (int i = 0; i < characterList.size(); i++) {
			UserFightBean operation = new UserFightBean();
			operation.setAtkType((byte) 10); // 走default的doNothing逻辑
			addFightOpration(characterList.get(i), operation);
		}
	}

	private void commonAI(final List<IGameCharacter> characterList) {
		for (int i = 0; i < characterList.size(); i++) {

			if (characterList.get(i).getHP() > 0) {
				UserFightBean operation = new UserFightBean();
				operation.setAtkType((byte) 0);

				List<IGameCharacter> enemyTeam = getEnemyTeam(characterList.get(i));

				IGameCharacter target = getTarget(enemyTeam);

				if (target != null) {
					operation.setDest(target.getSeatId());

					addFightOpration(characterList.get(i), operation);
				} else {
					Log.error(Log.STDOUT, "commonAI", "no target found!");
				}
			}
		}
	}

	public final void offLinePlayerAI(final IGameCharacter player) {
		List<IGameCharacter> characterList = new ArrayList<IGameCharacter>();
		characterList.add(player);
		commonAI(characterList);
	}

	private void monsterAI(final List<IGameCharacter> monsterList) {
		commonAI(monsterList);
	}

	// 为了方便 这里顺便返回胜利的队伍
	final ArrayList<IGameCharacter> isFinish() {
		for (int i = 0; i < teamLeft.size(); i++) {
			if (teamLeft.get(i).getHP() > 0) {
				break;
			} else if (i == (teamLeft.size() - 1)) {
				// 所有队伍成员血量都为0
				return teamRight;
			}
		}

		for (int i = 0; i < teamRight.size(); i++) {
			if (teamRight.get(i).getHP() > 0) {
				break;
			} else if (i == (teamRight.size() - 1)) {
				return teamLeft;
			}
		}

		return null;
	}

	private List<Item> getBonusLoot() {

		List<Item> items = new ArrayList<Item>();

		// 这里暂时写死只会生成一样物品
		if (lootList.size() > 0) {
			int lootIndex = random.nextInt(lootList.size());
			Item item = lootList.get(lootIndex);

			if (items.contains(item)) {
				Item same = items.get(items.indexOf(item));
				same.setStorage((short) (same.getStorage() + item.getStorage()));
			} else {
				items.add(item);
			}

			lootList.remove(lootIndex);
		}

		return items;
	}

	private int[] getBonusExpGold() {
		// 打怪才会调用此方法 先判断左边是否为怪

		int exp = 0;
		int gold = 0;

		if (teamLeft.size() > 0 && teamLeft.get(0).getType() == 1) {
			for (int i = 0; i < teamLeft.size(); i++) {
				exp += LuaService.call4Int("getMonsterExp", teamLeft.get(i).getId());
				gold += LuaService.call4Int("getMonsterGold", teamLeft.get(i).getId());
			}
		}

		int[] expGold = { exp, gold };
		return expGold;
	}

	/** 计算队伍等级（即等级最高角色的等级） **/
	private int getHighLevel(final List<IGameCharacter> team) {
		int highLv = 0;
		for (int i = 0; i < team.size(); i++) {
			if (team.get(i).getLevel() > highLv) {
				highLv = team.get(i).getLevel();
			}
		}

		return highLv;
	}

	/**
	 * @function 得到按等级排序的角色
	 * @author LuoSR
	 * @date 2011-12-20
	 */
	private List<RoleBean> roleList(final List<IGameCharacter> team) {
		RoleBean[] roleBeans = new RoleBean[team.size()];
		for (int i = 0; i < team.size(); i++) {
			roleBeans[i] = (RoleBean) team.get(i);
		}
		RoleBean middleLvRole = null;

		for (int i = 0; i < roleBeans.length - 1; i++) {
			for (int j = i + 1; j < roleBeans.length; j++) {
				if (roleBeans[i].getLevel() > roleBeans[j].getLevel()) {
					middleLvRole = roleBeans[i];
					roleBeans[i] = roleBeans[j];
					roleBeans[j] = middleLvRole;
				}
			}
		}
		List<RoleBean> beans = new ArrayList<RoleBean>(Arrays.asList(roleBeans));
		return beans;
	}

	private ModifyData caculExp(final IGameCharacter member, final ArrayList<IGameCharacter> team, final int monsterAvgLv, final int totalExp) {
		ModifyData modifyExp = new ModifyData(0);

		int refLV = monsterAvgLv - member.getLevel();
		int refExp = totalExp / team.size();
		// 单人打怪
		if (team.size() == 1) {

			if (this.singleHunt(refLV, refExp) < 1) {
				modifyExp.setModifyValue(1);
			} else {
				modifyExp.setModifyValue((int) (this.singleHunt(refLV, refExp)));
			}
			// 俩人打怪0
		} else if (team.size() == 2) {

			if (this.doubleHunt(member, refLV, refExp, team, monsterAvgLv) < 1) {
				modifyExp.setModifyValue(1);
			} else {
				modifyExp.setModifyValue((int) (this.doubleHunt(member, refLV, refExp, team, monsterAvgLv)));
			}
			// 三人打怪
		} else if (team.size() == 3) {

			if (this.trioHunt(member, refLV, refExp, team, monsterAvgLv) < 1) {
				modifyExp.setModifyValue(1);
			} else {
				modifyExp.setModifyValue((int) (this.trioHunt(member, refLV, refExp, team, monsterAvgLv)));
			}

		}

		if (member.hasBuff(BuffType.EXP.getId())) {
			modifyExp.setModifyValue(member.fixValueAfterBuff(BuffType.EXP.getId(), modifyExp.getModifyValue()));
		}

		if (member instanceof RoleBean) {
			RoleBean role = (RoleBean) member;
			Gang gang = GangService.INSTANCE.getGang(role.getGangid());

			if (gang != null) {
				modifyExp.setModifyValue(gang.benefit(Benefit.EXP, modifyExp.getModifyValue()));
			}
		}

		// 夫妻师徒经验加成

		if (team.size() == 2) {
			int role1 = team.get(0).getId();
			int role2 = team.get(1).getId();

			if (MarryMasterAddExpGold.twoIsMarry(role1, role2)) {
				modifyExp.setModifyValue((int) (modifyExp.getModifyValue() * 1.05));
			}
			if (MarryMasterAddExpGold.twoIsMaster(role1, role2)) {
				modifyExp.setModifyValue((int) (modifyExp.getModifyValue() * 1.05));
			}
		} else if (team.size() == 3) {
			int role1 = team.get(0).getId();
			int role2 = team.get(1).getId();
			int role3 = team.get(2).getId();
			if (MarryMasterAddExpGold.threeIsMarry(role1, role2, role3)) {
				modifyExp.setModifyValue((int) (modifyExp.getModifyValue() * 1.05));
			}
			if (MarryMasterAddExpGold.threeIsAllMaster(role1, role2, role3)) {
				modifyExp.setModifyValue((int) (modifyExp.getModifyValue() * 1.1));
			} else if (MarryMasterAddExpGold.threeIsMaster(role1, role2, role3)) {
				modifyExp.setModifyValue((int) (modifyExp.getModifyValue() * 1.05));
			}

		}

		return modifyExp;
	}

	/**
	 * @function 单人打怪返回单人应得到的经验
	 * @author LuoSR
	 * @date 2012-1-4
	 */
	private int singleHunt(int refLV, int refExp) {
		int exp = 0;
		if (refLV < -10) {
			exp = (int) (refExp * 0.01);
		} else if (refLV >= -10 && refLV <= -7) {
			exp = (int) (refExp * 0.2);
		} else if (refLV > -7 && refLV <= -3) {
			exp = (int) (refExp * 0.6);
		} else if (refLV > -3 && refLV <= -1) {
			exp = (int) (refExp * 0.8);
		} else if (refLV == 0) {
			exp = (int) (refExp);
		} else if (refLV >= 1 && refLV <= 3) {
			exp = (int) (refExp * 1.2);
		} else if (refLV > 3 && refLV <= 7) {
			exp = (int) (refExp * 0.6);
		} else if (refLV > 7 && refLV <= 10) {
			exp = (int) (refExp * 0.3);
		} else if (refLV > 10) {
			exp = 1;
		}
		return exp;
	}

	/**
	 * @function 俩人打怪返回单人应得到的经验
	 * @author LuoSR
	 * @date 2012-1-4
	 */
	private int doubleHunt(final IGameCharacter member, int refLV, int refExp, ArrayList<IGameCharacter> team, final int monsterAvgLv) {
		int exp = this.singleHunt(refLV, refExp);
		int doubleExp = 0;
		int minLv = roleList(team).get(0).getLevel();
		int maxLv = roleList(team).get(1).getLevel();

		if (member.getId() == roleList(team).get(0).getId()) {
			if (monsterAvgLv < minLv) {
				if (minLv != maxLv) {
					doubleExp = (int) (exp * 1.2 * 0.55);
				} else {
					doubleExp = (int) (exp * 1.2 * 0.50);
				}
			} else if (monsterAvgLv < maxLv) {
				if (minLv != maxLv) {
					doubleExp = (int) (exp * 1.2 * 0.50);
				} else {
					doubleExp = (int) (exp * 1.2 * 0.50);
				}
			} else {
				if (minLv != maxLv) {
					doubleExp = (int) (exp * 1.2 * 0.45);
				} else {
					doubleExp = (int) (exp * 1.2 * 0.50);
				}
			}
		} else {
			if (monsterAvgLv < minLv) {
				if (minLv != maxLv) {
					doubleExp = (int) (exp * 1.2 * 0.45);
				} else {
					doubleExp = (int) (exp * 1.2 * 0.50);
				}
			} else if (monsterAvgLv < maxLv) {
				if (minLv != maxLv) {
					doubleExp = (int) (exp * 1.2 * 0.50);
				} else {
					doubleExp = (int) (exp * 1.2 * 0.50);
				}
			} else {
				if (minLv != maxLv) {
					doubleExp = (int) (exp * 1.2 * 0.55);
				} else {
					doubleExp = (int) (exp * 1.2 * 0.50);
				}
			}
		}
		return doubleExp;
	}

	/**
	 * @function 三人打怪返回单人应得到的经验
	 * @author LuoSR
	 * @date 2012-1-4
	 */
	private int trioHunt(final IGameCharacter member, int refLV, int refExp, ArrayList<IGameCharacter> team, final int monsterAvgLv) {
		int exp = this.singleHunt(refLV, refExp);
		int trioExp = 0;

		int minLv = roleList(team).get(0).getLevel();
		int midLv = roleList(team).get(1).getLevel();
		int maxLv = roleList(team).get(2).getLevel();

		if (member.getId() == roleList(team).get(0).getId()) {
			if (monsterAvgLv < minLv) {
				if (minLv == midLv && midLv != maxLv) {
					trioExp = (int) (exp * 1.5 * 0.35);
				} else if (minLv != midLv && midLv == maxLv) {
					trioExp = (int) (exp * 1.5 * 0.4);
				} else if (minLv == midLv && midLv == maxLv) {
					trioExp = (int) (exp * 1.5 * 0.35);
				} else {
					trioExp = (int) (exp * 1.5 * 0.4);
				}
			} else if (monsterAvgLv < midLv) {
				if (minLv == midLv && midLv != maxLv) {
					trioExp = (int) (exp * 1.5 * 0.35);
				} else if (minLv != midLv && midLv == maxLv) {
					trioExp = (int) (exp * 1.5 * 0.4);
				} else if (minLv == midLv && midLv == maxLv) {
					trioExp = (int) (exp * 1.5 * 0.35);
				} else {
					trioExp = (int) (exp * 1.5 * 0.35);
				}
			} else if (monsterAvgLv < maxLv) {
				if (minLv == midLv && midLv != maxLv) {
					trioExp = (int) (exp * 1.5 * 0.3);
				} else if (minLv != midLv && midLv == maxLv) {
					trioExp = (int) (exp * 1.5 * 0.3);
				} else if (minLv == midLv && midLv == maxLv) {
					trioExp = (int) (exp * 1.5 * 0.35);
				} else {
					trioExp = (int) (exp * 1.5 * 0.3);
				}
			} else {
				if (minLv == midLv && midLv != maxLv) {
					trioExp = (int) (exp * 1.5 * 0.3);
				} else if (minLv != midLv && midLv == maxLv) {
					trioExp = (int) (exp * 1.5 * 0.3);
				} else if (minLv == midLv && midLv == maxLv) {
					trioExp = (int) (exp * 1.5 * 0.35);
				} else {
					trioExp = (int) (exp * 1.5 * 0.3);
				}
			}
		} else if (member.getId() == roleList(team).get(1).getId()) {
			if (monsterAvgLv < minLv) {
				if (minLv == midLv && midLv != maxLv) {
					trioExp = (int) (exp * 1.5 * 0.35);
				} else if (minLv != midLv && midLv == maxLv) {
					trioExp = (int) (exp * 1.5 * 0.3);
				} else if (minLv == midLv && midLv == maxLv) {
					trioExp = (int) (exp * 1.5 * 0.35);
				} else {
					trioExp = (int) (exp * 1.5 * 0.3);
				}

			} else if (monsterAvgLv < midLv) {
				if (minLv == midLv && midLv != maxLv) {
					trioExp = (int) (exp * 1.5 * 0.35);
				} else if (minLv != midLv && midLv == maxLv) {
					trioExp = (int) (exp * 1.5 * 0.3);
				} else if (minLv == midLv && midLv == maxLv) {
					trioExp = (int) (exp * 1.5 * 0.35);
				} else {
					trioExp = (int) (exp * 1.5 * 0.35);
				}

			} else if (monsterAvgLv < maxLv) {
				if (minLv == midLv && midLv != maxLv) {
					trioExp = (int) (exp * 1.5 * 0.3);
				} else if (minLv != midLv && midLv == maxLv) {
					trioExp = (int) (exp * 1.5 * 0.35);
				} else if (minLv == midLv && midLv == maxLv) {
					trioExp = (int) (exp * 1.5 * 0.35);
				} else {
					trioExp = (int) (exp * 1.5 * 0.35);
				}
			} else {
				if (minLv == midLv && midLv != maxLv) {
					trioExp = (int) (exp * 1.5 * 0.3);
				} else if (minLv != midLv && midLv == maxLv) {
					trioExp = (int) (exp * 1.5 * 0.35);
				} else if (minLv == midLv && midLv == maxLv) {
					trioExp = (int) (exp * 1.5 * 0.35);
				} else {
					trioExp = (int) (exp * 1.5 * 0.3);
				}
			}
		} else {
			if (monsterAvgLv < minLv) {
				if (minLv == midLv && midLv != maxLv) {
					trioExp = (int) (exp * 1.5 * 0.3);
				} else if (minLv != midLv && midLv == maxLv) {
					trioExp = (int) (exp * 1.5 * 0.3);
				} else if (minLv == midLv && midLv == maxLv) {
					trioExp = (int) (exp * 1.5 * 0.35);
				} else {
					trioExp = (int) (exp * 1.5 * 0.3);
				}
			} else if (monsterAvgLv < midLv) {
				if (minLv == midLv && midLv != maxLv) {
					trioExp = (int) (exp * 1.5 * 0.3);
				} else if (minLv != midLv && midLv == maxLv) {
					trioExp = (int) (exp * 1.5 * 0.3);
				} else if (minLv == midLv && midLv == maxLv) {
					trioExp = (int) (exp * 1.5 * 0.35);
				} else {
					trioExp = (int) (exp * 1.5 * 0.3);
				}
			} else if (monsterAvgLv < maxLv) {
				if (minLv == midLv && midLv != maxLv) {
					trioExp = (int) (exp * 1.5 * 0.4);
				} else if (minLv != midLv && midLv == maxLv) {
					trioExp = (int) (exp * 1.5 * 0.35);
				} else if (minLv == midLv && midLv == maxLv) {
					trioExp = (int) (exp * 1.5 * 0.35);
				} else {
					trioExp = (int) (exp * 1.5 * 0.35);
				}
			} else {
				if (minLv == midLv && midLv != maxLv) {
					trioExp = (int) (exp * 1.5 * 0.4);
				} else if (minLv != midLv && midLv == maxLv) {
					trioExp = (int) (exp * 1.5 * 0.35);
				} else if (minLv == midLv && midLv == maxLv) {
					trioExp = (int) (exp * 1.5 * 0.35);
				} else {
					trioExp = (int) (exp * 1.5 * 0.4);
				}
			}
		}
		return trioExp;
	}

	private ModifyData caculGold(final IGameCharacter member, final int teamSize, final int totalGold, final ArrayList<IGameCharacter> team) {
		ModifyData modifyGold = new ModifyData(1);

		modifyGold.setModifyValue(totalGold / teamSize);

		if (member.hasBuff(BuffType.GOLD.getId())) {
			modifyGold.setModifyValue(member.fixValueAfterBuff(BuffType.GOLD.getId(), modifyGold.getModifyValue()));
		}

		if (team.size() == 2) {
			int role1 = team.get(0).getId();
			int role2 = team.get(1).getId();

			if (MarryMasterAddExpGold.twoIsMarry(role1, role2)) {
				modifyGold.setModifyValue((int) (modifyGold.getModifyValue() * 1.05));
			}
			if (MarryMasterAddExpGold.twoIsMaster(role1, role2)) {
				modifyGold.setModifyValue((int) (modifyGold.getModifyValue() * 1.05));
			}
		} else if (team.size() == 3) {
			int role1 = team.get(0).getId();
			int role2 = team.get(1).getId();
			int role3 = team.get(2).getId();
			if (MarryMasterAddExpGold.threeIsMarry(role1, role2, role3)) {
				modifyGold.setModifyValue((int) (modifyGold.getModifyValue() * 1.05));
			}
			if (MarryMasterAddExpGold.threeIsAllMaster(role1, role2, role3)) {
				modifyGold.setModifyValue((int) (modifyGold.getModifyValue() * 1.1));
			} else if (MarryMasterAddExpGold.threeIsMaster(role1, role2, role3)) {
				modifyGold.setModifyValue((int) (modifyGold.getModifyValue() * 1.05));
			}
		}

		return modifyGold;
	}

	public final int getTeamAvgLv(final List<IGameCharacter> team) {
		int lv = 0;
		for (int i = 0; i < team.size(); i++) {
			lv += team.get(i).getLevel();
		}
		return lv / team.size();
	}

	private int getMonsterAvgLv(final List<IGameCharacter> monsters) {
		int lv = 0;
		for (int i = 0; i < monsters.size(); i++) {
			lv += monsters.get(i).getLevel();
		}
		return lv / monsters.size();
	}

	final Map<IGameCharacter, BonusData> getBonus(final ArrayList<IGameCharacter> winTeam) {
		Map<IGameCharacter, BonusData> bonusMap = new HashMap<IGameCharacter, BonusData>();

		// 打怪
		if (teamLeft.get(0).getType() == 1) {

			for (int i = 0; i < teamRight.size(); i++) {
				RoleBean role = (RoleBean) teamRight.get(i);
				LuaService.callLuaFunction("npcFight", this, role);
			}

			if (winTeam == teamRight) {

				LuaService.callLuaFunction("getMonsterLoot", teamLeft.get(0).getId(), teamLeft.size(), lootList);
				// 人赢了
				// 先结算0经验, 1金钱
				// 得到的总经验和金钱下面按照公式去分
				int[] expGold = getBonusExpGold();

				for (int i = 0; i < teamRight.size(); i++) {
					BonusData bonus = new BonusData();

					IGameCharacter member = teamRight.get(i);
					// if(member.getHP() == 0) {
					// LuaService.callLuaFunction("roleDie", member,
					// this.battelType);
					// }

					if (expGold != null && expGold.length == 2) {

						ModifyData modifyExp = caculExp(member, teamRight, getMonsterAvgLv(teamLeft), expGold[0]);
						ModifyData modifyGold = caculGold(member, teamRight.size(), expGold[1], teamRight);
						bonus.getModifyDataList().add(modifyExp);
						bonus.getModifyDataList().add(modifyGold);
					}

					List<Item> loots = getBonusLoot();

					LuaService.callLuaFunction("monsterDie", this, member, bonus);

					Iterator<Item> it = loots.iterator();

					while (it.hasNext()) {
						// 将物品添加到背包中
						Item loot = it.next();

						if (LuaService.call4Bool("canAddToBag", member, loot.getTid(), loot.getStorage())) {
							((RoleBean) member).getStore().getBag().addItem(loot, true);
							ModifyItemData itemData = new ModifyItemData((byte) 0, loot);
							bonus.getItemDataList().add(itemData);
						} else {
							it.remove();
						}
					}

					bonusMap.put(member, bonus);
				}
			} else {
				// 怪赢了
				for (int i = 0; i < teamRight.size(); i++) {
					RoleBean role = (RoleBean) teamRight.get(i);
					if (LuaService.call4Bool("victim", role)) {
						bonusMap.put(role, new BonusData());
					} else {
						bonusMap.put(role, killedByMonster(role));
					}
					LuaService.callLuaFunction("roleDie", role, this.battelType);
				}
			}
		} else { // 打人

			if (this.getBattelType() == Battle.BATTLE_DUEL) {
				// 决斗无奖励
				for (int i = 0; i < teamRight.size(); i++) {
					BonusData bonus = new BonusData();
					bonusMap.put(teamRight.get(i), bonus);
				}

				for (int i = 0; i < teamLeft.size(); i++) {
					BonusData bonus = new BonusData();
					bonusMap.put(teamLeft.get(i), bonus);
				}
			} else if (this.getBattelType() == Battle.BATTLE_PVP) {

				List<IGameCharacter> loseTeam = getEnemyTeam(winTeam.get(0));
				int lvDiff = Math.abs(getHighLevel(winTeam) - getHighLevel(loseTeam));// 队伍等级差

				// 荣誉值计算，等级差小于等于10有荣誉值加减
				if (lvDiff <= 10) {
					this.winHonor(winTeam, this.loseHonor(loseTeam));
				}

				// 罪恶值计算
				int evilMark = this.countEvil(loseTeam) / winTeam.size();
				for (int i = 0; i < winTeam.size(); i++) {
					RoleBean winTeamPlayer = (RoleBean) winTeam.get(i);
					TaskState ts = winTeamPlayer.getTasks().getTaskState(24000);
					if (ts == null || (ts != null && ts.isFinished())) {
						winTeamPlayer.addEvil(evilMark);
					}
				}

				// PK 有几率抢夺对方物品
				// ===================先计算失败玩家惩罚 得出胜利者的战利品=====================
				int goldForWinner = 0;
				// 战斗失败惩罚
				for (int i = 0; i < loseTeam.size(); i++) {

					RoleBean loseTeamPlayer = (RoleBean) loseTeam.get(i);
					loseTeamPlayer.setPvPProtectTime(System.currentTimeMillis());
					TaskState ts = loseTeamPlayer.getTasks().getTaskState(24000);
					BonusData bonus = new BonusData();
					if (loseTeamPlayer.getNameCatalog() == RoleBean.NAME_RED && (ts == null || (ts != null && ts.isFinished()))) {
						// 赢的一方大于输的一方十级以上则不给金钱奖励
						if (lvDiff <= 10) {
							ModifyData modifyGold = new ModifyData(1);
							modifyGold.setModifyAct((byte) 1);
							// 掉落1/100的金钱
							modifyGold.setModifyValue(loseTeamPlayer.getGold() / 100);

							goldForWinner += modifyGold.getModifyValue();

							bonus.getModifyDataList().add(modifyGold);

						}

						// 物品掉落逻辑
						int loseRate = fixLoseRateConsiderLvDiff(lvDiff, getLoseItemRate(winTeam, loseTeam));
						// Log.info(Log.STDOUT, "修正后掉率概率为" + loseRate);
						if (isLoseItem(loseRate)) {
							Log.info(Log.STDOUT, "物品掉落 开始掉落逻辑");
							Item loot = getLoseItem(loseTeamPlayer);
							if (loot != null) {
								lootList.add(loot);
								ModifyItemData itemData = new ModifyItemData((byte) 1, loot);
								bonus.getItemDataList().add(itemData);
							}
						}
					}
					bonusMap.put(loseTeamPlayer, bonus);

				}
				// ============================================================

				// 给胜利玩家发放奖励
				for (int i = 0; i < winTeam.size(); i++) {

					BonusData bonus = new BonusData();
					RoleBean winTeamPlayer = (RoleBean) winTeam.get(i);
					List<Item> loots = getBonusLoot();

					Iterator<Item> it = loots.iterator();

					while (it.hasNext()) {
						// 将物品添加到背包中
						Item loot = it.next();

						if (LuaService.call4Bool("canAddToBag", winTeamPlayer, loot.getTid(), loot.getStorage())) {
							winTeamPlayer.getStore().getBag().addItem(loot, true);
							ModifyItemData itemData = new ModifyItemData((byte) 0, loot);
							bonus.getItemDataList().add(itemData);
						} else {
							it.remove();
						}
					}

					ModifyData modifyGold = new ModifyData(1);
					modifyGold.setModifyAct((byte) 0);

					modifyGold.setModifyValue(goldForWinner / winTeam.size());

					bonus.getModifyDataList().add(modifyGold);

					bonusMap.put(winTeamPlayer, bonus);
				}

				// 主动偷袭方增加每日偷袭次数
				Date date = new Date();
				for (int i = 0; i < teamRight.size(); i++) {
					RoleBean teamRightPlayer = (RoleBean) teamRight.get(i);
					TaskState ts = teamRightPlayer.getTasks().getTaskState(24000);
					if (ts == null || (ts != null && ts.isFinished())) {
						teamRightPlayer.setSneakAttackNum(teamRightPlayer.getSneakAttackNum() + 1);
						teamRightPlayer.setLastBattleTime(date);
					}
				}

			}
		}
		return bonusMap;
	}

	private void reliveAllAfterBattle() {
		for (int i = 0; i < teamRight.size(); i++) {
			if (teamRight.get(i).getHP() == 0) {
				teamRight.get(i).relive(battelType);
			}
		}

		// 左边可能是怪 乖不用复活
		if (teamLeft.get(0).getType() == 0) {
			for (int i = 0; i < teamLeft.size(); i++) {
				if (teamLeft.get(i).getHP() == 0) {
					teamLeft.get(i).relive(battelType);
				}
			}
		}
	}

	@SuppressWarnings("unused")
	@Deprecated
	private void getPVPLoot(final List<IGameCharacter> winTeam, final List<IGameCharacter> loseTeam, final Map<IGameCharacter, BonusData> bonusMap) {
		for (int i = 0; i < loseTeam.size(); i++) {
			RoleBean losePlayer = (RoleBean) loseTeam.get(i);
			int lvDiff = getHighLevel(winTeam) - getHighLevel(loseTeam);
			int loseRate = fixLoseRateConsiderLvDiff(lvDiff, getLoseItemRate(winTeam, loseTeam));

			// Log.info(Log.STDOUT, "修正后掉率概率为" + loseRate);
			if (isLoseItem(loseRate)) {
				Item loot = getLoseItem(losePlayer);
				if (loot != null) {
					lootList.add(loot);
				}
			}
		}
	}

	/**
	 * 考虑等级差进行掉落物品概率修正
	 * 
	 * @param lvDiff
	 *            胜利方与失败方的等级差
	 * @param rateBeforeFix
	 *            修正前的概率
	 * @return 考虑等级差进行修正后的掉落物品概率
	 * */
	private int fixLoseRateConsiderLvDiff(final int lvDiff, final int rateBeforeFix) {

		if (lvDiff <= 2) {
			return rateBeforeFix;
		} else if (lvDiff > 2 && lvDiff <= 4) {
			return rateBeforeFix * 8 / 10;
		} else if (lvDiff > 4 && lvDiff <= 6) {
			return rateBeforeFix * 5 / 10;
		} else if (lvDiff > 6 && lvDiff <= 8) {
			return rateBeforeFix * 5 / 10;
		} else if (lvDiff > 8 && lvDiff <= 10) {
			return rateBeforeFix / 100;
		} else {
			return 0;
		}
	}

	/**
	 * 物品掉落几率算法 由胜利队伍与失败队伍掉落几率之和决定
	 * 
	 * @param winTeam
	 *            胜利的队伍
	 * @param loseTeam
	 *            失败的队伍
	 * @return tag
	 * */
	private int getLoseItemRate(final List<IGameCharacter> winTeam, final List<IGameCharacter> loseTeam) {
		int rate = 0;

		int tmpMaxKillNum = 0;
		for (int i = 0; i < winTeam.size(); i++) {
			RoleBean winPlayer = (RoleBean) winTeam.get(i);
			if (winPlayer.getKillPlayerNum() > tmpMaxKillNum) {
				tmpMaxKillNum = winPlayer.getKillPlayerNum();
			}
		}

		rate += LuaService.call4Int("convertWinTeamKillNumToRate", tmpMaxKillNum);
		tmpMaxKillNum = 0;

		for (int i = 0; i < loseTeam.size(); i++) {
			RoleBean losePlayer = (RoleBean) loseTeam.get(i);
			if (losePlayer.getKillPlayerNum() > tmpMaxKillNum) {
				tmpMaxKillNum = losePlayer.getKillPlayerNum();
			}
		}

		rate += LuaService.call4Int("convertLoseTeamKillNumToRate", tmpMaxKillNum);

		return rate;
	}

	private boolean isLoseItem(final int rate) {
		if (rate >= 100) {
			return true;
		}
		int randomInt = random.nextInt(100);
		return randomInt < rate;
	}

	private Item getLoseItem(final RoleBean losePlayer) {
		Item loseItem = losePlayer.getStore().getInuse().pickItem(getRandomEquipType(), true);
		return loseItem;
	}

	private byte getRandomEquipType() {
		byte loseType = LuaService.call4Byte("getRandomEquipType", random.nextInt(10000) + 1);
		return loseType;
	}

	/**
	 * 当怪赢的时候，对玩家的惩罚
	 * 
	 * @param irole
	 *            被怪物杀死的玩家
	 * @return 对玩家的惩罚数据
	 */
	private BonusData killedByMonster(final RoleBean irole) {
		BonusData bonus = new BonusData();
		ModifyData modifyExp = null;
		ModifyData modifyGold = null;
		if (irole.getEXP() > 0) {
			modifyExp = new ModifyData(0);
			modifyExp.setModifyAct((byte) 1);
			int decreaseExp = LuaService.call4Int("getDecreasedExp", irole.getLevel());

			modifyExp.setModifyValue(irole.getEXP() > decreaseExp ? decreaseExp : irole.getEXP());
			bonus.getModifyDataList().add(modifyExp);
		}

		if (irole.getGold() > 0) {
			modifyGold = new ModifyData(1);
			modifyGold.setModifyAct((byte) 1);

			int decreaseGold = LuaService.call4Int("getDecreasedGold", irole.getLevel());

			modifyGold.setModifyValue(irole.getGold() > decreaseGold ? decreaseGold : irole.getGold());

			bonus.getModifyDataList().add(modifyGold);
		}

		return bonus;
	}

	/**
	 * 这里考虑几种异常情况 1. 组队中有玩家已经死亡 其他没死亡的玩家掉线 2. 未知
	 * 
	 * @return tag
	 */
	private boolean forceStart() {

		return false;
	}

	public final void checkTimeOut() {
		if (System.currentTimeMillis() - lastRoundStartTime > 40000 || forceStart()) {
			List<IGameCharacter> players = getPlayerList();
			for (int i = 0; i < players.size(); i++) {
				if (!fightOprations.keySet().contains(players.get(i))) {
					offLinePlayerAI(players.get(i));
				}
			}
			fightStart();
		} else if (System.currentTimeMillis() - lastRoundStartTime > 45000) {
			close();
		}
	}

	public final void close() {

		for (int i = 0; i < teamLeft.size(); i++) {
			teamLeft.get(i).setBattle(null);
		}

		for (int i = 0; i < teamRight.size(); i++) {
			teamRight.get(i).setBattle(null);
		}

		this.fightOprations.clear();
	}

	final void prepareData() {
		body.clear();
		body.putInt(0);
	}

	public final ArrayList<Item> getLootList() {
		return lootList;
	}

	public final void setLootList(final ArrayList<Item> lootList) {
		this.lootList = lootList;
	}

	public final byte getBattelType() {
		return battelType;
	}

}

class DuelTmpHPMP {
	private int HP;
	private int MP;

	public int getHP() {
		return HP;
	}

	public void setHP(final int hP) {
		HP = hP;
	}

	public int getMP() {
		return MP;
	}

	public void setMP(final int mP) {
		MP = mP;
	}

	DuelTmpHPMP(final int HP, final int MP) {
		this.HP = HP;
		this.MP = MP;
	}
}