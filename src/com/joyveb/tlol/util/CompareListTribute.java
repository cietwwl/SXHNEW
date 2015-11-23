package com.joyveb.tlol.util;

import java.util.Comparator;

import com.joyveb.tlol.gang.GangFight;
import com.joyveb.tlol.gang.TributeTop;

public class CompareListTribute implements Comparator {

	public int compare(Object o1, Object o2) {

		// TODO Auto-generated method stub

		TributeTop c1 = (TributeTop) o1;

		TributeTop c2 = (TributeTop) o2;

		if (c1.getTribute() > c2.getTribute()) {

			return -1;

		} else {

			if (c1.getTribute() == c2.getTribute()) {

				return 0;

			} else {

				return 1;

			}

		}

	}
}
