package com.joyveb.tlol;

import java.util.Date;

import javax.management.ObjectName;

import com.joyveb.tlol.charge.ShenZhouChargeInfo;
import com.joyveb.tlol.db.TaskManager;
import com.joyveb.tlol.fee.FeeService;
import com.joyveb.tlol.listener.RoleDataSaver;
import com.joyveb.tlol.mail.MailManager;
import com.joyveb.tlol.mailnotice.MailNoticeManager;
import com.joyveb.tlol.role.RoleCard;
import com.joyveb.tlol.role.RoleCardService;
import com.joyveb.tlol.schedule.Broadcast;
import com.joyveb.tlol.schedule.OneOffSchedule;
import com.joyveb.tlol.schedule.ScheduleManager;
import com.joyveb.tlol.uid.UIDManager;
import com.joyveb.tlol.util.Log;
import com.joyveb.tlol.util.UID;


public class TianLongDriver implements TianLongDriverMBean {
	private String TianLongPort;
	private TianLongServer tianLongServer;
	private ObjectName connectionManagerName;

	protected final void startService(final int port, final String luaPath, final String luaResource)
			throws Exception {
		tianLongServer = new TianLongServer(port, luaPath, luaResource);
	}
	@Override
	public final void stopService() {
		if (tianLongServer != null) {
			try {
				tianLongServer.stopTianLongServer();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			tianLongServer = null;
		}

		Log.info(Log.STDOUT, "TIANLONGBABU stopped!!");
	}
	@Override
	public final ObjectName getConnectionManager() {
		return connectionManagerName;
	}
	@Override
	public final void setConnectionManager(final ObjectName connectionManagerName) {
		this.connectionManagerName = connectionManagerName;
	}
	@Override
	public final String getTianLongPort() {
		return TianLongPort;
	}
	@Override
	public final void setTianLongPort(final String tianLongPort) {
		this.TianLongPort = tianLongPort;
	}

	@Override
	public final void loadBulletin() {
		BulletinService.INSTANCE.loadBulletin();
	}

	@Override
	public final void execute(final String command) {
		ScheduleManager.INSTANCE.offerTask(new OneOffSchedule() {
			@Override
			public void execute() {
				LuaService.callLuaFunction("parseCommand", command, true);
			}
		});
	}

	@Override
	public final void reloadLua() {
		ScheduleManager.INSTANCE.offerTask(new OneOffSchedule() {
			@Override
			public void execute() {
				LuaService.loadLua();
			}
		});
	}

	@Override
	public final void mailDialog(final String roleids, final String subject,
			final String content, final int gold, final int tid) {
		ScheduleManager.INSTANCE.offerTask(new OneOffSchedule() {
			@Override
			public void execute() {
				for (String idStr : roleids.split(",")) {
					try {
						MailManager.getInstance().send_GM_Mail(
								Integer.parseInt(idStr), subject, content,
								gold, tid);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

	@Override
	public final void changeWriteBackInterval(final int writeBackInterval) {
		RoleDataSaver.setWrite_Back_Interval(writeBackInterval);
	}

	@Override
	public final int getWriteBackTaskCount() {
		return TaskManager.dbTaskCount;
	}

	@Override
	public final void reloadChargeInfo() {
		ShenZhouChargeInfo.getInstance().reloadChargeInfo();
	}

	@Override
	public final void watchRoleCard(final String name) {
		ScheduleManager.INSTANCE.offerTask(new OneOffSchedule() {
			@Override
			public void execute() {
				RoleCard card = RoleCardService.INSTANCE.getCard(name);
				Log.info(Log.STDOUT, card == null ? "角色【" + name + "】名片尚未加载" : card);
			}
		});
	}
	@Override
	public void sendMailToAllRoles(int id, String title, String content) {
		Log.info(Log.STDOUT, "loadMailNotice", "加载邮件公告");
		MailNoticeManager.getInstance().sendMailToAllRoles(id, new Date(),
				title, content);
		Log.info(Log.STDOUT, "loadMailNotice", "加载邮件公告完成");
	}
	@Override
	public void loadLoginBulletin() {
		LoginBulletinService.INSTANCE.loadLoginBulletin();
		
	}
	@Override
	public void loadFee() {
		FeeService.INSTANCE.loadFee();
		
	}
	@Override
	public String getItemId(int num) {
		String result = "KRXHLYDivT";
		for (int i = 0; i < num; i++) {
			result += UID.next();
			if (i < num - 1) {
				result += ":";
			}
		}
		if (result.equals("KRXHLYDivT")) {
			result += "nonevqEVW" + UIDManager.getInstance().getUID();
		} else {
			result += "vqEVW" + UIDManager.getInstance().getUID();
		}
		return result + "KRXHLYDivT";
	}

	@Override
	public void sendWorldChat(String word) {
		Broadcast.send(word);
	}

}
