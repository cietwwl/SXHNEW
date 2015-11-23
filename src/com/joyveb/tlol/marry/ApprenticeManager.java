package com.joyveb.tlol.marry;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.joyveb.tlol.role.RoleBean;

public final class ApprenticeManager {

	public static ApprenticeManager apprenticeManager = new ApprenticeManager();
	public List<Integer> appList = new ArrayList<Integer>();

	public static ApprenticeManager getInstance() {
		return apprenticeManager;
	}

	public List<Integer> getAppList() {
		return appList;
	}

	private ApprenticeManager() {
	}

	public void checkAppList(RoleBean roleBean) {
		if (appList.contains(roleBean.getRoleid())) {
			roleBean.removeRoleMaster();
			appList.remove((Integer)roleBean.getRoleid());
		}
	}

	public void addAppList(int roleId) {
		if (!appList.contains(roleId)) {
			appList.add(roleId);
		}
	}
	
	public void removeApprentice(){
		for(Integer roleId : appList){
			RoleRemoveApprenticeService.INSTANCE.removeApp(roleId);	
		}
		
	}
}

