package com.joyveb.tlol.listener;

import java.util.Iterator;
import java.util.LinkedList;

public class RoleMinListenerMan {

	private LinkedList<RoleMinListener> listeners = new LinkedList<RoleMinListener>();

	public final void offer(final RoleMinListener listener) {
		listeners.add(listener);
	}

	public final void execute(final int curMin) {
		Iterator<RoleMinListener> iterator = listeners.iterator();
		while (iterator.hasNext()) {
			RoleMinListener listener = iterator.next();
			try {
				listener.minTick(curMin);
				if (listener.isTimeOut())
					iterator.remove();
			} catch (Exception e) {
				e.printStackTrace();
				iterator.remove();
			}
		}
	}

}
