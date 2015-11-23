package com.joyveb.tlol.user;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.joyveb.tlol.MessageSend;
import com.joyveb.tlol.core.AgentProxy;
import com.joyveb.tlol.db.DataHandler;
import com.joyveb.tlol.db.DataStruct;
import com.joyveb.tlol.db.parser.CommonParser;
import com.joyveb.tlol.db.parser.DbConst;
import com.joyveb.tlol.db.parser.MailDelOnRoleDel;
import com.joyveb.tlol.db.parser.QuickEnterRoleData;
import com.joyveb.tlol.db.parser.ReturnMailOnRoleDelData;
import com.joyveb.tlol.db.parser.RoleData;
import com.joyveb.tlol.db.parser.RoleData.RoleDataStruct;
import com.joyveb.tlol.db.parser.RoleListData;
import com.joyveb.tlol.gang.GangService;
import com.joyveb.tlol.net.HoldNetHandler;
import com.joyveb.tlol.net.NetHandler;
import com.joyveb.tlol.protocol.MsgID;
import com.joyveb.tlol.role.Role;
import com.joyveb.tlol.role.RoleCardService;
import com.joyveb.tlol.role.Vocation;
import com.joyveb.tlol.util.Log;

/*用户三次握手建立连接后还未发过消息来的状态*/
public class UserBean extends MessageSend implements DataHandler, HoldNetHandler {

	int userid;
	short zoneid;
	UserAgent userAgent;
	NetHandler clientHandler = null;
	List<Role> roleList = null;

	public UserBean(final NetHandler clientHandler) {
		userAgent = new UserAgent(this);
		this.clientHandler = clientHandler;
	}

	@Override
	public final NetHandler getNetHandler() {
		return clientHandler;
	}

	public final UserAgent getUserAgent() {
		return userAgent;
	}

	public final void setUserAgent(final UserAgent userAgent) {
		this.userAgent = userAgent;
	}

	public final int getUserid() {
		return userid;
	}

	public final void setUserid(final int userid) {
		this.userid = userid;
	}

	@Override
	public final void handle(final DbConst eventID, final boolean flag, final DataStruct ds) {
		if(flag) {
			switch(eventID) {
			case GET_ROLE_LIST:
				sendRoleList(((RoleListData) ds).getRoleList());
				break;
			case ROLE_ADD:
				CommonParser.getInstance().postTask(DbConst.NORMAL_REG_GET_BY_NAME, this, ds);
				Role.LOCK_NAME.remove(((RoleDataStruct) ds).getRoleData().getName());
				break;
			case ROLE_DEL:
				replyMessage(this, 0, MsgID.MsgID_Role_Del_Resp, "删除成功～");

				RoleDataStruct roleDataStruct = (RoleDataStruct) ds;
				RoleData roleData = roleDataStruct.getRoleData();
				ReturnMailOnRoleDelData returnMailOnRoleDelData = new ReturnMailOnRoleDelData();
				returnMailOnRoleDelData.setDeletedRoleId(roleData.getRoleid());
				CommonParser.getInstance().postTask(DbConst.RETRUN_MAIL_ON_ROLE_DEL, null, returnMailOnRoleDelData);

				MailDelOnRoleDel mailDelOnRoleDel = new MailDelOnRoleDel();
				mailDelOnRoleDel.setDeletedRoleId(roleData.getRoleid());
				CommonParser.getInstance().postTask(DbConst.MAIL_DEL_ON_ROLE_DEL, null, mailDelOnRoleDel);

				if(GangService.INSTANCE.isGangLoaded(roleData.getGangid())) {
					GangService.INSTANCE.getGang(roleData.getGangid()).removeMember(
							RoleCardService.INSTANCE.getCard(roleData.getRoleid()));
				}
				RoleCardService.INSTANCE.removeCard(roleData.getRoleid());
				
				removeRole(roleData);

				break;
			case ROLE_QUICK_ENTER_INFO:
				sendQuickEnterResult((QuickEnterRoleData) ds);
				break;
			case ROLE_QUICK_ADD:
				CommonParser.getInstance().postTask(DbConst.ROLE_GET_BY_NAME, this, ds);
				break;
			case ROLE_GET_BY_NAME:
				onRoleGetByName(((RoleData.RoleDataStruct) ds).getRoleData());
				break;
			case NORMAL_REG_GET_BY_NAME:
				onNormalRegGetByName(((RoleData.RoleDataStruct) ds).getRoleData());
				break;
			default:
				Log.error(Log.STDOUT, "UserBean.handle", "event " + eventID + " not handled!");
			}
		}else {
			switch(eventID) {
			case ROLE_QUICK_ADD:
				onQuickRoleAddFailed();
				break;
			default:
				Log.error(Log.STDOUT, "UserBean.handle", "event " + eventID + " not handled!");
			}
		}

	}

	private static ByteBuffer body = AgentProxy.body;

	private void onNormalRegGetByName(final RoleData roleData) {

		if(roleList == null) {
			roleList = new ArrayList<Role>();
		}

		if(roleList.size() > 3) {
			replyMessage(this, 5, MsgID.MsgID_Role_Add_Resp, "创建失败! 角色数量已达到上限");
			return;
		}

		roleList.add(roleData);

		prepareBody();
		putShort((short) 0);
		putString("创建成功～");
		// Log.info(Log.STDOUT, "新创建角色ID为: " + roleData.getRoleid());
		putInt(roleData.getRoleid());
		putShort((short) 0);
		sendMsg(this, MsgID.MsgID_Role_Add_Resp);
	}

	private void onRoleGetByName(final RoleData roleData) {
		roleList = new ArrayList<Role>();
		roleList.add(roleData);

		body.clear();
		body.putInt(0);
		body.putShort((short) 0);
		body.putShort((short) 0);
		body.putInt(roleData.getUserId());
		body.putInt(roleData.getRoleid());
		sendMsg(this, MsgID.MsgID_Role_Quick_Enter_Resp);
	}

	private void onQuickRoleAddFailed() {
		body.clear();
		body.putInt(0);
		body.put((byte) 1);
		byte[] promptBytes = AgentProxy.getUTF8("创建角色失败! 请尝试正常登陆!");
		body.putShort((short) promptBytes.length);
		body.put(promptBytes);

		sendMsg(this, MsgID.MsgID_Role_Quick_Enter_Resp);
	}

	private void sendQuickEnterResult(final QuickEnterRoleData quickEnterRoleData) {
		if(quickEnterRoleData.getRoleData() == null) {
			body.clear();
			body.putInt(0);
			body.putShort((short) 1);
			byte[] msg = AgentProxy.getUTF8("获取角色信息失败, 请尝试普通登录!");
			body.putShort((short) msg.length);
			body.put(msg);
			sendMsg(this, MsgID.MsgID_Role_Quick_Enter_Resp);
		}else {
			roleList = new ArrayList<Role>();
			roleList.add(quickEnterRoleData.getRoleData());

			body.clear();
			body.putInt(0);
			body.putShort((short) 0);
			body.putShort((short) 0);
			body.putInt(quickEnterRoleData.getRoleData().getUserId());
			body.putInt(quickEnterRoleData.getRoleData().getRoleid());
			sendMsg(this, MsgID.MsgID_Role_Quick_Enter_Resp);
		}
	}

	public void sendRoleList(final List<Role> roles) {
		if(roles == null) {
			replyMessage(this, 2, MsgID.MsgID_Role_List_Resp, "登陆失败！");
			return;
		}
		
		short result = 0;
		if(roles.size() == 0)
			result = 1;
		else 
			roleList = roles;

		body.clear();
		body.putInt(0);
		body.putShort(result);
		putString(result == 1 ? "您还未创建角色，赶快试试吧～" : null);

		body.putShort((short) roles.size());
		for(Role role : roles) {
			body.putInt(role.getRoleid());
			putString(role.getName());
			body.put(role.getSex());

			body.put(role.getVocationCode());
			body.putShort(role.getAnimeGroup());
			body.putShort(role.getAnime());
			body.putShort(role.getLevel());

			StringBuffer buffer = new StringBuffer();
			buffer.append("姓名：" + role.getName());
			buffer.append("/声望：" + role.getCharm());
			buffer.append("/等级：" + role.getLevel());
			buffer.append("/职业：" + Vocation.values()[role.getVocationCode()]);

			buffer.append("/力量：" + role.getStrength());
			buffer.append("    智力：" + role.getIntellect());
			buffer.append("/敏捷：" + role.getAgility());

			buffer.append("    体质：" + role.getVitality());

			putString(buffer.toString());
			body.putShort((short) 0);
		}

		body.putShort((short) 0);

		sendMsg(this, MsgID.MsgID_Role_List_Resp);
	}

	public Role getRole(int roleid) {
		for(Role role : roleList)
			if(role.getRoleid() == roleid)
				return role;
		
		return null;
	}

	public List<Role> getRoleList() {
		return roleList;
	}

	public void setRoleList(List<Role> roleList) {
		this.roleList = roleList;
	}
	
	public void removeRole(Role role) {
		Iterator<Role> it = roleList.iterator();
		while(it.hasNext()) {
			if(it.next().getRoleid() == role.getRoleid()) {
				it.remove();
				return;
			}
		}
	}
	
}
