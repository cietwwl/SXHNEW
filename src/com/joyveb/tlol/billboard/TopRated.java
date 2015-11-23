package com.joyveb.tlol.billboard;

import java.util.ArrayList;
import java.util.Comparator;

import com.joyveb.tlol.NameCard;
import com.joyveb.tlol.db.DataHandler;
import com.joyveb.tlol.role.RoleBean;
import com.joyveb.tlol.util.Log;

public abstract class TopRated<T extends NameCard<T>> implements DataHandler {
	public static final String CHNUM = "一二三四五六七八九十";

	public static int max = 10;

	protected ArrayList<T> topCards = new ArrayList<T>();

	protected final String topRatedName;

	protected final Comparator<T> comparator;

	protected volatile boolean loaded;
	
	public TopRated(final String topRatedName, final Comparator<T> comparator) {
		this.topRatedName = topRatedName;
		this.comparator = comparator;
	}

	public abstract String getDescribe(int index, T card);

	public abstract void sendBulletin();

	public abstract void loadTopRated();

	protected final int getRawIndex(final T element) {
		for(int i = 0; i < topCards.size(); i++)
			if(topCards.get(i).equals(element))
				return i;

		return topCards.size();
	}

	protected final String getItemName(final int index) {
		if(index >= topCards.size())
			return "无";

		return topCards.get(index).getName();
	}

	public final void watch() {
		Log.info(Log.STDOUT, topRatedName);
		if(topCards.size() > max)
			Log.error(Log.ERROR, this.topRatedName + "人数超出上限");

		for(int i = 0; i < Math.max(topCards.size(), max); i++)
			Log.info(Log.STDOUT, "第" + CHNUM.charAt(i) + "名 " + topCards.get(i));
	}

	public final ArrayList<T> getTopCards() {
		return topCards;
	}

	public final String getTopRatedName() {
		return topRatedName;
	}

	public final boolean isEmpty() {
		return topCards.isEmpty();
	}
	

	public void setLoaded(boolean loaded) {
		this.loaded = loaded;
	}

	public boolean isLoaded() {
		return loaded;
	}

	public void removeEvent(RoleBean source) {
		// TODO Auto-generated method stub
		
	}

}
