package com.joyveb.tlol.map;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.joyveb.tlol.role.RoleBean;
import com.joyveb.tlol.util.Log;

public class GridMap {
	public static final int NEARBY_SIZE = 10;

	public static final int GRID_SIZE = 64;

	private int width;
	private int height;

	private ArrayList<HashSet<Integer>> rolesAtGrids = new ArrayList<HashSet<Integer>>();

	public GridMap(final int width, final int height) {
		this.width = width;
		this.height = height;

		for (int i = 0; i < width * height; i++)
			rolesAtGrids.add(new HashSet<Integer>());
	}

	private int calcGrid(final RoleBean role) {
		int x = role.getCoords().getX();
		if (x == GRID_SIZE * width)
			x = x - 1;

		int y = role.getCoords().getY();
		if (y == GRID_SIZE * height)
			y = y - 1;

		return x / GRID_SIZE + y / GRID_SIZE * width;
	}

	public final List<Integer> getNearby(final RoleBean role) {
		int curgid = calcGrid(role);

		ArrayDeque<Integer> queue = new ArrayDeque<Integer>();
		queue.add(curgid);

		ArrayList<Integer> nearby = new ArrayList<Integer>();

		HashSet<Integer> visited = new HashSet<Integer>();

		/** 广度优先搜索 */
		while (!queue.isEmpty()) {
			int gid = queue.removeFirst();

			if (visited.contains(gid))
				continue;

			if (gid >= rolesAtGrids.size() || gid < 0) {
				Log.error(Log.ERROR, "GID: " + gid + " MAP: " + role.getCoords() + " WIDTH: " + width + " HEIGHT: " + height);
				continue;
			}

			nearby.addAll(rolesAtGrids.get(gid));
			visited.add(gid);

			if (nearby.size() > NEARBY_SIZE + 1) {
				nearby.remove(Integer.valueOf(role.getRoleid()));
				return nearby.subList(0, NEARBY_SIZE);
			}

			int row = gid / width;
			int col = gid - row * width;

			if (col > 0)
				queue.add(gid - 1);

			if (row > 0)
				queue.add(gid - width);

			if (col < width - 1)
				queue.add(gid + 1);

			if (row < height - 1)
				queue.add(gid + width);

			if (queue.isEmpty())
				break;
		}

		nearby.remove(Integer.valueOf(role.getRoleid()));
		return nearby;
	}

	public final void remove(final RoleBean role) {
		rolesAtGrids.get(calcGrid(role)).remove(role.getRoleid());
	}

	public final void add(final RoleBean role) {
		int grid = calcGrid(role);
		if (grid >= 0 && grid < rolesAtGrids.size())
			rolesAtGrids.get(grid).add(role.getRoleid());
	}

	public final List<Integer> ridInMap() {
		ArrayList<Integer> sameMap = new ArrayList<Integer>();
		for (HashSet<Integer> grid : rolesAtGrids)
			sameMap.addAll(grid);

		return sameMap;
	}

}
