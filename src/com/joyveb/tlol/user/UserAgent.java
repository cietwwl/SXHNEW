package com.joyveb.tlol.user;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import com.joyveb.tlol.ConfRedis;
import com.joyveb.tlol.LoginBulletinService;
import com.joyveb.tlol.LuaService;
import com.joyveb.tlol.OnlineService;
import com.joyveb.tlol.RedisCas;
import com.joyveb.tlol.TianLongServer;
import com.joyveb.tlol.core.AgentProxy;
import com.joyveb.tlol.db.DataHandler;
import com.joyveb.tlol.db.DataStruct;
import com.joyveb.tlol.db.parser.CommonParser;
import com.joyveb.tlol.db.parser.DbConst;
import com.joyveb.tlol.db.parser.MailListData;
import com.joyveb.tlol.db.parser.NickCheckData;
import com.joyveb.tlol.db.parser.RoleData;
import com.joyveb.tlol.db.parser.RoleListData;
import com.joyveb.tlol.db.parser.RoleNameExistData;
import com.joyveb.tlol.db.parser.SendMailListData;
import com.joyveb.tlol.gang.GangJobTitle;
import com.joyveb.tlol.map.GridMapSys;
import com.joyveb.tlol.net.IncomingMsg;
import com.joyveb.tlol.net.NetHandler;
import com.joyveb.tlol.protocol.AddRoleBody;
import com.joyveb.tlol.protocol.DeleteRoleBody;
import com.joyveb.tlol.protocol.GetRoleInfoBody;
import com.joyveb.tlol.protocol.ListRoleBody;
import com.joyveb.tlol.protocol.MsgID;
import com.joyveb.tlol.protocol.NickCheckBody;
import com.joyveb.tlol.protocol.RoleQuickEnterBody;
import com.joyveb.tlol.role.Role;
import com.joyveb.tlol.role.RoleBean;
import com.joyveb.tlol.role.RoleCardService;
import com.joyveb.tlol.role.events.LoginEvent;
import com.joyveb.tlol.server.ServerMessage;
import com.joyveb.tlol.transfer.Transfer;
import com.joyveb.tlol.transfer.TransferManager;
import com.joyveb.tlol.trigger.RoleEvent;
import com.joyveb.tlol.uid.UIDManager;
import com.joyveb.tlol.util.Log;

public class UserAgent extends AgentProxy implements DataHandler {
	private UserBean user;
	private byte sex;
	private byte vocation;

	public UserAgent(final UserBean user) {
		this.user = user;
	}

	@Override
	public final void processCommand(final IncomingMsg message) {
		switch (MsgID.getInstance(message.getHeader().getMsgID())) {
		case MsgID_Role_List:// 刚登陆后显示角色列表
			listRole(message);
			break;
		case MsgID_Role_Add:// 添加一个新的角色
			if (AddRoleBody.INSTANCE.readBody(message.getBody()))
				addRole(user);
			else
				replyMessage(user, 1, MsgID.MsgID_Role_Add_Resp, "创建失败！");

			break;
		case MsgID_Role_Del:// 删除已有的角色
			if (DeleteRoleBody.INSTANCE.readBody(message.getBody()))
				deleteRole();
			else
				replyMessage(user, 1, MsgID.MsgID_Role_Add_Resp, "删除失败！");
			break;
		case MsgID_Role_Get_Info:// 获取某个角色的详细信息
			if (GetRoleInfoBody.INSTANCE.readBody(message.getBody()))
				getRoleInfo(user);
			else
				replyMessage(user, 1, MsgID.MsgID_Role_Get_Info_Resp, "获取失败！");

			break;
		case MsgID_Nick_Check:
			NickCheckBody.INSTANCE.readBody(message.getBody());
			checkNick(NickCheckBody.INSTANCE.getNick());
			break;
		case MsgID_Role_Quick_Enter:
			RoleQuickEnterBody.INSTANCE.readBody(message.getBody());
			quickEnter();
			break;
		case MsgID_Hello:
			respHello();
			break;
		case MsgID_Hello_Resp:
			break;
		default:
			Log.error(Log.ERROR, "未处理的协议号:" + message.getHeader().getMsgID());
			break;
		}
	}

	/** 回复客户端探测 */
	private void respHello() {

		// Log.info(Log.STDOUT, "user id " + user.userid + "发送心跳返回");
		body.clear();
		body.putInt(0);
		sendMsg(user, MsgID.MsgID_Hello_Resp);

	}

	private void putRandomRoleData(final RoleData roleData) {
		Random random = new Random();
		int sex = random.nextInt(2);
		int vocation = random.nextInt(3);
		roleData.setSex((byte) sex);
		roleData.setVocation((byte) vocation);
		if (sex == 0 && vocation == 0) {
			roleData.setAnimeGroup((short) 15);
			roleData.setAnime((short) 0);
		} else if (sex == 0 && vocation == 1) {
			roleData.setAnimeGroup((short) 17);
			roleData.setAnime((short) 0);
		} else if (sex == 0 && vocation == 2) {
			roleData.setAnimeGroup((short) 19);
			roleData.setAnime((short) 0);
		} else if (sex == 1 && vocation == 0) {
			roleData.setAnimeGroup((short) 16);
			roleData.setAnime((short) 0);
		} else if (sex == 1 && vocation == 1) {
			roleData.setAnimeGroup((short) 18);
			roleData.setAnime((short) 0);
		} else if (sex == 1 && vocation == 2) {
			roleData.setAnimeGroup((short) 20);
			roleData.setAnime((short) 0);
		}
	}

	private RoleData getRandomPlayer(final int userId, final short zoneId, final byte sex, final byte vocation) {
		RoleData roleData = new RoleData();
		String nick = "游客" + UIDManager.getInstance().getUID();
		roleData.setUserId(userId);
		roleData.setZoneid(zoneId);
		roleData.setName(nick);
		putRandomRoleData(roleData);
		roleData.setRegdate(new Date());

		if (sex == -1 || vocation == -1) {
			putRandomRoleData(roleData);
		} else {
			roleData.setSex(sex);
			roleData.setVocation(vocation);
			if (sex == 0 && vocation == 0) {
				roleData.setAnimeGroup((short) 15);
				roleData.setAnime((short) 0);
			} else if (sex == 0 && vocation == 1) {
				roleData.setAnimeGroup((short) 17);
				roleData.setAnime((short) 0);
			} else if (sex == 0 && vocation == 2) {
				roleData.setAnimeGroup((short) 19);
				roleData.setAnime((short) 0);
			} else if (sex == 1 && vocation == 0) {
				roleData.setAnimeGroup((short) 16);
				roleData.setAnime((short) 0);
			} else if (sex == 1 && vocation == 1) {
				roleData.setAnimeGroup((short) 18);
				roleData.setAnime((short) 0);
			} else if (sex == 1 && vocation == 2) {
				roleData.setAnimeGroup((short) 20);
				roleData.setAnime((short) 0);
			}

		}

		roleData.setLevel((short) 1);
		roleData.setStrength(1);
		roleData.setAgility(1);
		roleData.setIntellect(1);
		roleData.setVitality(1);

		LuaService.callLuaFunction("initRookie", roleData);
		return roleData;
	}

	private void quickEnter() {

		int userId = RoleQuickEnterBody.INSTANCE.getUserId();
		int roleId = RoleQuickEnterBody.INSTANCE.getRoleId();
		short zoneId = RoleQuickEnterBody.INSTANCE.getZoneId();
		sex = RoleQuickEnterBody.INSTANCE.getSex();
		vocation = RoleQuickEnterBody.INSTANCE.getVocation();
		if (OnlineService.isRoleOnline(roleId) && OnlineService.getOnline(roleId).getUserid() != userId) { // 快速判断
			replyMessage(user, 1, MsgID.MsgID_Role_Quick_Enter_Resp, "用户与角色不匹配! 请尝试正常登陆!");
			return;
		}

		user.userid = userId;
		user.zoneid = zoneId;

		RedisCas redisCas = new RedisCas();
		redisCas.setKey(ConfRedis.instance().getKey(userId));
		redisCas.setZoneId(zoneId);
		boolean isSuccess = redisCas.execute(ConfRedis.instance().getKey(userId));
		if (!isSuccess) {
			try {
				// T掉角色
				int roleid = OnlineService.getUserRoleOnline(user.userid);

				RoleBean role = OnlineService.getOnline(roleid);
				if (role != null) {
					GridMapSys.INSTANCE.remove(role);
					role.getNetHandler().close(NetHandler.STATE_CLOSED_BY_APP_LEVEL);
				}
			} catch (Exception e) {
				System.out.println("登录其它服时，找不到 userMap,T掉自己");

			} finally {
				// T掉User
				OnlineService.kickUnUser(user.userid);
			}
			// 断自己
			replyMessage(user, 2, MsgID.MsgID_Role_List_Resp, "登陆失败！");
			user.getNetHandler().close(NetHandler.STATE_CLOSED_BY_APP_LEVEL);
		} else {
			if (OnlineService.isUserOnline(userId)) {
				int olRoleid = OnlineService.getUserRoleOnline(userId);
				if (olRoleid > 0) { // 在role状态
					RoleBean role = OnlineService.getOnline(olRoleid);
					if (roleId == 0 || roleId == olRoleid) { // 此角色在线上
						prepareBody();
						putShort((short) 0);
						putShort((short) 0);
						putInt(role.getUserid());
						putInt(role.getRoleid());
						user.roleList = new ArrayList<Role>();
						user.roleList.add(role);
						sendMsg(user, MsgID.MsgID_Role_Quick_Enter_Resp);
					} else { // 踢掉其他角色
						ServerMessage.orderExit(role, "您的账户已经在其他地方登录！");

						// 玩家刚上线时 如果还在战斗则要求玩家进入战斗
						if (role.getBattle() == null)
							role.getRoleAgent().userDisconnect();
						else if (role.getNetHandler() != null)
							role.getNetHandler().close(NetHandler.STATE_CLOSED_BY_APP_LEVEL);

						// 查数据库中此角色
						CommonParser.getInstance().postTask(DbConst.USER_CHECK_ROLE_INFO, this, new RoleListData(userId, zoneId, role).setRoleid(roleId));
					}
				} else { // 在user状态
					for (UserBean userTemp : OnlineService.getUnLogUsers()) {
						if (userTemp.getUserid() == user.userid) {
							if (!userTemp.getNetHandler().equals(user.getNetHandler())) {
								ServerMessage.orderExit(userTemp, "您的账户已经在其他地方登录！");

								userTemp.userAgent.unloginUserDisconnect();

								if (userTemp.roleList == null) {
									CommonParser.getInstance().postTask(DbConst.USER_CHECK_ROLE_INFO, this, new RoleListData(userId, zoneId, null).setRoleid(roleId));

									break;
								} else
									user.roleList = userTemp.roleList;

								Role role = null;
								if (roleId > 0) {
									role = user.getRole(roleId);
									if (role == null) {
										replyMessage(user, 1, MsgID.MsgID_Role_Quick_Enter_Resp, "用户与角色不匹配! 请尝试正常登陆!");
										return;
									}
								} else if (!userTemp.roleList.isEmpty())
									role = userTemp.roleList.get(0);
								else {
									RoleData roleData = getRandomPlayer(user.userid, user.zoneid, sex, vocation);
									CommonParser.getInstance().postTask(DbConst.ROLE_QUICK_ADD, user, roleData.getRoleDataStruct());

									break;
								}

								body.clear();
								body.putInt(0);
								body.putShort((short) 0);
								body.putShort((short) 0);
								body.putInt(role.getUserId());
								body.putInt(role.getRoleid());
								sendMsg(user, MsgID.MsgID_Role_Quick_Enter_Resp);
							}

							break;
						}
					}
				}
			} else
				CommonParser.getInstance().postTask(DbConst.USER_CHECK_ROLE_INFO, this, new RoleListData(userId, zoneId, null).setRoleid(roleId));

			OnlineService.addUserRole(userId, 0);
		}
	}

	private void checkNick(final String nick) {
		if (nick == null) {
			sendCheckNickErr(user, "角色名不可用！");
			return;
		}

		if (nick.length() < 2 || nick.length() > 6) {
			sendCheckNickErr(user, "角色名称长度过" + (nick.length() < 2 ? "短" : "长") + "！");
			return;
		}

		if (nick.replaceAll(" ", "").equalsIgnoreCase("GM") || RoleCardService.INSTANCE.hasCard(nick) || Role.LOCK_NAME.contains(nick)) {
			sendCheckNickErr(user, "此角色已存在！！");
			return;
		}

		for (char ch : nick.toCharArray()) {
			if (Character.isDigit(ch) || (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || (ch >= 0x4e00 && ch <= 0x9fa5))
				continue;
			else {
				sendCheckNickErr(user, "创建失败，角色名只能为数字、字母或汉字！");
				return;
			}
		}

		if (!LuaService.call4Bool("checkDirtyWord", nick)) {
			sendCheckNickErr(user, "创建失败，角色名含有非法词汇！");
			return;
		}

		Role.LOCK_NAME.add(nick);
		CommonParser.getInstance().postTask(DbConst.ROLE_NAME_EXIST2, this, new NickCheckData(user.zoneid, nick));
	}

	private void sendCheckNickErr(final UserBean user, final String prompt) {
		body.clear();
		body.putInt(0);
		body.put((byte) 0);
		byte[] promptBytes = getUTF8(prompt);
		body.putShort((short) promptBytes.length);
		body.put(promptBytes);

		sendMsg(user, MsgID.MsgID_Nick_Check_Resp);
	}

	/**
	 * 用户请求自己的角色
	 * 
	 * @param message
	 */
	private void listRole(final IncomingMsg message) {
		if (!ListRoleBody.INSTANCE.readBody(message.getBody())) {
			return;
		}

		user.userid = ListRoleBody.INSTANCE.getUserid();
		user.zoneid = ListRoleBody.INSTANCE.getZoneid();

		for (Transfer t : TransferManager.getInstance().getList()) {
			if (t.getUserId() == user.userid) {
				replyMessage(user, 2, MsgID.MsgID_Role_List_Resp, "转服中不允许登录，请等待5分钟才能登录此服，或您可前往转向的新服查看是否有新角色！");
				return;
			}
		}

		RedisCas redisCas = new RedisCas();
		redisCas.setKey(ConfRedis.instance().getKey(user.userid));
		redisCas.setZoneId(user.zoneid);
		boolean isSuccess = redisCas.execute(ConfRedis.instance().getKey(user.userid));

		if (!isSuccess) {
			try {
				// T掉角色
				int roleid = OnlineService.getUserRoleOnline(user.userid);

				RoleBean role = OnlineService.getOnline(roleid);
				if (role != null) {
					GridMapSys.INSTANCE.remove(role);
					role.getNetHandler().close(NetHandler.STATE_CLOSED_BY_APP_LEVEL);
				}
			} catch (Exception e) {
				System.out.println("登录其它服时，找不到 userMap,T掉自己");

			} finally {
				// T掉User
				OnlineService.kickUnUser(user.userid);
			}

			replyMessage(user, 2, MsgID.MsgID_Role_List_Resp, "登陆失败！");
			user.getNetHandler().close(NetHandler.STATE_CLOSED_BY_APP_LEVEL);
		} else {

			if (OnlineService.isUserOnline(user.userid)) {
				int roleid = OnlineService.getUserRoleOnline(user.userid);
				if (roleid > 0) { // 角色在线
					RoleBean role = OnlineService.getOnline(roleid);

					if (role != null) {
						ServerMessage.orderExit(role, "您的账户已经在其他地方登录！");

						// 玩家刚上线时 如果还在战斗则要求玩家进入战斗
						if (role.getBattle() == null)
							role.getRoleAgent().userDisconnect();

						if (role.getNetHandler() != null)
							role.getNetHandler().close(NetHandler.STATE_CLOSED_BY_APP_LEVEL);
					}

					RoleListData roleListData = new RoleListData(user.userid, user.zoneid, role);
					CommonParser.getInstance().postTask(DbConst.GET_ROLE_LIST, user, roleListData);
				} else {
					for (UserBean userTemp : OnlineService.getUnLogUsers()) {
						if (userTemp.getUserid() == user.userid) {
							if (!userTemp.getNetHandler().equals(user.getNetHandler())) {
								ServerMessage.orderExit(userTemp, "您的账户已经在其他地方登录！");
								userTemp.userAgent.unloginUserDisconnect();
							}

							if (userTemp.roleList == null) {
								RoleListData roleListData = new RoleListData(user.userid, user.zoneid, null);
								CommonParser.getInstance().postTask(DbConst.GET_ROLE_LIST, user, roleListData);
							} else {
								user.roleList = userTemp.roleList;
								user.sendRoleList(user.roleList);
							}

							break;
						}
					}
				}
			} else {
				RoleListData roleListData = new RoleListData(user.userid, user.zoneid, null);
				CommonParser.getInstance().postTask(DbConst.GET_ROLE_LIST, user, roleListData);
			}
			if (!OnlineService.getUserMap().containsKey(user.userid)) {
				OnlineService.addUserRole(user.userid, 0);
			}
		}
	}

	public final void unloginUserDisconnect() {
		user.getNetHandler().close(NetHandler.STATE_CLOSED_BY_APP_LEVEL);
		OnlineService.removeUnLoginUser(user);
		OnlineService.delUserRole(user.getUserid());
	}

	/**
	 * 用户创建新角色
	 * 
	 * @param user
	 */
	private void addRole(final UserBean user) {
		AddRoleBody addRoleBody = AddRoleBody.INSTANCE;
		String nick = addRoleBody.getNick();
		if (nick.length() < 2 || nick.length() > 6) {
			replyMessage(user, 7, MsgID.MsgID_Role_Add_Resp, "角色名称长度过" + (nick.length() < 2 ? "短" : "长") + "！");
			return;
		}

		if (nick.replaceAll(" ", "").equalsIgnoreCase("GM") || RoleCardService.INSTANCE.hasCard(nick) || Role.LOCK_NAME.contains(nick)) {
			replyMessage(user, 8, MsgID.MsgID_Role_Add_Resp, "此角色已存在！");
			return;
		}

		for (char ch : nick.toCharArray()) {
			if (Character.isDigit(ch) || (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || (ch >= 0x4e00 && ch <= 0x9fa5))
				continue;
			else {
				replyMessage(user, 2, MsgID.MsgID_Role_Add_Resp, "创建失败，角色名只能为数字、字母或汉字！");
				return;
			}
		}

		if (!LuaService.call4Bool("checkDirtyWord", nick)) {
			replyMessage(user, 3, MsgID.MsgID_Role_Add_Resp, "创建失败，角色名含有非法词汇！");
			return;
		}

		RoleNameExistData roleNameExist = new RoleNameExistData();
		RoleData roleData = new RoleData();
		roleData.setUserId(user.userid);
		roleData.setZoneid(user.zoneid);
		roleData.setName(nick);
		roleData.setAnimeGroup(addRoleBody.getGroupid());
		roleData.setAnime(addRoleBody.getAminiid());
		roleData.setSex(addRoleBody.getSex());
		roleData.setVocation(addRoleBody.getVocation());
		roleData.setRegdate(new Date());

		roleData.setLevel((short) 1);
		roleData.setStrength(1);
		roleData.setAgility(1);
		roleData.setIntellect(1);
		roleData.setVitality(1);

		roleNameExist.roleData = roleData;

		Role.LOCK_NAME.add(nick);
		CommonParser.getInstance().postTask(DbConst.ROLE_NAME_EXIST, this, roleNameExist);
	}

	/** 用户删除角色 */
	private void deleteRole() {
		Role role = user.getRole(DeleteRoleBody.INSTANCE.getRoleid());

		if (role == null) {
			replyMessage(user, 2, MsgID.MsgID_Role_Del_Resp, "删除失败！");
			return;
		}

		if (GangJobTitle.getInstance(role.jobTitle()) == GangJobTitle.Leader) {
			replyMessage(user, 3, MsgID.MsgID_Role_Del_Resp, "删除失败，帮主不能被删除！");
			return;
		}

		Log.info(Log.ROLE, role.getZoneid() + "#$" + role.getUserId() + "#$" + role.getRoleid() + "#$" + role.getName() + "#$" + role.getLevel() + "#$" + role.getVocationCode() + "#$" + role.getSex()
				+ "#$" + role.getGold() + "#$" + role.getStoreStr() + "#$" + role.getSkillStr() + "#$" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "#$" + TianLongServer.srvId);

		CommonParser.getInstance().postTask(DbConst.ROLE_DEL, user, role.getRoleDataStruct());
	}

	/**
	 * 获取角色详细信息，此时进行具体的角色上线
	 * 
	 * @param user
	 */
	private void getRoleInfo(final UserBean user) {
		// 无需再判断是否在线 踢人提升到用户级别而不是角色级别------不T不行
		/*
		 * 判断在线改到这里，一个角色上线时检查，他同区是否有角色在线。如果有角色在线，T掉双方，被T直接关掉，T人的需要重新登陆， 给回写的时间。
		 */

		// 在此检测

		// 登陆用户（登陆到角色列表会存一个uid一个0，登陆到游戏会存uid,rid，如果点重选角色，会把userMap里的这条清掉）
		// 如果是已登陆用户，判断在不在userMap中，如果在，就得到他的rid,rid为0则说明玩家在角色列表，
		// rid>0则说明玩家角色在线，在线则T掉，同时T掉请求登陆的用户，rid为0则为正常登陆
		int roleid = 0;
		if (OnlineService.getUserMap().containsKey(user.userid)) {
			roleid = OnlineService.getUserRoleOnline(user.userid);
		}

		// if (roleid == 0) {

		RoleBean roleBean = OnlineService.getOnline(GetRoleInfoBody.INSTANCE.getRoleid());

		if (roleBean == null) {
			Role role = getRole(GetRoleInfoBody.INSTANCE.getRoleid());

			if (role == null) {
				replyMessage(user, 2, MsgID.MsgID_Role_Get_Info_Resp, "获取角色信息失败！");
				return;
			}

			roleBean = role.toRole();

			if (role instanceof RoleData) {
				MailListData mailListData = new MailListData(roleBean.getRoleid(), roleBean.getName());
				CommonParser.getInstance().postTask(DbConst.MAIL_LIST, roleBean, mailListData);
				SendMailListData sendMailListData = new SendMailListData(roleBean.getRoleid(), roleBean.getName());
				CommonParser.getInstance().postTask(DbConst.SEND_MAILL_LIST, roleBean, sendMailListData);
			}

			OnlineService.addOnline(roleBean);

			roleBean.setLoginTime(System.currentTimeMillis());
		}

		roleBean.setNetHandler(user.getNetHandler());
		OnlineService.addUserRole(user.userid, roleBean.getRoleid());
		OnlineService.removeUnLoginUser(user);

		LuaService.callLuaFunction("roleOnline", roleBean);
		// 新公告
		String bulletinContent = LoginBulletinService.INSTANCE.getBulletinContent();// 得到公告内容

		if (bulletinContent != null && !"".equals(bulletinContent)) {
			// 向客户端发送回应协议
			sendMsg(roleBean, MsgID.MsgID_Notice_Resp, bulletinContent);
		}

		RoleEvent.OnlineEvent.handleEvent(roleBean);
		LoginEvent loginEvent = new LoginEvent(roleBean);
		roleBean.onOnline(loginEvent);
		roleBean.setLogOnTime(System.currentTimeMillis());
		Log.info(Log.STDOUT, "【" + roleBean.getRoleid() + "】【" + roleBean.getNick() + "】上线了");

		// } else {
		// RoleBean role = OnlineService.getOnline(roleid);
		// ServerMessage.orderExit(role, "您的账户已经在其他地方登录！");
		// if (role != null) {
		// if (role.getBattle() == null) {
		// role.getRoleAgent().userDisconnect();
		// }
		// if (role.getNetHandler() != null) {
		//
		// role.getNetHandler().close(
		// NetHandler.STATE_CLOSED_BY_APP_LEVEL);
		// }
		//
		// if (user.getNetHandler() != null) {
		// user.getNetHandler().close(
		// NetHandler.STATE_CLOSED_BY_APP_LEVEL);
		// }
		//
		// }
		// }
	}

	@Override
	public final void handle(final DbConst eventID, final boolean flag, final DataStruct ds) {
		switch (eventID) {
		case ROLE_NAME_EXIST:
			RoleNameExistData roleNameExist = (RoleNameExistData) ds;
			if (roleNameExist.ifNameExist) {
				replyMessage(user, 4, MsgID.MsgID_Role_Add_Resp, "创建失败，此角色名称已存在！");
				Role.LOCK_NAME.remove(roleNameExist.roleData.getName());
			} else {
				RoleData roleData = roleNameExist.roleData;
				if (user.roleList != null && user.roleList.size() >= 3) {
					replyMessage(user, 5, MsgID.MsgID_Role_Add_Resp, "创建失败! 角色数量已达到上限");
					Role.LOCK_NAME.remove(roleNameExist.roleData.getName());
				} else {
					LuaService.callLuaFunction("initRookie", roleData);
					CommonParser.getInstance().postTask(DbConst.ROLE_ADD, user, roleData.getRoleDataStruct());
				}
			}
			break;
		case ROLE_NAME_EXIST2:
			NickCheckData nickCheckData = (NickCheckData) ds;
			if (nickCheckData.ifNameExist) {
				sendCheckNickErr(user, "此角色名称已存在！");
			} else {
				body.clear();
				body.putInt(0);
				body.put((byte) 1);
				sendMsg(user, MsgID.MsgID_Nick_Check_Resp);
			}

			Role.LOCK_NAME.remove(nickCheckData.nick);
			break;
		case USER_CHECK_ROLE_INFO:
			RoleListData roleListData = (RoleListData) ds;

			if (roleListData.getRoleid() == 0 && roleListData.getRoleList().isEmpty()) {
				RoleData roleData = getRandomPlayer(user.userid, user.zoneid, sex, vocation);
				CommonParser.getInstance().postTask(DbConst.ROLE_QUICK_ADD, user, roleData.getRoleDataStruct());
			} else {
				Role role = null;
				if (roleListData.getRoleid() == 0) {
					if (!roleListData.getRoleList().isEmpty())
						role = roleListData.getRoleList().get(0);
				} else {
					for (Role data : roleListData.getRoleList()) {
						if (data.getRoleid() == roleListData.getRoleid()) {
							role = data;
							break;
						}
					}
				}

				if (role != null) {
					user.roleList = roleListData.getRoleList();

					body.clear();
					body.putInt(0);
					body.putShort((short) 0);
					body.putShort((short) 0);
					body.putInt(role.getUserId());
					body.putInt(role.getRoleid());
					sendMsg(user, MsgID.MsgID_Role_Quick_Enter_Resp);
				} else {
					body.clear();
					body.putInt(0);
					body.putShort((short) 1);
					putString("登陆失败! 请尝试正常登陆!");

					sendMsg(user, MsgID.MsgID_Role_Quick_Enter_Resp);
				}
			}

			break;
		default:
			Log.info(Log.STDOUT, "handle", "unhandled db call back! : " + eventID);
			break;
		}
	}

	private Role getRole(final int roleid) {
		if (user.roleList == null)
			return null;

		for (Role role : user.roleList)
			if (role.getRoleid() == roleid)
				return role;

		return null;
	}

}
