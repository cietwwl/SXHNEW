package com.joyveb.tlol.task;

import java.util.ArrayList;

public class SubtaskState {

	private boolean finished;

	private ArrayList<Integer> states = new ArrayList<Integer>();

	public final void complete() {
		this.finished = true;
		states.clear();
	}
	@Override
	public final String toString() {
		return "finished = " + finished + " " + states;
	}

	public final void init() {
		finished = false;
		states.clear();
	}

	public final void setFinished(final boolean finished) {
		this.finished = finished;
	}

	public final boolean isFinished() {
		return finished;
	}

	public final void setStates(final ArrayList<Integer> states) {
		this.states = states;
	}

	public final ArrayList<Integer> getStates() {
		return states;
	}

}
