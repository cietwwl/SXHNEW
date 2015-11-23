package com.joyveb.tlol.community;

import java.util.HashMap;

import com.joyveb.tlol.db.DataHandler;
import com.joyveb.tlol.db.DataStruct;
import com.joyveb.tlol.db.parser.CommonParser;
import com.joyveb.tlol.db.parser.CommunityCreator;
import com.joyveb.tlol.db.parser.CommunityGetter;
import com.joyveb.tlol.db.parser.DbConst;
import com.joyveb.tlol.role.RoleBean;
import com.joyveb.tlol.uid.UIDManager;
import com.joyveb.tlol.util.Log;

public enum Communitys implements DataHandler {
	INSTANCE;

	private HashMap<Long, Community> communitys = new HashMap<Long, Community>();

	private volatile boolean loaded;
	
	public void loadCommunity() {
		Log.info(Log.STDOUT, "Communitys", "加载社区");

		CommonParser.getInstance().postTask(DbConst.Community_Get, this, new CommunityGetter(), true);
	}

	public Community getCommunity(long cid) {
		return communitys.get(cid);
	}

	public Community getCommunityByItem(int itemid) {
		for (Community community : communitys.values())
			if (community.getItemid() == itemid)
				return community;

		return null;
	}

	public Community creatCommunity(int itemid, String cname) {
		Community community = new Community(UIDManager.getInstance().getUID(), itemid, cname);

		communitys.put(community.getId(), community);

		CommonParser.getInstance().postTask(DbConst.Community_Create, null,
				new CommunityCreator(community));

		return community;
	}

	public void addAll(HashMap<Long, Community> dbCommunitys) {
		this.communitys.putAll(dbCommunitys);
	}

	@Override
	public void handle(DbConst eventID, boolean flag, DataStruct ds) {
		switch (eventID) {
		case Community_Get:
			((CommunityGetter) ds).callback();
			this.loaded = true;
			
			Log.info(Log.STDOUT, "Communitys", "加载社区完成");
			break;
		default:
			break;
		}
	}

	public void addRole(RoleBean role) {
		if (communitys.containsKey(role.getCommunity()))
			communitys.get(role.getCommunity()).addMember(role);
	}

	public void setLoaded(boolean loaded) {
		this.loaded = loaded;
	}

	public boolean isLoaded() {
		return loaded;
	}

}
