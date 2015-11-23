package com.joyveb.tlol.util;

public final class StringToInt {

	public static int stringToInt(String num) {
		if (num != null) {
			try {
				return Integer.valueOf(num);
			} catch (NumberFormatException e) {
				System.out.println("stringToInt字符串数据异常！！！！！！请检查");
				return 0;
			}
		}
		return 0;
	}
}
