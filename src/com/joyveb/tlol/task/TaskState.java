package com.joyveb.tlol.task;

import java.util.ArrayList;

public class TaskState {
	/** 任务id */
	int taskid;

	/** 子任务步骤 0表示完成 */
	byte step = 1;

	private SubtaskState substate = new SubtaskState();

	private ArrayList<Integer> extra = new ArrayList<Integer>();
	@Override
	public final String toString() {
		return "taskid = " + taskid + "\nstep = " + step + "\nsubstate = "
				+ substate + "\nextra = " + extra;
	}

	public TaskState(final int taskid) {
		this.taskid = taskid;
	}

	public TaskState(final int taskid, final byte step) {
		this.taskid = taskid;
		this.step = step;
	}

	public final void putStateStr(final StringBuffer buffer) {
		StringBuffer single = new StringBuffer();
		single.append("<" + taskid + " " + step);
		if (substate.isFinished()) {
			single.append(" 0");
		} else {
			single.append(" 1");
			for (int num : substate.getStates())
				single.append(" " + num);
		}

		if (!extra.isEmpty()) {
			single.append(" |");
			for (int num : extra)
				single.append(" " + num);
		}

		single.append(">");
		buffer.append(single.toString());
	}

	public final byte getStep() {
		return step;
	}

	public final void stepUp() {
		step++;
		substate.init();
	}
	
	public final void onlyStepUp(byte step)
	{
		this.step  = step;
		
	}

	public final void stepDown() {
		step--;
		substate.complete();
	}

	public final boolean isFinished() {
		return step == 0;
	}

	public void complete() {
		step = 0;
		substate.complete();
	}
	
	public void completeSubstate() {
		substate.complete();
	}

	public ArrayList<Integer> getExtra() {
		return extra;
	}
	
	public void setExtra(ArrayList<Integer> extra) {
		this.extra = extra;
	}

	public SubtaskState getSubstate() {
		return substate;
	}

	public void setSubstate(SubtaskState substate) {
		this.substate = substate;
	}

}
