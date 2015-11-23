package com.joyveb.tlol.marry;

import java.util.ArrayList;
import java.util.List;

import com.joyveb.tlol.role.RoleBean;

public final class MarryManager {

	public static MarryManager marryManager = new MarryManager();
	public List<Integer> marryList = new ArrayList<Integer>();

	public static MarryManager getInstance() {
		return marryManager;
	}

	public List<Integer> getMarryList() {
		return marryList;
	}

	private MarryManager() {
	}

	public void checkMarryList(RoleBean roleBean) {
		if (marryList.contains(roleBean.getRoleid())) {
			roleBean.removeMarry();
			roleBean.setMarryringtime(null);
			marryList.remove((Integer)roleBean.getRoleid());
		}
	}

	public void addMarryList(int roleId) {
		if (!marryList.contains(roleId)) {
			marryList.add(roleId);
		}
	}
	
	public void breakUp(){
		for(Integer roleId : marryList){
			RoleBreakUPService.INSTANCE.breakUp(roleId);	
		}
		
	}
}
