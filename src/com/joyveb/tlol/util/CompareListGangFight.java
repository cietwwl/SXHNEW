package com.joyveb.tlol.util;

import java.util.Comparator;

import com.joyveb.tlol.gang.GangFight;

public class CompareListGangFight implements Comparator {

	public int compare(Object o1, Object o2) {

		// TODO Auto-generated method stub

		GangFight c1 = (GangFight) o1;

		GangFight c2 = (GangFight) o2;

		if (c1.getAtkSpd() > c2.getAtkSpd()) {

			return -1;

		} else {

			if (c1.getAtkSpd() == c2.getAtkSpd()) {

				return 0;

			} else {

				return 1;

			}

		}

	}
}
