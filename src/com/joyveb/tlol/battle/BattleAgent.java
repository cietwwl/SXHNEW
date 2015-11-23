package com.joyveb.tlol.battle;

import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.joyveb.tlol.LuaService;
import com.joyveb.tlol.OnlineService;
import com.joyveb.tlol.SubModules;
import com.joyveb.tlol.TianLongServer;
import com.joyveb.tlol.core.AgentProxy;
import com.joyveb.tlol.core.IGameCharacter;
import com.joyveb.tlol.item.Item;
import com.joyveb.tlol.net.IncomingMsg;
import com.joyveb.tlol.net.NetHandler;
import com.joyveb.tlol.protocol.FightChooseBody;
import com.joyveb.tlol.protocol.FightWithBody;
import com.joyveb.tlol.protocol.MsgID;
import com.joyveb.tlol.role.RoleBean;
import com.joyveb.tlol.role.UnHandledAsk;
import com.joyveb.tlol.server.ServerMessage;
import com.joyveb.tlol.task.TaskState;
import com.joyveb.tlol.util.Log;

public class BattleAgent extends AgentProxy {

	private static final int ALLOW_PVP_LV = 20;

	private Battle battle;

	private byte seatId;

	public BattleAgent(final RoleBean player) {
		this.player = player;
	}

	public final Battle getBattle() {
		return battle;
	}

	public final void setBattle(final Battle battle) {

		if (battle == null || (battle.getTeamLeft().size() > 0 && battle.getTeamRight().size() > 0)) {
			this.battle = battle;
		} else {
			Log.info(Log.STDOUT, "====================设置异常战斗信息=====================");
			Log.info(Log.STDOUT, "战斗类型为: " + battle.getBattelType());

			Log.info(Log.STDOUT, "Team Left: ");
			for (IGameCharacter igc : battle.getTeamLeft()) {
				Log.error(Log.STDOUT, igc.getNick() + " | " + igc.getHP() + " | " + " 连接是否正常 " + igc.isConnected());
			}

			Log.info(Log.STDOUT, "Team Right: ");
			for (IGameCharacter igc : battle.getTeamRight()) {
				Log.error(Log.STDOUT, igc.getNick() + " | " + igc.getHP() + " | " + " 连接是否正常 " + igc.isConnected());
			}
			Log.info(Log.STDOUT, "====================设置异常战斗信息=====================");
			try {
				new Exception("战斗异常");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param message
	 *            消息内容
	 */
	public final void processCommand(final IncomingMsg message) {
		switch (MsgID.getInstance(message.getHeader().getMsgID())) {

		case MsgID_Fight_With:// 客户端请求和某人或某怪物战斗
			if (System.currentTimeMillis() - player.getFightTime() < 1800) {
				System.out.println("加速掉线=====" + player.getNick());
				player.getNetHandler().close(NetHandler.STATE_CLOSED_BY_APP_LEVEL);
			}
			if (FightWithBody.INSTANCE.readBody(message.getBody())) {
				fightWith();
			} else {
				replyMessage(player, 1, MsgID.MsgID_Fight_With_Resp, "获取失败！");
			}
			player.setFightTime(System.currentTimeMillis());
			break;
		case MsgID_Fight_Choose:// 客户端告诉服务器，选择的攻击结果

			if (FightChooseBody.INSTANCE.readBody(message.getBody())) {
				fightChoose(player);
			} else {
				replyMessage(player, 1, MsgID.MsgID_Fight_Choose_Resp, "攻击失败！协议错误");
			}
			break;
		case MsgID_Hero_Request_Duel:
			message.getBody().getInt(); // body长度
			TaskState ts = this.player.getTasks().getTaskState(24000);
			if (ts == null || (ts != null && ts.isFinished())) {
				sendDuelReq(message.getBody().getInt());
			} else {
				prepareBody();
				body.putShort((short) 1);
				byte[] msg = getUTF8("此地图不允许切磋！！");
				body.putShort((short) msg.length);
				body.put(msg);
				body.putShort((short) 0);
				sendMsg(player, MsgID.MsgID_Hero_Request_Duel_Resp);
			}
			break;
		case MsgID_Hero_BeRequest_Duel_Resp:
			message.getBody().getInt(); // body长度
			duelResp(message.getBody().get());
			break;
		case MsgID_Fight_Sneak_Attack: // 偷袭
			message.getBody().getInt(); // body长度
			sneakAttack(message.getBody().getInt());
			break;
		case MsgID_Fight_Start_Resp:
			break;
		default:
			Log.error(Log.STDOUT, "processCommand", "unhandled msgid! : " + message.getHeader().getMsgID());
			break;

		}

	}

	private void sneakAttack(final int attackedPlyaerId) {

		RoleBean attackedPlayer = OnlineService.getOnline(attackedPlyaerId);
		TaskState ts = player.getTasks().getTaskState(24000);
		if (ts != null && ts.isFinished()) {
		}
		if (player.getId() == attackedPlyaerId) {
			prepareBody();
			LuaService.callLuaFunction("fillPrompt", "你不能偷袭你自己!");
			body.putShort((short) 0);
			sendMsg(player, MsgID.MsgID_Special_Train);
		} else if (attackedPlayer == null) {
			prepareBody();
			LuaService.callLuaFunction("fillPrompt", "未找到玩家");
			body.putShort((short) 0);
			sendMsg(player, MsgID.MsgID_Special_Train);
		} else if (attackedPlayer.getBattle() != null) {
			prepareBody();
			LuaService.callLuaFunction("fillPrompt", "对方正在战斗中!");
			body.putShort((short) 0);
			sendMsg(player, MsgID.MsgID_Special_Train);
		} else if (player.getBattle() != null) {
			if (player.getBattle().getBattelType() == Battle.BATTLE_PVE) {
				player.getBattle().close();
			}

			prepareBody();
			LuaService.callLuaFunction("fillPrompt", "数据发生异常 请重试!");
			body.putShort((short) 0);
			sendMsg(player, MsgID.MsgID_Special_Train);
		} else if (attackedPlayer.getPvPProtectTime() > 0 && (ts == null || (ts != null && ts.isFinished()))) {

			prepareBody();
			LuaService.callLuaFunction("fillPrompt", "对方虚弱中...剩余时间: " + (RoleBean.PVP_PROTECT_TIME - (System.currentTimeMillis() - attackedPlayer.getPvPProtectTime())) / 1000);
			body.putShort((short) 0);
			sendMsg(player, MsgID.MsgID_Special_Train);

		} else if (attackedPlayer.getCoords().getMap() != player.getCoords().getMap()) {

			prepareBody();
			LuaService.callLuaFunction("fillPrompt", "对方与您不在同一地图");
			body.putShort((short) 0);
			sendMsg(player, MsgID.MsgID_Special_Train);

		} else if (player.getTeam() != null && player.getTeam().isTeammate(attackedPlayer)) {

			prepareBody();
			LuaService.callLuaFunction("fillPrompt", "不能与队友进行切磋!");
			body.putShort((short) 0);
			sendMsg(player, MsgID.MsgID_Special_Train);

		} else if (!player.getMapAgent().canPvP(player.getCoords().getMap())) {

			prepareBody();
			LuaService.callLuaFunction("fillPrompt", "您所在的地图不能进行PK");
			body.putShort((short) 0);
			sendMsg(player, MsgID.MsgID_Special_Train);

		} else if (player.getLevel() < ALLOW_PVP_LV && (ts == null || (ts != null && ts.isFinished()))) {

			prepareBody();
			LuaService.callLuaFunction("fillPrompt", "您还未达到允许偷袭等级 要求等级" + ALLOW_PVP_LV);
			body.putShort((short) 0);
			sendMsg(player, MsgID.MsgID_Special_Train);

		} else if (attackedPlayer.getLevel() < ALLOW_PVP_LV && (ts == null || (ts != null && ts.isFinished()))) {

			prepareBody();
			LuaService.callLuaFunction("fillPrompt", "您不能偷袭级别过低的玩家");
			body.putShort((short) 0);
			sendMsg(player, MsgID.MsgID_Special_Train);

		} else if (player.getSneakAttackNum() >= 10 && (ts == null || (ts != null && ts.isFinished()))) {

			prepareBody();
			LuaService.callLuaFunction("fillPrompt", "您今日偷袭次数已达到上限");
			body.putShort((short) 0);
			sendMsg(player, MsgID.MsgID_Special_Train);

		} else if (player.getTeam() != null && player.getTeam().isOtherMembersSneakAttackNum()) {

			prepareBody();
			LuaService.callLuaFunction("fillPrompt", "您的队友今日偷袭次数已达到上限");
			body.putShort((short) 0);
			sendMsg(player, MsgID.MsgID_Special_Train);

		} else if (player.getTeam() != null && player.getTeam().isAllowPVPLevel(ALLOW_PVP_LV)) {

			prepareBody();
			LuaService.callLuaFunction("fillPrompt", "您的队友还未达到允许偷袭等级");
			body.putShort((short) 0);
			sendMsg(player, MsgID.MsgID_Special_Train);

		} else if (attackedPlayer.getStore().getBag().getItemCountByTid(Item.getAgainstSneakAttack()) != 0 && (ts == null || (ts != null && ts.isFinished()))) {
			attackedPlayer.getStore().getBag().pickItem(Item.getAgainstSneakAttack(), 1, true);

			prepareBody();
			LuaService.callLuaFunction("fillPrompt", "对方身上携带防偷袭娃娃.抵消了您的偷袭");
			body.putShort((short) 0);
			sendMsg(player, MsgID.MsgID_Special_Train);

			// 增加主动偷袭方得每日偷袭次数
			if (player.getTeam() != null) {
				player.getTeam().addSneakAttackNum(1);
			} else {
				player.addSneakAttackNum(1);
			}

			prepareBody();
			LuaService.callLuaFunction("fillBagDel", Item.getAgainstSneakAttack(), 1);
			body.putShort((short) 0);
			sendMsg(attackedPlayer, MsgID.MsgID_Special_Train);

			ServerMessage.sendSysPrompt(attackedPlayer, "您抵消了一次偷袭.且失去了一个防偷袭娃娃");

			attackedPlayer.setPvPProtectTime(System.currentTimeMillis());
		} else {

			// TODO 偷袭日志
			Log.info(Log.BATTLE, attackedPlayer.getUserid() + "#$" + attackedPlayer.getRoleid() + "#$" + player.getUserid() + "#$" + player.getRoleid() + "#$" + TianLongServer.srvId + "#$"
					+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

			ServerMessage.sendSysPrompt(attackedPlayer, player.getName() + " 偷袭了你");
			ArrayList<IGameCharacter> teamLeftCharacters = new ArrayList<IGameCharacter>();
			ArrayList<IGameCharacter> teamRightCharacters = new ArrayList<IGameCharacter>();

			if (attackedPlayer.getTeam() == null) {
				teamLeftCharacters.add(attackedPlayer);
			} else {
				teamLeftCharacters.addAll(attackedPlayer.getTeam().getMember());
			}
			if (player.getTeam() == null) {
				teamRightCharacters.add(player);
			} else {
				teamRightCharacters.addAll(player.getTeam().getMember());
			}
			Battle sneakBattle = new Battle(Battle.BATTLE_PVP, teamLeftCharacters, teamRightCharacters);
			sneakBattle.broadCastBattleInfo();

			if (player.getTeam() == null) {
				sendFightOrderTo(player);
			} else {
				for (int i = 0; i < player.getTeam().size(); i++) {
					sendFightOrderTo(player.getTeam().getMember().get(i));
				}
			}

			if (attackedPlayer.getTeam() == null) {
				sendFightOrderTo(attackedPlayer);
			} else {
				for (int i = 0; i < attackedPlayer.getTeam().size(); i++) {
					sendFightOrderTo(attackedPlayer.getTeam().getMember().get(i));
				}
			}
		}
	}

	/** online 为发起打斗者(队伍), 这里默认把发起打斗者放置在右边 */
	private void fightWith() {
		// 防止战斗非正常退出
		if (battle != null) {

			if (battle.getBattelType() == Battle.BATTLE_PVE) {
				battle.close();
			} else {
				Log.error(Log.ERROR, "非PVE战斗中 发送遇怪信息");
				return;
			}

			prepareBody();
			SubModules.fillAttributes(player);
			putShort((short) 0);
			sendMsg(player, MsgID.MsgID_Special_Train);
		}

		if (player.getHP() <= 0) {
			// 这里是为了防止异常情况角色血量为0的时候触发战斗
			player.relive(Battle.BATTLE_PVE);
		}

		// 这里是为了防止异常情况角色血量为0的时候触发战斗
		if (player.getTeam() != null) {
			for (int i = 1; i < player.getTeam().size(); i++) {
				if (player.getTeam().getMember().get(i).getHP() <= 0) {
					player.getTeam().getMember().get(i).relive(Battle.BATTLE_PVE);
				}
			}
		}

		ArrayList<IGameCharacter> teamLeftCharacters = new ArrayList<IGameCharacter>();
		ArrayList<IGameCharacter> teamRightCharacters = new ArrayList<IGameCharacter>();

		// teamRight初始化
		if (player.getTeam() != null) {
			if (player.getTeam().getLeader() == player) {
				teamRightCharacters.addAll(player.getTeam().getMember());
			} else {
				Log.error(Log.STDOUT, "fightWith", "team Member can not start a fight!");
				return;
			}

		} else {
			// 玩家自己打怪
			teamRightCharacters.add(player);
		}

		// teamLeft 初始化
		LuaService.callLuaFunction("initMonster", FightWithBody.INSTANCE.getFoeID(), getTeamAvgLv(teamRightCharacters), teamRightCharacters.size(), teamLeftCharacters);

		if (FightWithBody.INSTANCE.getType() == 1) {
			battle = new Battle(Battle.BATTLE_PVE, teamLeftCharacters, teamRightCharacters);
		} else {
			Log.error(Log.ERROR, "fightWith", "fight with type error, type = " + FightWithBody.INSTANCE.getType());
			return;
		}

		// 将Battle对象赋值给所有battle中的成员
		battle.broadCastBattleInfo();

		// 初始化完毕 开始返回数据
		body.clear();
		body.putInt(0);
		body.putShort((short) 0);
		body.putShort((short) 0); // 错误描述长度
		body.put((byte) battle.getFighterNum()); // 挑战1个怪物 加上自己
		initFightRoleData(body);
		body.putShort((short) 0);

		sendMsg(player, MsgID.MsgID_Fight_With_Resp);

		List<IGameCharacter> playerList = battle.getPlayerList();
		// 多人战斗要把此数据广播给Battle中所有的玩家
		for (int i = 1; i < playerList.size(); i++) {
			IGameCharacter player = playerList.get(i);
			if (player instanceof RoleBean && player != this.player) {
				prepareBody();
				body.putShort((short) 0);
				body.putShort((short) 0); // 错误描述长度
				body.put((byte) battle.getFighterNum()); // 挑战1个怪物 加上自己
				initFightRoleData(body);
				body.putShort((short) 0);

				sendMsg((RoleBean) player, MsgID.MsgID_Order_Hero_Fight);
			}
		}
	}

	public final int getTeamAvgLv(final List<IGameCharacter> team) {
		int lv = 0;
		for (int i = 0; i < team.size(); i++) {
			lv += team.get(i).getLevel();
		}
		return lv / team.size();
	}

	/**
	 * 用户选择攻击方式 处理结果 0.成功，1.MP不足，2.物品使用失败，3.逃跑成功，4.逃跑失败，5攻击结束，6物品使用成功
	 * 
	 * @param online
	 *            选择战斗选项的玩家
	 */
	private void fightChoose(final RoleBean online) {
		UserFightBean fight = FightChooseBody.INSTANCE.getFightBean();
		if (this.battle != null) {
			if (fight.getAtkType() == 3) { // 用户选择逃跑
				if (online.getTeam() == null && battle.getBattelType() == Battle.BATTLE_PVE) { // 单人打怪，未组队
					boolean flee = System.currentTimeMillis() % 2 == 1;

					if (flee) {
						LuaService.callLuaFunction("roleFlee", online, Battle.BATTLE_PVE);
						// 战斗结束清除战斗
						leaveBattle();

						replyMessage(online, 3, MsgID.MsgID_Fight_Choose_Resp, "逃跑成功");

						prepareBody();
						SubModules.fillAttributes(player);
						putShort((short) 0);
						sendMsg(player, MsgID.MsgID_Special_Train);
						return;
					} else {
						replyMessage(online, 4, MsgID.MsgID_Fight_Choose_Resp, "逃跑失败！");
					}
				} else {
					replyMessage(online, 4, MsgID.MsgID_Fight_Choose_Resp, battle.getBattelType() == Battle.BATTLE_PVE ? "逃跑失败，队伍中不能逃跑！" : "逃跑失败，PK时不能逃跑！");
				}
			} else {
				// 非逃跑/逃跑失败 选择正确的技能
				int result = checkConditions(fight);

				if (result == 7 || result == 6 || result == 0) {
					replyMessage(online, result, MsgID.MsgID_Fight_Choose_Resp, null);
				} else {
					// 这里失败的条件有 1MP 不足 2物品使用失败
					String resultTip = null;
					switch (result) {
					case 1:
						resultTip = "内力不足";
						break;
					case 2:
						resultTip = "物品使用失败!";
						break;
					default:
						resultTip = "未知错误";
						break;
					}

					replyMessage(online, result, MsgID.MsgID_Fight_Choose_Resp, resultTip);
					return;
				}
			}

			// 防止客户端错误重复选择
			if (!battle.isChose(online)) {
				battle.addFightOpration(online, fight);
			}
			if (battle.chooseOver()) {
				battle.fightStart();
			}
		} else {
			replyMessage(online, 5, MsgID.MsgID_Fight_Choose_Resp, "战斗已结束");

			prepareBody();
			SubModules.fillAttributes(player);
			putShort((short) 0);
			sendMsg(player, MsgID.MsgID_Special_Train);
			return;
		}
	}

	private void initFightRoleData(final ByteBuffer body) {
		// teamLeft处理
		// 可能是怪可能是玩家
		ArrayList<IGameCharacter> teamLeftMembers = battle.getTeamLeft();
		for (int i = 0; i < teamLeftMembers.size(); i++) {
			IGameCharacter gameCharacter = teamLeftMembers.get(i);
			if (gameCharacter != null) {
				body.put(gameCharacter.getType());
				body.put(gameCharacter.getSeatId()); // 只有一个时放置在中间

				body.putInt(gameCharacter.getId());
				byte[] nick = getUTF8(gameCharacter.getNick());
				body.putShort((short) nick.length);
				body.put(nick);
				body.putInt(0); // 角色文字的颜色
				body.putInt(gameCharacter.getHP());
				body.putInt(gameCharacter.getMaxHP());
				body.putInt(gameCharacter.getMP());
				body.putInt(gameCharacter.getMaxMP());
				body.putShort(gameCharacter.getAnimeGroup());
				if (gameCharacter.getType() == 0) {
					body.putShort((short) (gameCharacter.getAnime() + 5));
				} else {
					body.putShort(gameCharacter.getAnime());
				}
				body.put((byte) 0);
				body.putShort((short) 0);
			}
		}
		// teamRight 处理
		// 相对于teamLeft teamRight里的对象只有可能是玩家
		ArrayList<IGameCharacter> teamRightMembers = battle.getTeamRight();
		for (int i = 0; i < teamRightMembers.size(); i++) {
			IGameCharacter player = teamRightMembers.get(i);
			if (player != null) {
				body.put((byte) 0); // 类别：玩家
				body.put(player.getSeatId());
				body.putInt(player.getId());
				byte[] nick = getUTF8(player.getNick());
				body.putShort((short) nick.length);
				body.put(nick);
				body.putInt(0); // 角色文字的颜色
				body.putInt(player.getHP());
				body.putInt(player.getMaxHP());
				body.putInt(player.getMP());
				body.putInt(player.getMaxMP());
				body.putShort(player.getAnimeGroup());

				if (player.getType() == 0) {
					body.putShort((short) (player.getAnime() + 4));
				} else {
					body.putShort(player.getAnime());
				}
				body.put((byte) 0);
				body.putShort((short) 0);
			}
		}
	}

	public final void sendFightOrderTo(final IGameCharacter character) {
		prepareBody();
		body.putShort((short) 0);
		body.putShort((short) 0);
		body.put((byte) battle.getFighterNum()); // 挑战1个怪物 加上自己
		initFightRoleData(body);
		body.putShort((short) 0);

		sendMsg((RoleBean) character, MsgID.MsgID_Order_Hero_Fight);
	}

	private void leaveBattle() {
		battle.close();
	}

	private int checkConditions(final UserFightBean fight) {
		int result = 0;
		if (fight.getAtkType() == 1) {
			if (player.getSkillAgent().checkCondition(fight.getAppID())) {
				result = 7;
			} else {
				result = 1;
			}
		} else if (fight.getAtkType() == 2) {
			Item item = player.getStore().getBag().getItem(fight.getAppID());
			if (item != null && item.canUseInFight(player)) {
				result = 6;
			} else {
				result = 2;
			}
		}

		return result;
	}

	public final byte getSeatId() {
		return this.battle == null ? -1 : seatId;
	}

	public final void setSeatId(final byte seatId) {
		this.seatId = seatId;
	}

	private void sendDuelReq(final int playerId) {

		if (player.getTeam() == null) {
			RoleBean requestedPlayer = OnlineService.getOnline(playerId);
			if (requestedPlayer == null) {
				prepareBody();
				body.putShort((short) 1);
				byte[] msg = getUTF8("对方已下线");
				body.putShort((short) msg.length);
				body.put(msg);
				body.putShort((short) 0);
				sendMsg(player, MsgID.MsgID_Hero_Request_Duel_Resp);
			} else if (requestedPlayer.getUnHandledAsk() == null) {
				if (requestedPlayer.getTeam() == null && requestedPlayer.getBattle() == null) {

					UnHandledAsk duelRequest = new UnHandledAsk(player.getId(), MsgID.MsgID_Hero_Request_Duel);
					requestedPlayer.setUnHandledAsk(duelRequest);

					prepareBody();
					// body.putShort((short)0);
					byte[] msgToRequestedPlayer = getUTF8("是否允许 " + player.getNick() + "(等级: " + player.getLevel() + " 职业: " + player.getVocation() + ")" + " 与您切磋?");
					body.putShort((short) msgToRequestedPlayer.length);
					body.put(msgToRequestedPlayer);
					body.putShort((short) 0);
					sendMsg(requestedPlayer, MsgID.MsgID_Hero_BeRequest_Duel);

					prepareBody();
					body.putShort((short) 0);
					byte[] msg = getUTF8("切磋请求已发送请等待对方回应...");
					body.putShort((short) msg.length);
					body.put(msg);
					body.putShort((short) 0);
					sendMsg(player, MsgID.MsgID_Hero_Request_Duel_Resp);

				} else {
					// 对方在队伍中不能发起切磋
					prepareBody();
					body.putShort((short) 1);
					byte[] msg = getUTF8("对方正忙, 不能发起切磋请求");
					body.putShort((short) msg.length);
					body.put(msg);
					body.putShort((short) 0);
					sendMsg(player, MsgID.MsgID_Hero_Request_Duel_Resp);
				}
			} else {
				// 对方正忙
				prepareBody();
				body.putShort((short) 1);
				byte[] msg = getUTF8("对方正忙, 请稍后发送切磋请求");
				body.putShort((short) msg.length);
				body.put(msg);
				body.putShort((short) 0);
				sendMsg(player, MsgID.MsgID_Hero_Request_Duel_Resp);
			}
		} else {
			// 在队伍中不能发起切磋
			prepareBody();
			body.putShort((short) 2);
			byte[] msg = getUTF8("你在队伍中, 不能发起切磋请求");
			body.putShort((short) msg.length);
			body.put(msg);
			body.putShort((short) 0);
			sendMsg(player, MsgID.MsgID_Hero_Request_Duel_Resp);
		}

	}

	private void duelResp(final byte resp) {
		if (player.getUnHandledAsk() != null) {
			RoleBean requestPlayer = OnlineService.getOnline(player.getUnHandledAsk().getRequestId());

			if (requestPlayer == null) {
				ServerMessage.sendSysPrompt(player, "对方已下线！");
			} else if (player.getBattle() != null) {
				ServerMessage.sendSysPrompt(player, "战斗中不能同意其他玩家的请求!");
			} else if (requestPlayer.getTeam() == null && requestPlayer.getBattle() == null && resp == 0) {
				switch (player.getUnHandledAsk().getCmd()) {
				case MsgID_Hero_Request_Duel:

					ArrayList<IGameCharacter> teamLeftCharacters = new ArrayList<IGameCharacter>();
					ArrayList<IGameCharacter> teamRightCharacters = new ArrayList<IGameCharacter>();

					teamLeftCharacters.add(player);
					teamRightCharacters.add(requestPlayer);

					Battle duelBattle = new Battle(Battle.BATTLE_DUEL, teamLeftCharacters, teamRightCharacters);
					duelBattle.broadCastBattleInfo();

					sendFightOrderTo(player);
					sendFightOrderTo(requestPlayer);
					break;
				default:
					Log.error(Log.STDOUT, "duelResp", "unhandled msgid! : " + player.getUnHandledAsk().getCmd());
					break;
				}
			} else {
				switch (player.getUnHandledAsk().getCmd()) {
				case MsgID_Hero_Request_Duel:
					if (requestPlayer.getTeam() != null && resp == 0) {
						ServerMessage.sendSysPrompt(player, "对方正忙, 不能进行切磋");
					} else {
						ServerMessage.sendSysPrompt(requestPlayer, "对方拒绝了你的请求");
					}
					break;
				default:
					Log.error(Log.STDOUT, "duelResp", "unhandled msgid! : " + player.getUnHandledAsk().getCmd());
					break;
				}
			}
		} else {
			ServerMessage.sendSysPrompt(player, "请求已过期!");
		}

		player.setUnHandledAsk(null);
	}

	public void checkTimeOut() {
		if (this.battle == null)
			return;

		this.battle.checkTimeOut();
	}

	// TODO
	/** BOSS卡战斗 */
	public void bossCardbattle(int monsterId, int monsterNum) {
		// 防止战斗非正常退出
		if (battle != null) {
			if (battle.getBattelType() == Battle.BATTLE_PVE) {
				battle.close();
			} else {
				Log.error(Log.ERROR, "非PVE战斗中 发送遇怪信息");
				return;
			}

			prepareBody();
			SubModules.fillAttributes(player);
			putShort((short) 0);
			sendMsg(player, MsgID.MsgID_Special_Train);
		}

		if (player.getHP() <= 0) {
			// 这里是为了防止异常情况角色血量为0的时候触发战斗
			player.relive(Battle.BATTLE_PVE);
		}

		// 这里是为了防止异常情况角色血量为0的时候触发战斗
		if (player.getTeam() != null) {
			for (int i = 1; i < player.getTeam().size(); i++) {
				if (player.getTeam().getMember().get(i).getHP() <= 0) {
					player.getTeam().getMember().get(i).relive(Battle.BATTLE_PVE);
				}
			}
		}

		FightWithBody.INSTANCE.setFoeID(monsterId);// 设置战斗怪物ID
		FightWithBody.INSTANCE.setType((byte) 1);// 设置战斗类型为PVE
		ArrayList<IGameCharacter> teamLeftCharacters = new ArrayList<IGameCharacter>();
		ArrayList<IGameCharacter> teamRightCharacters = new ArrayList<IGameCharacter>();

		// teamRight初始化
		if (player.getTeam() != null) {
			if (player.getTeam().getLeader() == player) {
				teamRightCharacters.addAll(player.getTeam().getMember());
			} else {
				Log.error(Log.STDOUT, "fightWith", "team Member can not start a fight!");
				return;
			}

		} else {
			// 玩家自己打怪
			teamRightCharacters.add(player);
		}

		// teamLeft 初始化
		LuaService.callLuaFunction("initBossCardMonster", FightWithBody.INSTANCE.getFoeID(), monsterNum, teamLeftCharacters);

		if (FightWithBody.INSTANCE.getType() == 1) {
			battle = new Battle(Battle.BATTLE_PVE, teamLeftCharacters, teamRightCharacters);
		} else {
			Log.error(Log.ERROR, "fightWith", "fight with type error, type = " + FightWithBody.INSTANCE.getType());
			return;
		}

		// 将Battle对象赋值给所有battle中的成员
		battle.broadCastBattleInfo();

		// 初始化完毕 开始返回数据
		body.clear();
		body.putInt(0);
		body.putShort((short) 0);
		body.putShort((short) 0); // 错误描述长度
		body.put((byte) battle.getFighterNum()); // 挑战1个怪物 加上自己
		initFightRoleData(body);
		body.putShort((short) 0);

		sendMsg(player, MsgID.MsgID_Fight_With_Resp);

		List<IGameCharacter> playerList = battle.getPlayerList();
		// 多人战斗要把此数据广播给Battle中所有的玩家
		for (int i = 1; i < playerList.size(); i++) {
			IGameCharacter player = playerList.get(i);
			if (player instanceof RoleBean && player != this.player) {
				prepareBody();
				body.putShort((short) 0);
				body.putShort((short) 0); // 错误描述长度
				body.put((byte) battle.getFighterNum()); // 挑战1个怪物 加上自己
				initFightRoleData(body);
				body.putShort((short) 0);

				sendMsg((RoleBean) player, MsgID.MsgID_Order_Hero_Fight);
			}
		}

	}

}
