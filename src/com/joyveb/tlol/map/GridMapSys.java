package com.joyveb.tlol.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.joyveb.tlol.LuaService;
import com.joyveb.tlol.role.RoleBean;

public enum GridMapSys {
	INSTANCE();

	private HashMap<Short, GridMap> gridSystem = new HashMap<Short, GridMap>();

	GridMapSys() {
		LuaService.callLuaFunction("initGridMapSys", gridSystem);
	}

	public void changeGrid(final RoleBean role) {
		changeGrid(role, role.getCoords());
	}

	public void changeGrid(final RoleBean role, final int x, final int y) {
		changeGrid(role, new Coords(role.getCoords().getMap(), x, y));
	}

	public void changeGrid(final RoleBean role, final Coords coords) {
		if (LuaService.call4Bool("checkCoords", coords)) {
			gridSystem.get(role.getCoords().getMap()).remove(role);

			role.freshCoords(coords);

			gridSystem.get(coords.getMap()).add(role);
		}
	}

	public void freshNearby(final RoleBean role) {
		List<Integer> nearby = gridSystem.get(role.getCoords().getMap()).getNearby(role);
		HashMap<Integer, Byte> lastNearby = role.getMapAgent().getNearby();

		for (int rid : lastNearby.keySet())
			lastNearby.put(rid, (byte) 2);

		for (int rid : nearby)
			if (lastNearby.containsKey(rid))
				lastNearby.put(rid, (byte) 1);
			else
				lastNearby.put(rid, (byte) 0);
	}

	public List<Integer> ridInMap(final RoleBean role) {
		return gridSystem.get(role.getCoords().getMap()).ridInMap();
	}

	public void remove(final RoleBean role) {
		gridSystem.get(role.getCoords().getMap()).remove(role);
	}

	/**
	 * @function 得到狮王争霸活动结束还在地图里的所有人
	 * @author LuoSR
	 * @date 2012-3-6
	 */
	public List<Integer> getAllRoleInHegemonyForTimeOut(int mapId) {
		List<Integer> list = new ArrayList<Integer>();
		list = gridSystem.get((short) mapId).ridInMap();
		return list;
	}

}
