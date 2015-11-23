package com.joyveb.tlol.listener;

import com.joyveb.tlol.role.RoleBean;

public class RoleDataSaver {

	/**
	 * 回写间隔
	 */
	private static int Write_Back_Interval = 18;

	/** 下次回写时间 */
	private long nextSave = System.currentTimeMillis() + Write_Back_Interval * 60 * 1000;

	private RoleBean owner;
	
	public RoleDataSaver(final RoleBean owner) {
		this.owner = owner;
	}

	public final void mainLoopTick() {
		if (System.currentTimeMillis() < nextSave)
			return;

		owner.updateRole();
	}

	public final void resetNextSave() {
		this.nextSave = System.currentTimeMillis() + Write_Back_Interval * 60 * 1000;
	}

	public static int getWrite_Back_Interval() {
		return Write_Back_Interval;
	}

	public static void setWrite_Back_Interval(final int write_Back_Interval) {
		Write_Back_Interval = write_Back_Interval;
	}

}
