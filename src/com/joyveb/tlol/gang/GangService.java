package com.joyveb.tlol.gang;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.joyveb.tlol.Watchable;
import com.joyveb.tlol.db.DataHandler;
import com.joyveb.tlol.db.DataStruct;
import com.joyveb.tlol.db.parser.CommonParser;
import com.joyveb.tlol.db.parser.DbConst;
import com.joyveb.tlol.db.parser.GangCreator;
import com.joyveb.tlol.db.parser.GangDeleter;
import com.joyveb.tlol.db.parser.GangGetter;
import com.joyveb.tlol.role.RoleBean;
import com.joyveb.tlol.role.RoleCardService;
import com.joyveb.tlol.schedule.MinTickHandler;
import com.joyveb.tlol.schedule.OneOffSchedule;
import com.joyveb.tlol.schedule.ScheduleManager;
import com.joyveb.tlol.server.ServerMessage;
import com.joyveb.tlol.uid.UIDManager;
import com.joyveb.tlol.util.Log;

public enum GangService implements DataHandler, MinTickHandler, Watchable {
	INSTANCE;
	
	/** 当前已载入内存的帮派，id为key的映射 */
	private Map<Long, Gang> gangs = new HashMap<Long, Gang>();
	/** 当前已载入内存的帮派，name为key的映射 */
	private Map<String, Gang> gangNames = new HashMap<String, Gang>();
	
	private boolean isLoading = true;
	
	public void loadGang() {
		CommonParser.getInstance().postTask(DbConst.Gang_Get, this, new GangGetter(), true);
		while(isLoading) {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 获取帮派
	 * @param id
	 * @return
	 */
	public Gang getGang(long id) {
		return isGangLoaded(id) ? gangs.get(id) : null;
	}
	
	/**
	 * 查看帮派ID是否存在 且 返回数据是否完整
	 * @param gangid
	 * @return
	 */
	public boolean isGangLoaded(long gangid) {
		return gangs.containsKey(gangid) && gangs.get(gangid).isIntact();
	}
	
	public boolean isGangDiscarded(long gangid) {
		return !gangs.containsKey(gangid);
	}
	
	public boolean isNameExist(String gangName) {
		return gangNames.containsKey(gangName);
	}
	
	public Gang creatGang(RoleBean creator, final String gangName) {
		long gangid = UIDManager.getInstance().getUID();
		creator.setGangid(gangid);
		creator.setJobTitle(GangJobTitle.Leader);
		int leader = creator.getRoleid();
		
		final Gang gang = new Gang().setId(gangid).setName(gangName).setCreated(new Date()).setLeader(leader).setIntact(true);
		gang.addMember(RoleCardService.INSTANCE.getCard(leader));
		
		gangs.put(gang.getId(), gang);
		gangNames.put(gangName, gang);
		
		CommonParser.getInstance().postTask(DbConst.Gang_Creat, null, new GangCreator(gang));
		
		ScheduleManager.INSTANCE.offerTask(new OneOffSchedule() {
			public void execute() {
				ServerMessage.sendBulletin("恭喜【" + RoleCardService.INSTANCE.getCard(gang.getLeader()).getName() 
						+ "】成功创建帮会【" + gang.getName() + "】 ～");
			}
		});
		
		return gang;
	}
    public void addTribute(long gangid ,RoleBean role , int tri){
        Gang gang = getGang(gangid);
    	gang.updateTribute(role, tri);
    } 
	
	
	
	@Override
	public void handle(DbConst eventID, boolean flag, DataStruct ds) {
		switch(eventID) {
		case Gang_Get:
			gangLoaded(ds);
			break;
		case Gang_Delete:
			gangDeleted(ds);
			break;
		}
	}
	
	private void gangLoaded(DataStruct ds) {
		GangGetter getter = (GangGetter) ds;
		// 将数据库里的全部帮会信息读出放入
		for (int i = 0; i < getter.getGang().size(); i++) {
			if (getter.getGang().get(i) != null) {
				gangs.put(getter.getGang().get(i).getId(), getter.getGang()
						.get(i));
				gangNames.put(getter.getGang().get(i).getName(), getter
						.getGang().get(i));

			}
		}

		//Log.info((byte) 0, "init Gangs info is finished！");
		//Log.info((byte) 0, "公会总数量" + gangs.size());
		isLoading = false;
		// loading.remove(getter.getGangid());
	}
	
	private void gangDeleted(DataStruct ds) {
		GangDeleter deleter = (GangDeleter) ds;
		Gang gang = gangs.remove(deleter.getGangid());
		if(gang == null)
			return;
		
		gangNames.remove(gang.getName());
		
		gang.dismissedNotify();
	}
	
	@Override
	public void minTick(int curMin) {
		for(Gang gang : gangs.values())
			gang.minTick(curMin);
	}

	public void dismiss(RoleBean role) {
		if(role.getJobTitle() == GangJobTitle.Leader)
			CommonParser.getInstance().postTask(DbConst.Gang_Delete, this, new GangDeleter(role.getGangid()));
	}

	public void watch() {
		StringBuilder builder = new StringBuilder();
		builder.append("\n已加载帮派数量：" + gangNames.size() + "\n");
		builder.append("名称映射与id映射大小相同：" + (gangs.size() == gangNames.size()) + "\n");
		
		int index = 1;
		
		for(Gang gang : gangs.values()) {
			builder.append(index + ". id【" + gang.getId() + "】帮派【" + gang.getName() + "】\n");
			index++;
		}
		
		Log.info(Log.STDOUT, builder);
	}
	
}
