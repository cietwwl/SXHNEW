package com.joyveb.tlol.map;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import com.joyveb.tlol.LuaService;
import com.joyveb.tlol.OnlineService;
import com.joyveb.tlol.core.AgentProxy;
import com.joyveb.tlol.gang.GangService;
import com.joyveb.tlol.net.IncomingMsg;
import com.joyveb.tlol.protocol.EnterMapBody;
import com.joyveb.tlol.protocol.EnterMapStartBody;
import com.joyveb.tlol.protocol.FindPathBody;
import com.joyveb.tlol.protocol.MsgID;
import com.joyveb.tlol.protocol.RequestMapBody;
import com.joyveb.tlol.protocol.RoleArroundBody;
import com.joyveb.tlol.protocol.RoleMoveBody;
import com.joyveb.tlol.protocol.TransToMapBody;
import com.joyveb.tlol.role.RoleBean;
import com.joyveb.tlol.util.Log;

public class MapAgent extends AgentProxy {
	private long lastUpdate;

	/** 角色id及状态，0表示添加，1表示更新，2表示删除 */
	private HashMap<Integer, Byte> nearby = new HashMap<Integer, Byte>();

	public MapAgent(final RoleBean player) {
		this.player = player;
	}

	@Override
	public final void processCommand(final IncomingMsg message) {
		switch (MsgID.getInstance(message.getHeader().getMsgID())) {
		case MsgID_Enter_Map:// 进入某等级地图的某小地图
			if (EnterMapBody.INSTANCE.readBody(message.getBody())) {
				if (EnterMapBody.INSTANCE.getTransid() == 0)
					
					firstEnterMap();
				else
					enterMap(EnterMapBody.INSTANCE);
			} else
				replyMessage(player, 1, MsgID.MsgID_Enter_Map_Resp, "进入地图失败！");
			break;
		case MsgID_Enter_Map_Start:// 进入某等级地图
			if (EnterMapStartBody.INSTANCE.readBody(message.getBody())) {
				GridMapSys.INSTANCE.changeGrid(player, EnterMapStartBody.INSTANCE.getCoords());
				this.nearby.clear();
			}
			break;
		case MsgID_Get_MapInfo:// 客户端请求获取地图数据
			if (RequestMapBody.INSTANCE.readBody(message.getBody()))
				requestMap();
			else
				replyMessage(player, 1, MsgID.MsgID_Get_MapInfo_Resp, "获取地图失败，请稍后再试～");
			break;
		case MsgID_Find_Point_Address:
			if (FindPathBody.INSTANCE.readBody(message.getBody()))
				LuaService.callLuaFunction("findPath", player);
			else
				replyMessage(player, 1, MsgID.MsgID_Find_Point_Address_Resp, "查找失败，请稍后再试～");

			break;
		case MsgID_Role_Arround:
			if (RoleArroundBody.INSTANCE.readBody(message.getBody()))
				roleArround(RoleArroundBody.INSTANCE.getMapx(), RoleArroundBody.INSTANCE.getMapy());
			else
				replyMessage(player, 1, MsgID.MsgID_Role_Arround_Resp, "删除失败！");
			break;
		case MsgID_Manage_Move:// 移动到某个位置
			if (RoleMoveBody.INSTANCE.readBody(message.getBody()))
				roleMove(RoleMoveBody.INSTANCE.getMapX(), RoleMoveBody.INSTANCE.getMapY());
			else
				Log.error(Log.STDOUT, "roleMove", RoleMoveBody.INSTANCE);
			break;
		case MsgID_Get_TransToMap: // 地图传送 将玩家直接传送至某个地图
			if (TransToMapBody.INSTANCE.readBody(message.getBody())) {
				Log.info((byte) 0, "传送者：" + player.getId());
				LuaService.callLuaFunction("TransPlayerToAnotherMap", player, TransToMapBody.INSTANCE.getMapId());
			}
			else
				replyMessage(player, 1, MsgID.MsgID_Get_TransToMap_Resp, "数据信息有误！");
			break;
			
		case MsgID_Get_TransMaps: //向客户端发送主城地图传送信息
				getTransMapInfo();
			
			break;
		default:
			break;
		}
	}

	public final boolean isInSameMap(final RoleBean otherPlayer) {
		return player.getCoords().getMap() == otherPlayer.getCoords().getMap();
	}

	private void firstEnterMap() {
		//检查是否要传送进193或者203- 205的地图是的话将其传送至123
		 int mapId = player.getCoords().getMap();
		 if(mapId == 205 || mapId == 204 || mapId == 203 || mapId == 193) {
			 player.getCoords().setMap((short) 123);
			double middle = Math.random() * 2 * Math.PI;
			double x = 165 + Math.sin(middle) * 16;
			double y = 136 + Math.sin(middle) * 16;
			 player.getCoords().setX((int)x);
			 player.getCoords().setY((int)y);
         }
		GridMapSys.INSTANCE.changeGrid(player);

		if (player.getTeam() != null)// 判断玩家掉线时 是否有队伍
			player.getTeamAgent().offlineTeammateOnline();

		if (player.getBattle() != null)// 玩家刚上线时 如果还在战斗则要求玩家进入战斗
			player.getBattleAgent().sendFightOrderTo(player);

		LuaService.callLuaFunction("enterMap", player);

		player.getBuffManager().send();
	}

	private void enterMap(final EnterMapBody enterMapBody) {
		Coords coords = LuaService.call4Object("getDestCoords", player);
		if (coords == null || (coords != null && !coords.nonNegative())) {
			replyMessage(player, 2, MsgID.MsgID_Enter_Map_Resp, "进入地图失败！");
			return;
		}

		LuaService.callLuaFunction("enterMap", player, coords);

		/** 有队伍并且自己是队长 让队友们过地图 */
		if (player.getBattle() == null && player.getTeam() != null && player.getTeam().getLeader() == player)
			for (int i = 1; i < player.getTeam().size(); i++)
				LuaService.callLuaFunction("forceMap", player.getTeam().getMember().get(i), coords);
	}

	/** 客户端请求地图数据 */
	private void requestMap() {
		int areaid = RequestMapBody.INSTANCE.getArea();

		if (!LuaService.call4Bool("checkArea", player, areaid))
			return;

		body.clear();
		body.putInt(0); // 长度
		body.putShort((short) 0); // 成功
		body.putShort((short) 0); // 错误长度

		if (areaid == 0) {
			LuaService.callLuaFunction("fillAreas");
			areaid = LuaService.call4Int("getArea", player.getCoords().getMap());
		} else
			body.putShort((short) 0); // 本次返回区域地图数量

		LuaService.callLuaFunction("fillArea", areaid);

		body.putShort((short) 0); // 保留数据长度
		sendMsg(player, MsgID.MsgID_Get_MapInfo_Resp);
	}

	private void roleArround(final int x, final int y) {
		body.clear();
		body.putInt(0);

		GridMapSys.INSTANCE.changeGrid(player, x, y);

		body.mark();
		body.put((byte) 0);

		int wrote = 0;
		for (int rid : nearby.keySet()) {
			RoleBean other = OnlineService.getOnline(rid);
			if (other != null) {
				wrote++;
				putInt(other.getId());
				putString(other.getNick());
				putInt(other.getColor());
			}
			if (wrote >= 30)
				break;
		}

		if (wrote > 0)
			LuaService.callLuaFunction("fillPlayerMenu");
		else
			body.put((byte) 0); // 菜单数量

		body.putShort((short) 0);
		body.limit(body.position());
		body.reset();
		body.put((byte) wrote);

		body.position(body.limit());

		sendMsg(player, MsgID.MsgID_Role_Arround_Resp);
	}

	private void roleMove(final int x, final int y) {
		body.clear();
		body.putInt(0);
		sendMsg(player, MsgID.MsgID_Manage_Move_Resp);

		if (player.getTeam() == null || player.getTeam().getLeader() == player)
			GridMapSys.INSTANCE.changeGrid(player, x, y);

		if (player.getTeam() != null && player.getTeam().getLeader() == player)
			player.getTeamAgent().broadcastMove();
	}

	public final void flushNearby() {
		long timeMillis = System.currentTimeMillis();

		if (lastUpdate <= 0) {
			lastUpdate = timeMillis;
			return;
		}

		if (timeMillis - lastUpdate < 1000 * 3)
			return;

		_flushNearby();
	}

	public final void _flushNearby() {
		GridMapSys.INSTANCE.freshNearby(player); // 更新附近角色
		if (nearby.containsKey(player.getRoleid()))
			Log.error(Log.STDOUT, "flushNearby", "周围玩家列表中有自己！");

		lastUpdate = System.currentTimeMillis();

		if (nearby.isEmpty() || player.getCoords().getMap() <= 1 || player.getCoords().getMap() == 202) // 前三地图不同步周围列表
			return;

		prepareBody();

		putShort((short) nearby.size()); // 数量

		Iterator<Entry<Integer, Byte>> it = nearby.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Integer, Byte> entry = it.next();
			RoleBean role = OnlineService.getOnline(entry.getKey());
			if (role == null)
				entry.setValue((byte) 2);

			putByte(entry.getValue());
			putInt(entry.getKey());
			if (entry.getValue() == 2)
				it.remove();
			else if (entry.getValue() == 1) {
				putInt(role.getRoleState());
				putByte(role.getVIPLevel());
				putInt(role.getCoords().getX());
				putInt(role.getCoords().getY());
				putInt(role.getColor());
				if (role.getIsNotFatwa()) {
					putString("[杀]");

				} else {
					putString(" ");
				}
				role.colorJudgement();
			} else {
				putInt(role.getRoleState());
				putByte(role.getVIPLevel());
				putInt(role.getCoords().getX());
				putInt(role.getCoords().getY());

				putString(role.getEpithet());
				putInt(Color.MAGENTA.getRGB()); // 称号名称颜色

				if (GangService.INSTANCE.isGangLoaded(role.getGangid())) {
					putString(GangService.INSTANCE.getGang(role.getGangid()).getName() + role.getJobTitle().getDesAtTitle());
					putInt(0x009ad6); // 帮派名称颜色
				} else {
					putString(null);
					putInt(0); // 帮派名称颜色
				}

				putString(role.getNick());
				putInt(role.getColor());

				if (role.getIsNotFatwa()) {
					putString("[杀]");

				} else {
					putString(" ");
				}
				role.colorJudgement();

				putByte(role.getSex());
				putShort(role.getAnimeGroup());
				putShort(role.getAnime());
			}
		}

		putShort((short) 0);

		sendMsg(player, MsgID.MsgID_Role_Other_Init);
	}

	private final void getTransMapInfo() {
		  Log.info((byte) 0, "请求传送地图！！");
		List<MainMapInfo> mapInfos = new ArrayList<MainMapInfo>();

		LuaService.callLuaFunction("GetTransMaps", player, mapInfos);

		 Collections.sort(mapInfos, new Comparator<MainMapInfo>() {  
	            public int compare(MainMapInfo o1, MainMapInfo o2) {  
	                int result = o1.getLevel() - o2.getLevel();  
	                return result;  
	            }  
	        });  
		prepareBody();
		int enableNum = mapInfos.size();
		putShort((short) mapInfos.size());
		for (MainMapInfo mapInfo : mapInfos) {
			putString(mapInfo.getName());
			putShort((short) mapInfo.getId());
			putInt(mapInfo.getHexColor());
			if (!mapInfo.getEnable())
				-- enableNum;
		}
		putShort((short) enableNum);
		putShort((short) 0);
		sendMsg(player, MsgID.MsgID_Get_TransMaps_Resp);
		  Log.info((byte) 0, "请求传送地图成功！！");
	}

	public final HashMap<Integer, Byte> getNearby() {
		return nearby;
	}

	public final void setNearby(final HashMap<Integer, Byte> nearby) {
		this.nearby = nearby;
	}

	public final void setLastUpdate(final long lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public final long getLastUpdate() {
		return lastUpdate;
	}

	public final boolean canPvP(final short mapId) {
		return LuaService.call4Bool("canPvP", mapId);
	}
}
