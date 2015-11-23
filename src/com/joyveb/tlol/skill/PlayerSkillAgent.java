/**
 * 
 */
package com.joyveb.tlol.skill;

import com.joyveb.tlol.LuaService;
import com.joyveb.tlol.core.AgentProxy;
import com.joyveb.tlol.net.IncomingMsg;
import com.joyveb.tlol.role.RoleBean;

/**
 * @author zhongyuan
 * 
 */
public class PlayerSkillAgent extends AgentProxy {

	/**
	 * @param player
	 *            TODO
	 * 
	 */
	public PlayerSkillAgent(final RoleBean player) {
		this.player = player;
	}

	/**
	 * 处理网络协议
	 * 
	 * @param msg
	 *            TODO
	 */
	public void processCommand(final IncomingMsg msg) {

	}

	@Deprecated
	public final int getSkillLv(final long skillId) {
		return LuaService.call4Int("getSkillLv", player.getId(), skillId);
	}

	public final boolean checkCondition(final long skillId) {
		if (player.getSkillLv(skillId) != 0) {
			int costMp = LuaService.call4Int("getManaCost", skillId,
					player.getSkillLv(skillId));
			return player.getMP() >= costMp;
		}

		return false;
	}

}
