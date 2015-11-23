package com.joyveb.tlol.chat;

import com.joyveb.tlol.core.IGameCharacter;

public interface IChatChannel {

	byte CHANNEL_PRIVATE = 0;

	byte CHANNEL_TEAM = 1;

	byte CHANNEL_GUILD = 2;

	byte CHANNEL_SYS = 3;

	byte CHANNEL_WORLD = 4;

	String[] NAME = new String[] { "私聊", "队伍", "公会", "系统", "世界" };

	/**
	 * 频道ID
	 * 
	 * @return ID
	 */
	byte getChannelId();

	/**
	 * 接收消息
	 * 
	 * @param sender 
	 * @param msg 
	 */
	void onMessageReceived(IGameCharacter sender, String msg);
}
