package com.joyveb.tlol.billboard;

import java.util.ArrayList;

import com.joyveb.tlol.role.RoleCard;
import com.joyveb.tlol.util.Log;

public enum TopRatedService {
	INSTANCE();

	private ArrayList<TopRated<RoleCard>> tops = new ArrayList<TopRated<RoleCard>>();

	private TopRatedService() {
		tops.add(TopLevel.INSTANCE);
		tops.add(TopGold.INSTANCE);
		tops.add(TopCharm.INSTANCE);
		tops.add(TopMark.INSTANCE);
		tops.add(TopTotalKillNum.INSTANCE);
		tops.add(TopHonor.INSTANCE);
	}

	public void loadTopRated() {
		Log.info(Log.STDOUT, "TopRated", "加载排行榜");
		for(TopRated<RoleCard> top : tops) 
			top.loadTopRated();
	}

	public TopRated<RoleCard> getTopRated(final int type) {
		return tops.get(type - 1);
	}

	public int getTopRatedSize() {
		return tops.size();
	}

	public void sendBulletin() {
		for(TopRated<RoleCard> top : tops)
			if(!top.isEmpty()) 
				top.sendBulletin();
	}

	public void watch() {
		for(TopRated<RoleCard> top : tops) {
			top.watch();
		}
	}
	
	public boolean isLoaded() {
		for(TopRated<RoleCard> top : tops) 
			if(!top.isLoaded())
				return false;
		
		return true;
	}

}
