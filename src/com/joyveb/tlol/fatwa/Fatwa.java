package com.joyveb.tlol.fatwa;

/**
 * @function 追杀令
 * @author LuoSR
 * @date 2011-12-21
 */
public class Fatwa {
	/**
	 * 发布者Id
	 */
	public int promulgatorId ;
	
	/**
	 * 发布者名字
	 */
	public String promulgatorName ;
	
	/**
	 * 被追杀者Id
	 */
	public int roleIdByFatwa;
	
	/**
	 * 到期时间
	 */
	public long timeOut;
	
	/**
	 * @param promulgatorId 发布者Id
	 * @param roleIdByFatwa 被追杀者Id
	 * @param timeOut 到期时间
	 * @return 
	 */
	public Fatwa(int promulgatorId, String promulgatorName, int roleIdByFatwa, long timeOut) {		
		this.promulgatorId = promulgatorId;
		this.promulgatorName = promulgatorName;
		this.roleIdByFatwa = roleIdByFatwa;
		this.timeOut = timeOut;
	}
	
	/**
	 * 构造函数
	 */
	public Fatwa (){
		
	}

	public int getPromulgatorId() {
		return promulgatorId;
	}

	public int getRoleIdByFatwa() {
		return roleIdByFatwa;
	}

	public long getTimeOut() {
		return timeOut;
	}

	public void setPromulgatorId(int promulgatorId) {
		this.promulgatorId = promulgatorId;
	}

	public void setRoleIdByFatwa(int roleIdByFatwa) {
		this.roleIdByFatwa = roleIdByFatwa;
	}

	public void setTimeOut(long timeOut) {
		this.timeOut = timeOut;
	}

	public String getPromulgatorName() {
		return promulgatorName;
	}

	public void setPromulgatorName(String promulgatorName) {
		this.promulgatorName = promulgatorName;
	}		
	
}
