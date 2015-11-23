package com.joyveb.tlol.item;

import com.joyveb.tlol.LuaService;

/**
 * 唯一物品
 * @author dell
 *
 */
public abstract class UniqueItem extends Item {
	
	/** 物品唯一id */
	protected long uid;
	
	/** 到期时间 */
	protected int expire = Integer.MAX_VALUE;
	
	@Override
	public long getUid() {
		return uid;
	}
	
	@Override
	public short getStorage() {
		return 1;
	}

	/**
	 * 此函数无效，应当保证不调用
	 * @param num 此物品对象中包含的物品数量
	 */
	@Override
	public void setStorage(final short num) { }
	
	/**
	 * @param uid 物品唯一id
	 */
	public final void setUid(final long uid) {
		this.uid = uid;
	}

	@Override
	public State getState(final int curMin) {
		if(curMin > expire || !LuaService.getBool(LUA_CONTAINER, tid))
			return State.Invalid;
		
		return State.Valid;
	}
	
	/**
	 * @return 是否需要监听
	 */
	public boolean monitored() {
		return expire < Integer.MAX_VALUE;
	}

	/** @return 到期时间 */
	public int getExpire() {
		return expire;
	}

	/** @param expire 到期时间 */
	public void setExpire(final int expire) {
		this.expire = expire;
	}
	
	/**
	 * 将物品设置为永久
	 */
	public void perpetuate() {
		this.expire = Integer.MAX_VALUE;
	}
	
}
