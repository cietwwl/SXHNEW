package com.joyveb.tlol.marry;

import com.joyveb.tlol.OnlineService;
import com.joyveb.tlol.role.RoleBean;

public final class MarryMasterAddExpGold {

	public static RoleBean getRoleBean(final int roleid) {
		return OnlineService.getOnline(roleid);
	}

	public static boolean isRoleOnline(final int roleid) {
		return OnlineService.isRoleOnline(roleid);
	}

	public static boolean twoIsMarry(final int roleid1, final int roleid2) {
		if (getRoleBean(roleid1).getMarryId() == roleid2) {

			return true;
		}
		return false;
	}

	public static  boolean twoIsMaster(final int roleid1, final int roleid2) {
		if (getRoleBean(roleid1).getIfApprentice()) {
			if (getRoleBean(roleid1).getIfEqualsApprentice(roleid2)&&getRoleBean(roleid2).getLevel()<50) {
				return true;
			}
		} else if (getRoleBean(roleid2).getIfApprentice()) {
			if (getRoleBean(roleid2).getIfEqualsApprentice(roleid1)&&getRoleBean(roleid1).getLevel()<50) {
				return true;
			}
		}
		return false;
	}

	public static boolean threeIsMarry(final int roleid1, final int roleid2, final int roleid3) {
		if (twoIsMarry(roleid1, roleid2) || twoIsMarry(roleid2, roleid3) || twoIsMarry(roleid1, roleid3)) {
			return true;
		}
		return false;
	}

	public static boolean threeIsMaster(final int roleid1, final int roleid2, final int roleid3) {
		if (twoIsMaster(roleid1, roleid2) || twoIsMaster(roleid2, roleid3) || twoIsMaster(roleid1, roleid3)) {
			return true;
		}
		return false;
	}

	public static boolean threeIsAllMaster(final int roleid1, final int roleid2, final int roleid3) {
		if (threeIsMaster(roleid1, roleid2, roleid3)) {
			if (getRoleBean(roleid1).getIfApprentice()) {
				if (getRoleBean(roleid1).getIfEqualsApprentice(roleid2) && getRoleBean(roleid1).getIfEqualsApprentice(roleid3)&&getRoleBean(roleid2).getLevel()<50&&getRoleBean(roleid3).getLevel()<50) {
					return true;
				}
			}

			if (getRoleBean(roleid2).getIfApprentice()) {
				if (getRoleBean(roleid2).getIfEqualsApprentice(roleid1) && getRoleBean(roleid2).getIfEqualsApprentice(roleid3)&&getRoleBean(roleid1).getLevel()<50&&getRoleBean(roleid3).getLevel()<50) {
					return true;
				}

			}
			if (getRoleBean(roleid3).getIfApprentice()) {
				if (getRoleBean(roleid3).getIfEqualsApprentice(roleid1) && getRoleBean(roleid3).getIfEqualsApprentice(roleid2)&&getRoleBean(roleid1).getLevel()<50&&getRoleBean(roleid2).getLevel()<50) {
					return true;
				}

			}
		}

		return false;
	}

}