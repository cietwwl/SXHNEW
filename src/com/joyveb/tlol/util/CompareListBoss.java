package com.joyveb.tlol.util;

import java.util.Comparator;

import com.joyveb.tlol.boss.Fighter;
import com.joyveb.tlol.gang.GangFight;

public class CompareListBoss implements Comparator {

	public int compare(Object o1, Object o2) {

		// TODO Auto-generated method stub

		Fighter c1 = (Fighter) o1;

		Fighter c2 = (Fighter) o2;

		if (c1.getTotalHit() > c2.getTotalHit()) {

			return -1;

		} else {

			if (c1.getTotalHit() == c2.getTotalHit()) {

				return 0;

			} else {

				return 1;

			}

		}

	}
}
