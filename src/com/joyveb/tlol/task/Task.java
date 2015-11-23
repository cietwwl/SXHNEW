package com.joyveb.tlol.task;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.joyveb.tlol.LuaService;
import com.joyveb.tlol.role.RoleBean;
import com.joyveb.tlol.util.Log;

public class Task {
	public static final int CURVERSION = 2;

	private int version;
	
	/**
	 * Lua中物品模板容器table的名称
	 */
	public static final String LUA_CONTAINER = "TaskSet";
	
	private final HashMap<Integer, TaskState> taskStates = new HashMap<Integer, TaskState>();

	private final HashSet<Integer> monitored = new HashSet<Integer>();

	private final RoleBean owner;
	
	public Task(RoleBean role, final String taskStr) {
		if(role == null)
			throw new IllegalArgumentException("参数 role 不能为空！");
		
		this.owner = role;
		
		if (taskStr == null || taskStr.trim().equals(""))
			return;

		this.version = getVersion(taskStr);

		if (version == 0) {
			Pattern pattern = Pattern.compile("<[\\d\\s]+>");
			Matcher matcher = pattern.matcher(taskStr);
			while (matcher.find()) {
				String find = matcher.group();
				StringTokenizer st = new StringTokenizer(find.substring(1,
						find.length() - 1));

				int taskid = Integer.parseInt(st.nextToken());
				byte step = Byte.parseByte(st.nextToken());

				TaskState taskState = new TaskState(taskid);
				if (step == 2) {
					taskState.step = 0;
				}else {
					taskState.step = step;
					if (st.hasMoreTokens())
						taskState.getSubstate().setFinished(
								Integer.parseInt(st.nextToken()) == 0);
					while (st.hasMoreTokens()) {
						taskState.getSubstate().getStates()
								.add(Integer.parseInt(st.nextToken()));
					}
				}

				taskStates.put(taskid, taskState);
			}
		} else {
			Pattern pattern = Pattern.compile("<[\\d\\s|]+>");
			Matcher matcher = pattern.matcher(taskStr);
			while (matcher.find()) {
				String find = matcher.group();
				String taskStateStr = find.substring(1, find.length() - 1);
				String basic = null;
				String ext = null;

				int index = taskStateStr.indexOf("|");
				if (index < 0 || index == taskStateStr.length() - 1)
					basic = taskStateStr;
				else {
					basic = taskStateStr.substring(0, index);
					ext = taskStateStr.substring(index + 1);
				}

				StringTokenizer basicTok = new StringTokenizer(basic);

				int taskid = Integer.parseInt(basicTok.nextToken());
				byte step = Byte.parseByte(basicTok.nextToken());

				TaskState taskState = new TaskState(taskid);
				taskState.step = step;
				if (basicTok.hasMoreTokens())
					taskState.getSubstate().setFinished(
							Integer.parseInt(basicTok.nextToken()) == 0);
				while (basicTok.hasMoreTokens()) {
					taskState.getSubstate().getStates()
							.add(Integer.parseInt(basicTok.nextToken()));
				}

				if (ext != null) {
					StringTokenizer extTok = new StringTokenizer(ext);
					while (extTok.hasMoreTokens()) {
						taskState.getExtra().add(
								Integer.parseInt(extTok.nextToken()));
					}
				}

				taskStates.put(taskid, taskState);
			}
		}
	}

	private int getVersion(final String taskStr) {
		int index = taskStr.indexOf("<");
		if (index <= 0)
			return 0;

		try {
			return Integer.parseInt(taskStr.substring(0, index).trim());
		} catch (Exception e) {
			Log.error(Log.ERROR, e);
			return 0;
		}
	}
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(CURVERSION);
		for (TaskState state : taskStates.values())
			state.putStateStr(buffer);

		return buffer.toString();
	}

	public final HashMap<Integer, TaskState> getTaskStates() {
		return taskStates;
	}

	public final boolean checkTaskExist(final int taskid) {
		return taskStates.containsKey(taskid);
	}

	public final TaskState getTaskState(final int taskid) {
		return taskStates.get(taskid);
	}

	public final void addTaskState(final TaskState ts) {
		taskStates.put(ts.taskid, ts);
	}

	public final void delTaskState(final int taskid) {
		taskStates.remove(taskid);
		monitored.remove(taskid);
	}

	public final void setVersion(final int version) {
		this.version = version;
	}

	public final int getVersion() {
		return version;
	}

	public void seconedTick(final int curSeconed) {
		Iterator<Integer> it = monitored.iterator();
		while(it.hasNext()) {
			int taskid = it.next();
			try {
				if(LuaService.callOO4Bool(2, LUA_CONTAINER, taskid, "seconedTick", owner, curSeconed))
					it.remove();
			}catch(Exception e) {
				Log.error(Log.ERROR, "角色" + owner.getRoleid() + "任务" + taskid + "监听异常", e);
				it.remove();
			}
		}
	}

	public HashSet<Integer> getMonitored() {
		return monitored;
	}

}
