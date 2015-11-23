package com.joyveb.tlol.community;

import java.util.HashSet;

import com.joyveb.tlol.role.RoleBean;

public class Community {
	private long id;

	private int itemid;

	private String cname;

	private HashSet<Integer> members = new HashSet<Integer>();

	public Community(final long id, final int itemid, final String cname) {
		this.setId(id).setItemid(itemid).setCname(cname);
	}

	public final long getId() {
		return id;
	}

	public final Community setId(final long id) {
		this.id = id;
		return this;
	}

	public final int getItemid() {
		return itemid;
	}

	public final Community setItemid(final int itemid) {
		this.itemid = itemid;
		return this;
	}

	public final String getCname() {
		return cname;
	}

	public final Community setCname(final String cname) {
		this.cname = cname;
		return this;
	}

	public final void addMember(final RoleBean role) {
		members.add(role.getRoleid());
	}

	public final HashSet<Integer> getMembers() {
		return members;
	}

	public final void delMember(final RoleBean role) {
		members.remove(role.getRoleid());
	}

}
