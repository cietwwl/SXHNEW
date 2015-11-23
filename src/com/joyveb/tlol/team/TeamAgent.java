package com.joyveb.tlol.team;

import com.joyveb.tlol.OnlineService;
import com.joyveb.tlol.core.AgentProxy;
import com.joyveb.tlol.map.Coords;
import com.joyveb.tlol.map.GridMapSys;
import com.joyveb.tlol.net.IncomingMsg;
import com.joyveb.tlol.protocol.MsgID;
import com.joyveb.tlol.role.RoleBean;
import com.joyveb.tlol.server.ServerMessage;
import com.joyveb.tlol.task.TaskState;
import com.joyveb.tlol.util.Log;

public class TeamAgent extends AgentProxy {

	private Team team;

	public TeamAgent(final RoleBean player) {
		this.player = player;
	}

	@Override
	public final void processCommand(final IncomingMsg message) {
		TaskState ts = this.player.getTasks().getTaskState(24000);
		switch (MsgID.getInstance(message.getHeader().getMsgID())) {
		case MsgID_Team_Create:
			if (ts == null || (ts != null && ts.isFinished())) {
				createTeam();
			} else {
				prepareBody();
				body.putShort((short) 1);// 创建失败
				byte[] msg = getUTF8("在狮王争霸任务中, 不能创建队伍！");
				body.putShort((short) msg.length);// 错误消息长度
				body.put(msg);
				body.putInt(0);
				body.put((byte) 0);
				body.putShort((short) 0);
				sendMsg(player, MsgID.MsgID_Team_Create_Resp);
			}
			break;
		case MsgID_Team_Delete:
			dissmissTeam();
			break;
		case MsgID_Team_Request_Enter: // 申请入队
			message.getBody().getInt(); // body长度
			try {
				requestEnter(message.getBody().getInt());
			} catch (Exception e) {
				e.printStackTrace();
				if (message.getBody() == null) {
					Log.error(Log.ERROR, "(message.getBody() is null");
				}
			}
			break;
		case MsgID_Team_Request_Quit:
			requestQuit();
			break;
		case MsgID_Team_Give_Invite: // 邀请入队
			message.getBody().getInt(); // body长度

			if (ts == null || (ts != null && ts.isFinished())) {
				teamInvite(message.getBody().getInt());
			} else {
				prepareBody();
				body.putShort((short) 1);// 创建失败
				byte[] msg = getUTF8("在狮王争霸任务中, 不能组队");
				body.putShort((short) msg.length);// 错误消息长度
				body.put(msg);
				body.putInt(0);
				body.put((byte) 0);
				body.putShort((short) 0);
				sendMsg(player, MsgID.MsgID_Team_Create_Resp);
			}
			break;
		case MsgID_Team_Ask_Resp: // 确认
			message.getBody().getInt(); // body长度
			askResp(message.getBody().get());
			break;
		case MsgID_Team_In_Resp:
			break;
		case MsgID_Team_Event_Resp:
			break;
		default:
			break;
		}
	}

	public final Team getTeam() {
		return team;
	}

	public void setTeam(Team team) {
		this.team = team;
	}

	/**
	 * (74) MsgID_Team_Create_Resp:
	 * 
	 * 名称 描述 类型 BodyLength 下面数据的总长度 U32 Result 处理结果 0,成功 其他失败 U16 ErrorStrlen
	 * 错误描述长度（成功为0） U16 ErrorStr 错误描述 String TeamID 队伍号 U32 Team_Role_Num
	 * 当前队伍几个人 U8 Team_Role_Data 角色的数据（Team_Role_Data） Data Other_Data_length
	 * 保留数据长 U16 Other_Data 保留数据 Data
	 */

	private void createTeam() {
		prepareBody();
		if (team == null) {
			team = new Team();
			team.addMember(player);
			if (player.getTeamAgent().team.size() > 3) {
				player.getTeamAgent().dissmissTeam();
			}

			byte[] msg = getUTF8("队伍创建成功!");
			body.putShort((short) 0);// 创建成功
			body.putShort((short) msg.length);// 错误消息长度
			body.put(msg);
			body.putInt(team.getId());
			body.put((byte) team.getMember().size());

			/**
			 * Team_Role_Data 名称 描述 类型 ID 角色号 U32 IS_Captain 是否队长（0不是1是） U8
			 * Name_Length 名称的长度 U16 Name 名的内容 String Name_Color 名称的颜色 U32 MapID
			 * 改角色所在的地图 U16 Mapx 人物当前的坐标 U32 Mapy 人物当前的坐标 U32
			 * 
			 * Other_Data_length 保留数据长 U16 Other_Data 保留数据 Data
			 */

			for (int i = 0; i < team.getMember().size(); i++) {
				RoleBean character = (RoleBean) team.getMember().get(i);
				body.putInt(character.getId());
				body.put((byte) (i == 0 ? 1 : 0));
				byte[] nick = getUTF8(character.getNick());
				body.putShort((short) nick.length);
				body.put(nick);
				body.putInt(character.getColor());
				body.putShort(character.getCoords().getMap());
				body.putInt(character.getCoords().getX());
				body.putInt(character.getCoords().getY());
				body.putShort((short) 0);
			}
			body.putShort((short) 0);
			sendMsg(player, MsgID.MsgID_Team_Create_Resp);
		} else {
			// 已经有队伍 不能创建
			prepareBody();
			body.putShort((short) 1);// 创建失败
			byte[] msg = getUTF8("你已经在队伍中了, 不能创建队伍");
			body.putShort((short) msg.length);// 错误消息长度
			body.put(msg);
			body.putInt(0);
			body.put((byte) 0);
			body.putShort((short) 0);
			sendMsg(player, MsgID.MsgID_Team_Create_Resp);
		}

	}

	/**
	 * (76) MsgID_Team_Delete_Resp:
	 * 
	 * 名称 描述 类型 BodyLength 下面数据的总长度 U32 Result 处理结果 0,成功 其他失败 U16 ErrorStrlen
	 * 错误描述长度（成功为0） U16 ErrorStr 错误描述 String Other_Data_length 保留数据长 U16
	 * Other_Data 保留数据 Data
	 */

	public void dissmissTeam() {

		if (team != null && team.getLeader() == this.player) {
			prepareBody();
			body.putShort((short) 0);
			byte[] msg = getUTF8("队伍已经解散!");
			body.putShort((short) msg.length);
			body.put(msg);
			body.putShort((short) 0);
			sendMsg(player, MsgID.MsgID_Team_Delete_Resp);

			// 因为默认队伍第一个人为队长 因此i = 1
			for (int i = 1; i < team.getMember().size(); i++) {
				RoleBean updater = (RoleBean) team.getMember().get(i);
				updateTeam(updater, player, 5);
				updater.getTeamAgent().team = null;
			}

			team.dismiss();
			team = null;
		} else {
			prepareBody();
			body.putShort((short) 1);

			byte[] msg;
			if (team == null)
				msg = getUTF8("你没在队伍中.");
			else if (team.getLeader() != this.player)
				msg = getUTF8("你不是队长不能解散队伍");
			else
				msg = getUTF8("未知错误");
			body.putShort((short) msg.length);
			body.put(msg);
			body.putShort((short) 0);
			sendMsg(player, MsgID.MsgID_Team_Delete_Resp);
		}
	}

	private boolean isInTeam(RoleBean player, Team team) {
		return team.member.contains(player);
	}

	/**
	 * 申请入队
	 * 
	 * @param roleid
	 *            队长角色编号
	 */
	private void requestEnter(int roleid) {

		prepareBody();
		RoleBean requestPlayer;

		if ((requestPlayer = OnlineService.getOnline(roleid)) != null) {
			if (requestPlayer.getTeam() != null) {

				if (!isInTeam(player, requestPlayer.getTeam()) && player.getTeam() == null) {
					if (requestPlayer.getTeam().size() < Team.MAX_MEMBER) {

						RoleBean leader = (RoleBean) requestPlayer.getTeam().getLeader();
						// 同时只能处理一个请求
						if (leader.getBattleAgent().getBattle() == null && sendAsk(player, leader, 0)) {

							// 以上消息转发给队长

							/**
							 * MsgID_Team_Request_Enter_Resp:
							 * 
							 * 名称 描述 类型 BodyLength 下面数据的总长度 U32 Result 处理结果 0,成功
							 * 其他失败 U16 ErrorStrlen 错误描述长度（成功为0） U16 ErrorStr
							 * 错误描述 String Other_Data_length 保留数据长 U16
							 * Other_Data 保留数据 Data
							 */
							prepareBody();
							body.putShort((short) 0);
							byte[] msg = getUTF8("申请已发送, 请等待对方回应...");
							body.putShort((short) msg.length);
							body.put(msg);
							body.putShort((short) 0);

							sendMsg(player, MsgID.MsgID_Team_Request_Enter_Resp);

						} else {
							// 对方正忙
							prepareBody();
							body.putShort((short) 1);
							byte[] msg;
							if (leader.getBattleAgent().getBattle() != null)
								msg = getUTF8("对方正在战斗中...");
							else
								msg = getUTF8("对方正忙...");
							body.putShort((short) msg.length);
							body.put(msg);
							body.putShort((short) 0);

							sendMsg(player, MsgID.MsgID_Team_Request_Enter_Resp);
						}

					} else {
						// 队伍已满
						prepareBody();
						body.putShort((short) 1);
						byte[] msg = getUTF8("对方队伍已满");
						body.putShort((short) msg.length);
						body.put(msg);
						body.putShort((short) 0);

						sendMsg(player, MsgID.MsgID_Team_Request_Enter_Resp);
					}
				} else {
					// 已经在此队伍中
					prepareBody();
					body.putShort((short) 1);
					byte[] msg = getUTF8("你已经在队伍中...");
					body.putShort((short) msg.length);
					body.put(msg);
					body.putShort((short) 0);

					sendMsg(player, MsgID.MsgID_Team_Request_Enter_Resp);
				}
			} else {
				// 玩家没有队伍
				prepareBody();
				body.putShort((short) 1);
				byte[] msg = getUTF8(requestPlayer.getNick() + " 没有队伍.");
				body.putShort((short) msg.length);
				body.put(msg);
				body.putShort((short) 0);

				sendMsg(player, MsgID.MsgID_Team_Request_Enter_Resp);
			}
		} else {
			// 没有找到玩家
			prepareBody();
			body.put((byte) 1);
			byte[] msg = getUTF8("没有找到玩家");
			body.putShort((short) msg.length);
			body.put(msg);
			body.putShort((short) 0);

			sendMsg(player, MsgID.MsgID_Team_Request_Enter_Resp);
		}
	}

	private void askResp(byte isAgree) {
		if (player.getUnHandledAsk() != null) {
			RoleBean invoker = OnlineService.getOnline(player.getUnHandledAsk().getRequestId());
			if (invoker != null && invoker.getBattle() == null && player.getMapAgent().isInSameMap(invoker) && player.getBattle() == null) {
				if (isAgree == 0) {
					switch (player.getUnHandledAsk().getCmd()) {
					case MsgID_Team_Request_Enter:

						// 这里防止队长同意时 申请人已加入到别的队伍中
						if (invoker.getTeam() == null) {
							// 给小队成员发送新来队友的信息
							// 这里并没有将队友添加到队伍列表里 稍后添加
							for (int i = 0; i < team.size(); i++) {
								RoleBean updater = (RoleBean) team.getMember().get(i);
								updateTeam(updater, invoker, 2);
							}
							// 将成员添加到队伍中
							team.addMember(invoker);
							// 将小队添加到小队成员teamAgent中
							invoker.getTeamAgent().team = team;
							// 给新来的队友发送队伍信息
							boradcastTeamInfo(invoker);
							if (invoker.getTeamAgent().team.size() > 3) {
								invoker.getTeamAgent().dissmissTeam();
							}
						} else {
							updateTeam(player, invoker, 9);
						}
						break;
					case MsgID_Team_Give_Invite:

						if (player.getTeam() != null) {
							updateTeam(player, player, 10);
						} else if (invoker.getTeamAgent().team == null) {
							team = new Team();
							team.addMember(invoker);
							invoker.getTeamAgent().team = team;
							team.addMember(player);
							boradcastTeamInfo(player);
							boradcastTeamInfo(invoker);
							if (invoker.getTeamAgent().team.size() > 3) {
								invoker.getTeamAgent().dissmissTeam();
							}
						} else {

							if (invoker.getTeamAgent().team.getLeader() == invoker) {

								for (int i = 0; i < invoker.getTeamAgent().team.size(); i++) {
									RoleBean updater = (RoleBean) invoker.getTeamAgent().team.getMember().get(i);
									updateTeam(updater, player, 2);
								}
								team = invoker.getTeamAgent().team;
								team.addMember(player);
								boradcastTeamInfo(player);
								if (team.size() > 3) {
									invoker.getTeamAgent().dissmissTeam();
								}
							} else {
								updateTeam(player, player, 9);
							}

						}
						break;
					}
				} else {
					switch (player.getUnHandledAsk().getCmd()) {
					case MsgID_Team_Request_Enter:
						// 这时member为请求加入队伍的玩家
						updateTeam(invoker, (RoleBean) team.getLeader(), 0);
						break;
					case MsgID_Team_Give_Invite:
						// 这时member为主动发起邀请请求的玩家(可能是队长或者没有组队的玩家)
						updateTeam(invoker, player, 1);
						break;
					}
				}
			} else {
				// 玩家未找到
				if (invoker == null)
					updateTeam(player, player, 6);
				else if (invoker.getBattle() != null) {
					// 玩家正在战斗...不能让玩家加入队伍
					updateTeam(player, player, 7);
				} else if (!player.getMapAgent().isInSameMap(invoker)) {
					// 不在同一地图
					updateTeam(player, player, 8);
					updateTeam(invoker, invoker, 8);
				} else {
					updateTeam(player, player, 10);
				}
			}
		} else {
			updateTeam(player, player, 10);
		}
		this.player.setUnHandledAsk(null);
	}

	public void requestQuit() {
		prepareBody();
		if (team != null && team.getLeader() == player) {
			// 这里为容错判断 应该不会走到这里来
			body.putShort((short) 0);
			byte[] msg = getUTF8("队伍已解散.");
			body.putShort((short) msg.length);
			body.put(msg);

			body.putShort((short) 0);
			sendMsg(player, MsgID.MsgID_Team_Request_Quit_Resp);

			// 因为默认队伍第一个人为队长 因此i = 1
			for (int i = 1; i < team.getMember().size(); i++) {
				RoleBean updater = (RoleBean) team.getMember().get(i);
				updateTeam(updater, player, 5);
				updater.getTeamAgent().team.dismiss();
				updater.getTeamAgent().team = null;
			}

			team.dismiss();
			team = null;

			Log.error(Log.ERROR, "team leader try to call request quit!");
		} else if (team != null) {
			team.removeMember(player);

			for (int i = 0; i < team.getMember().size(); i++) {
				RoleBean updater = (RoleBean) team.getMember().get(i);
				updateTeam(updater, player, 3);
			}
			/**
			 * MsgID_Team_Request_Quit_Resp:
			 * 
			 * 名称 描述 类型 BodyLength 下面数据的总长度 U32 Result 处理结果 0,成功 其他失败 U16
			 * ErrorStrlen 错误描述长度（成功为0） U16 ErrorStr 错误描述 String
			 * Other_Data_length 保留数据长 U16 Other_Data 保留数据 Data
			 */
			prepareBody();
			body.putShort((short) 0);
			byte[] msg = getUTF8("已退出队伍.");
			body.putShort((short) msg.length);
			body.put(msg);

			body.putShort((short) 0);
			team = null;
		} else {
			prepareBody();
			body.putShort((short) 1);
			byte[] msg = getUTF8("你不在队伍中.");
			body.putShort((short) msg.length);
			body.put(msg);

			body.putShort((short) 0);
		}

		sendMsg(player, MsgID.MsgID_Team_Request_Quit_Resp);
	}

	private void updateTeam(RoleBean updater, RoleBean eventInvoker, int type) {
		/**
		 * MsgID_Team_Event:
		 * 
		 * 名称 描述 类型 BodyLength 下面数据的总长度 U32 Event_Type 当前是什么事件 0.队长拒绝了你的申请
		 * 1.玩家拒绝了你的邀请 2.某人刚加入队伍（更新本地的客户端显示） 3.某人离开了队伍（更新本地的客户端显示） 4.当前队长已经移动到某地
		 * U8 5.此队伍已经解散 6.其他错误情况
		 * 
		 * MapID 当Event_Type为2,3,4的时候，可用 MapX 当Event_Type为2,3,4的时候，可用 MapY
		 * 当Event_Type为2,3,4的时候，可用
		 * 
		 * ID 角色号 U32 Name_Length 用户名的长度 U16 Name 用户名 String Name_Color 用户名的颜色
		 * U32 Detail_Length 描述的长度 U16 Detail 描述 String Detail_Color 描述的颜色 U32
		 * Other_Data_length 保留数据长 U16 Other_Data 保留数据 Data
		 */
		prepareBody();
		body.put((byte) type);
		body.putShort(eventInvoker.getCoords().getMap());
		body.putInt(eventInvoker.getCoords().getX());
		body.putInt(eventInvoker.getCoords().getY());
		body.putInt(eventInvoker.getId());
		putString(eventInvoker.getName());
		body.putInt(eventInvoker.getColor());
		byte[] msg = getUTF8("未知错误");

		switch (type) {
		case 0:
			msg = getUTF8("你被拒绝加入队伍.");
			break;
		case 1:
			msg = getUTF8(eventInvoker.getNick() + " 拒绝了你的邀请.");
			break;
		case 2:
			msg = getUTF8(eventInvoker.getNick() + " 加入队伍.");
			break;
		case 3:
			msg = getUTF8(eventInvoker.getNick() + " 离开了队伍.");
			break;
		case 4:
			msg = getUTF8(eventInvoker.getNick() + " 移动到了" + eventInvoker.getCoords().getX() + " , " + eventInvoker.getCoords().getY());
			break;
		case 5:
			msg = getUTF8("队伍已经解散");
			break;
		case 6:
			msg = getUTF8("玩家未找到或者已经下线...");
			break;
		case 7:
			msg = getUTF8("对方正忙, 暂时无法处理你的请求...");
			break;
		case 8:
			msg = getUTF8("必须在同一地图才能组队...");
			break;
		case 9:
			msg = getUTF8("对方已加入别的队伍...");
			break;
		case 10:
			msg = getUTF8("请求已过期...");
			break;
		case 25:
			msg = getUTF8("队伍人员已满...");
			break;
		default:
			msg = getUTF8("未知错误...");
		}

		body.putShort((short) msg.length);
		body.put(msg);
		body.putInt(ServerMessage.SYS_COLOR);
		body.putShort((short) 0);

		sendMsg(updater, MsgID.MsgID_Team_Event);
	}

	/**
	 * 邀请入队
	 * 
	 * @param playerId
	 *            被邀请人的角色编号
	 */
	private void teamInvite(int playerId) {

		prepareBody();
		RoleBean invitedPlayer = OnlineService.getOnline(playerId);

		if (team == null || team.getLeader() == player) { // 发请者不是队长

			if (team == null || team.size() < Team.MAX_MEMBER) { // 队伍人数已到达上限
				if (invitedPlayer != null) { // 被邀请者不在线
					if (invitedPlayer.getTeam() == null) { // 被邀请者已有队伍
						if (invitedPlayer.getFoes().contains(player.getRoleid())) { // 不能邀请黑名单
							prepareBody();
							body.putShort((short) 1);
							byte[] msg = getUTF8("对方正忙...");
							body.putShort((short) msg.length);
							body.put(msg);

							body.putShort((short) 0);
							sendMsg(player, MsgID.MsgID_Team_Give_Invite_Resp);
						} else if (invitedPlayer.getBattleAgent().getBattle() == null && sendAsk(player, invitedPlayer, 2)) {
							// 回复消息
							// 发送组队请求sendAsk
							/**
							 * MsgID_Team_Give_Invite_Resp:
							 * 
							 * 名称 描述 类型 BodyLength 下面数据的总长度 U32 Result 处理结果 0,成功
							 * 其他失败 U16 ErrorStrlen 错误描述长度（成功为0） U16 ErrorStr
							 * 错误描述 String Other_Data_length 保留数据长 U16
							 * Other_Data 保留数据 Data
							 */
							prepareBody();
							body.putShort((short) 0);
							byte[] msg = getUTF8("成功发送组队消息, 等待对方回应...");
							body.putShort((short) msg.length);
							body.put(msg);

							body.putShort((short) 0);
							sendMsg(player, MsgID.MsgID_Team_Give_Invite_Resp);
						} else {
							prepareBody();
							body.putShort((short) 1);
							byte[] msg;
							if (invitedPlayer.getBattleAgent().getBattle() != null)
								msg = getUTF8("对方正在战斗中...");
							else
								msg = getUTF8("对方正忙...");
							body.putShort((short) msg.length);
							body.put(msg);

							body.putShort((short) 0);
							sendMsg(player, MsgID.MsgID_Team_Give_Invite_Resp);
						}

					} else {
						// 被邀请玩家已有队伍
						prepareBody();
						body.putShort((short) 1);
						byte[] msg = getUTF8("对方已在队伍中...");
						body.putShort((short) msg.length);
						body.put(msg);

						body.putShort((short) 0);
						sendMsg(player, MsgID.MsgID_Team_Give_Invite_Resp);
					}
				} else {
					// 没有找到玩家
					prepareBody();
					body.putShort((short) 1);
					byte[] msg = getUTF8("玩家已下线...");
					body.putShort((short) msg.length);
					body.put(msg);

					body.putShort((short) 0);
					sendMsg(player, MsgID.MsgID_Team_Give_Invite_Resp);
				}
			} else {
				// 队伍人数已到达上限
				prepareBody();
				body.putShort((short) 1);
				byte[] msg = getUTF8("队伍人数已到达上限...");
				body.putShort((short) msg.length);
				body.put(msg);

				body.putShort((short) 0);
				sendMsg(player, MsgID.MsgID_Team_Give_Invite_Resp);
			}
		} else {
			// 不是队长
			prepareBody();
			body.putShort((short) 1);
			byte[] msg = getUTF8("你不是队长, 不能邀请别人...");
			body.putShort((short) msg.length);
			body.put(msg);

			body.putShort((short) 0);
			sendMsg(player, MsgID.MsgID_Team_Give_Invite_Resp);
		}
	}

	private boolean sendAsk(RoleBean fromPlayer, RoleBean toPlayer, int msgType) {

		/**
		 * MsgID_Team_Ask:
		 * 
		 * 名称 描述 类型 BodyLength 下面数据的总长度 U32 Type (0,某人申请入队，队长你是否同意
		 * 
		 * 1 2为同一协议 1,队长邀请你入队，是否同意 2某人邀请和你一起组队，是否同意) U8 Detail_Length 描述的长度 U16
		 * Detail 描述 String Detail_Color 描述的颜色 U32 Other_Data_length 保留数据长 U16
		 * Other_Data 保留数据 Data
		 */
		if (toPlayer.getUnHandledAsk() == null) {
			byte[] msg = getUTF8("未知消息");
			MsgID cmd = null;
			switch (msgType) {
			case 0:
				msg = getUTF8(fromPlayer.getNick() + " 申请加入队伍, 您是否同意?");
				cmd = MsgID.MsgID_Team_Request_Enter;
				break;
			case 1:
			case 2:
				msg = getUTF8(fromPlayer.getNick() + " 邀请你共同组队, 您是否同意?");
				cmd = MsgID.MsgID_Team_Give_Invite;
				break;
			}

			if (cmd == null)
				return false;

			prepareBody();
			body.put((byte) msgType);
			body.putShort((short) msg.length);
			body.put(msg);
			body.putInt(0);// 字体颜色
			body.putShort((short) 0);
			toPlayer.addUnHandledAsk(player.getId(), cmd);
			sendMsg(toPlayer, MsgID.MsgID_Team_Ask);
			return true;
		}
		return false;

	}

	// 掉线队友上线处理
	public void offlineTeammateOnline() {

		boradcastTeamInfo(player);
	}

	private void boradcastTeamInfo(RoleBean toPlayer) {
		prepareBody();

		/**
		 * MsgID_Team_In:
		 * 
		 * 名称 描述 类型 BodyLength 下面数据的总长度 U32 TeamID 队伍号 U32 Team_Role_Num 当前队伍几个人
		 * U8 Team_Role_Data 角色的数据（Team_Role_Data） Data Other_Data_length 保留数据长
		 * U16 Other_Data 保留数据 Data
		 */
		body.putInt(team.id);
		body.put((byte) team.getMember().size());
		for (int i = 0; i < team.getMember().size(); i++) {
			RoleBean teamMember = (RoleBean) team.getMember().get(i);
			body.putInt(teamMember.getId());
			body.put((byte) (i == 0 ? 1 : 0));
			byte[] name = getUTF8(teamMember.getNick());
			body.putShort((short) name.length);
			body.put(name);
			body.putInt(teamMember.getColor());
			body.putShort(teamMember.getCoords().getMap());
			body.putInt(teamMember.getCoords().getX());
			body.putInt(teamMember.getCoords().getY());
			body.putShort((short) 0);
		}
		body.putShort((short) 0);

		sendMsg(toPlayer, MsgID.MsgID_Team_In);
	}

	public void kick(RoleBean member) {
		team.removeMember(player);
		for (int i = 0; i < team.getMember().size(); i++) {
			RoleBean teamMember = (RoleBean) team.getMember().get(i);
			updateTeam(teamMember, member, 3);
		}
	}

	public void broadcastMove() {
		if (player == team.getLeader()) {
			Coords coords = player.getCoords();

			for (int i = 1; i < team.size(); i++) {
				RoleBean teammate = (RoleBean) team.getMember().get(i);

				if (coords.getMap() == teammate.getCoords().getMap())
					GridMapSys.INSTANCE.changeGrid(teammate, coords.getX(), coords.getY());
			}

			for (int i = 0; i < team.size(); i++) {
				RoleBean teammate = (RoleBean) team.getMember().get(i);

				teammate.getMapAgent()._flushNearby();

				if (i > 0 && coords.getMap() == teammate.getCoords().getMap())
					updateTeam(teammate, player, 4);
			}
		}
	}
}
