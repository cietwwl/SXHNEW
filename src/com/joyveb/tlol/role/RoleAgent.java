package com.joyveb.tlol.role;

import java.util.HashMap;
import java.util.Set;

import com.joyveb.tlol.LuaService;
import com.joyveb.tlol.OnlineService;
import com.joyveb.tlol.SubModules;
import com.joyveb.tlol.core.AgentProxy;
import com.joyveb.tlol.db.DataHandler;
import com.joyveb.tlol.db.DataStruct;
import com.joyveb.tlol.db.parser.CommonParser;
import com.joyveb.tlol.db.parser.DbConst;
import com.joyveb.tlol.db.parser.LogData;
import com.joyveb.tlol.db.parser.RoleChangeNameExistData;
import com.joyveb.tlol.item.Item;
import com.joyveb.tlol.map.Coords;
import com.joyveb.tlol.map.GridMapSys;
import com.joyveb.tlol.net.IncomingMsg;
import com.joyveb.tlol.protocol.ChangeNameBody;
import com.joyveb.tlol.protocol.MsgID;
import com.joyveb.tlol.protocol.PlusPointBody;
import com.joyveb.tlol.protocol.RoleMenuBody;
import com.joyveb.tlol.protocol.RoleViewMsgBody;
import com.joyveb.tlol.store.InusePack;
import com.joyveb.tlol.store.Store;
import com.joyveb.tlol.task.TaskState;
import com.joyveb.tlol.user.UserBean;
import com.joyveb.tlol.util.Log;

public class RoleAgent extends AgentProxy implements DataHandler {
	public RoleAgent(final RoleBean player) {
		this.player = player;
	}

	@Override
	public final void processCommand(final IncomingMsg message) {
		switch(MsgID.getInstance(message.getHeader().getMsgID())) {
		case MsgID_Role_Menu:
			if(RoleMenuBody.INSTANCE.readBody(message.getBody()))
				roleMenu();
			else
				replyMessage(player, 1, MsgID.MsgID_Role_Menu_Resp, "查看失败！");

			break;
		case MsgID_Manage_Skill_Points:// 玩家属性的加点操作
			if(PlusPointBody.INSTANCE.readBody(message.getBody()))
				plusPoint();
			else
				replyMessage(player, 1, MsgID.MsgID_Manage_Skill_Points_Resp, "加点失败！");

			break;
		case MsgID_Role_Logout:
			roleLogout();
			break;
		case MsgID_Role_ChangeName:
			roleChangeName(message);
			break;
		case MsgID_Role_View:// 查看信息
			if(RoleViewMsgBody.INSTANCE.readBody(message.getBody()))
				roleViewMsg();
			else
				replyMessage(player, 1, MsgID.MsgID_Role_View_Resp, "无法查看！");
			break;
		default:
			break;
		}
	}

	/**
	 * 更改角色名字
	 * 
	 * @param message 改名字的消息
	 */
	private void roleChangeName(final IncomingMsg message) {
		if(ChangeNameBody.INSTANCE.readBody(message.getBody())) {
			ChangeNameBody changeNameBody = ChangeNameBody.INSTANCE;
			String newName = changeNameBody.getNewName();
			if(newName.length() < 2 || newName.length() > 6) {
				replyMessage(player, 7, MsgID.MsgID_Role_ChangeName_Resp, "角色名称长度过"
						+ (newName.length() < 2 ? "短" : "长") + "！");
				return;
			}

			if(newName.replaceAll(" ", "").equalsIgnoreCase("GM") || RoleCardService.INSTANCE.hasCard(newName)
					|| Role.LOCK_NAME.contains(newName)) {
				replyMessage(player, 8, MsgID.MsgID_Role_ChangeName_Resp, "此角色名已存在！");
				return;
			}

			for(char ch : newName.toCharArray()) {
				if(Character.isDigit(ch) || (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z')
						|| (ch >= 0x4e00 && ch <= 0x9fa5))
					continue;
				else {
					replyMessage(player, 2, MsgID.MsgID_Role_ChangeName_Resp, "改名失败，角色名只能为数字、字母或汉字！");
					return;
				}
			}

			if(!LuaService.call4Bool("checkDirtyWord", newName)) {
				replyMessage(player, 3, MsgID.MsgID_Role_ChangeName_Resp, "改名失败，角色名含有非法词汇！");
				return;
			}

			RoleChangeNameExistData roleChangeNameExist = new RoleChangeNameExistData();
			roleChangeNameExist.setNewName(newName);

			Role.LOCK_NAME.add(newName);
			CommonParser.getInstance().postTask(DbConst.Role_ChangeName_EXIST, this, roleChangeNameExist);
		}else {
			replyMessage(player, 1, MsgID.MsgID_Role_ChangeName_Resp, "改名失败！");
		}
	}

	private void roleLogout() {
		UserBean logoutUser = new UserBean(player.getNetHandler());
		player.setNetHandler(null, false);
		OnlineService.addUnLoginUser(logoutUser);

	}

	private void roleMenu() {
		RoleBean destOnline = OnlineService.getOnline(RoleMenuBody.INSTANCE.getUserid());
		if(destOnline == null) {
			replyMessage(player, 1, MsgID.MsgID_Role_Menu_Resp, "查看失败！");
			return;
		}

		body.clear();
		body.putInt(0);
		LuaService.callLuaFunction("fillPlayerMenu");
		body.putShort((short) 0);
		sendMsg(player, MsgID.MsgID_Role_Menu_Resp);
	}

	/** 加点 */
	private void plusPoint() {
		int[] change = PlusPointBody.INSTANCE.getPlus();
		int total = 0;
		for(int plus : change)
			total += plus;

		if(player.getLeftPoint() < total) {
			replyMessage(player, 1, MsgID.MsgID_Manage_Skill_Points_Resp, "加点失败！");
			return;
		}

		player.updateStrength(change[0]);
		player.updateIntellect(change[1]);
		player.updateAgility(change[2]);
		player.updateVitality(change[3]);

		sendMsg(player, MsgID.MsgID_Manage_Skill_Points_Resp, "加点成功～");

		prepareBody();
		SubModules.fillAttributes(player);
		SubModules.fillAttributesDes(player);
		putShort((short) 0);
		sendMsg(player, MsgID.MsgID_Special_Train);
	}

	/** 查看信息 */
	private void roleViewMsg() {
		String str = "";
		int userid = RoleViewMsgBody.INSTANCE.getUserid();
		if(userid == 0) {
			str = "无法查看！";
		}else {
			RoleBean role = OnlineService.getOnline(userid);
			if(role == null) {
				str = "无法查看！";
			}else {
				str = "                        昵称：" + role.getName() + "/等级：" + role.getLevel() + "/    /物攻："
						+ role.getMinPAtk() + "~" + role.getMaxPAtk() + "/物防：" + role.getpDef() + "/法攻： "
						+ role.getMinMAtk() + "~" + role.getMaxMAtk() + "/法防：" + role.getmDef() + "/命中："
						+ role.getHit() + "/躲闪：" + role.getEvade() + "/暴击：" + role.getCrit()
						+ "/  /                        装备列表：/";
				Store store = role.getStore();
				InusePack inusePack = store.getInuse();
				HashMap<Long, Item> map = inusePack.getPackItems();
				Set<Long> set = map.keySet();
				if(!set.isEmpty()) {
					for(Long s : set) {
						str = str + map.get(s).getName() + "/";
					}
				}
			}
		}
		prepareBody();
		putString(str);
		sendMsg(player, MsgID.MsgID_Role_View_Resp);
	}

	/**
	 * @param 断开用户连接
	 */
	public final void userDisconnect() {
		LogData logData = new LogData();
		logData.userId = player.userId;
		logData.roleId = player.getRoleid();
		logData.loginTime = new java.sql.Timestamp(player.getLoginTime());
		logData.logoutTime = new java.sql.Timestamp(new java.util.Date().getTime());
		player.setWriteBacking(true);

		CommonParser.getInstance().postTask(DbConst.ROLE_LOG, null, logData);

		OnlineService.deleteOnline(player.getRoleid());

		GridMapSys.INSTANCE.remove(player);
	}

	@Override
	public final void handle(final DbConst eventID, final boolean flag, final DataStruct ds) {
		RoleChangeNameExistData roleChangeNameExistData = (RoleChangeNameExistData) ds;
		if(flag) {
			switch(eventID) {
			case Role_ChangeName_EXIST:
				if(!roleChangeNameExistData.isIfNameExist()) {
					player.setName(roleChangeNameExistData.getNewName());
					replyMessage(player, 0, MsgID.MsgID_Role_ChangeName_Resp, "改名成功！");
				}else
					replyMessage(player, 8, MsgID.MsgID_Role_ChangeName_Resp, "此角色名已存在！");

				Role.LOCK_NAME.remove(roleChangeNameExistData.getNewName());
				break;
			default:
				Log.info(Log.STDOUT, "handle", "unhandled db call back! : " + eventID);
				break;
			}
		}else {
			switch(eventID) {
			case Role_ChangeName_EXIST:
				replyMessage(player, 1, MsgID.MsgID_Role_ChangeName_Resp, "改名失败！");
				Role.LOCK_NAME.remove(roleChangeNameExistData.getNewName());
				break;
			default:
				Log.info(Log.STDOUT, "handle", "unhandled db call back! : " + eventID);
				break;
			}
		}
	}
}
