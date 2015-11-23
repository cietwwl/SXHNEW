package com.joyveb.tlol;

import javax.management.ObjectName;

public interface TianLongDriverMBean {
	ObjectName getConnectionManager();

	void setConnectionManager(ObjectName connectionManagerName);

	String getTianLongPort();

	void setTianLongPort(String tianLongPort);

	/**
	 * 执行命令
	 * 
	 * @param command
	 */
	void execute(String command);

	void stopService();

	/** 加载公告 */
	void loadBulletin();

	void reloadLua();

	void mailDialog(String roleids, String subject, String content, int gold,
			int tid);

	/**
	 * 
	 * @param writeBackInterval
	 *            回写频率 单位:分钟
	 */
	void changeWriteBackInterval(int writeBackInterval);

	int getWriteBackTaskCount();

	void reloadChargeInfo();

	void watchRoleCard(String name);

	void sendMailToAllRoles(int id, String title, String content);
	
	void loadLoginBulletin();
	void loadFee();
	String getItemId(int num);
	
	void sendWorldChat(String word);
}
