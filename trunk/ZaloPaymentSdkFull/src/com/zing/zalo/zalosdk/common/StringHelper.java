package com.zing.zalo.zalosdk.common;

public class StringHelper {
	public static String formatPrice(long val) {
		String balance = String.valueOf(val);
		String rs = balance;
		if (rs.length() > 3) {
			int i = balance.length() - 3;
			rs = balance.substring(i);
			i -= 3;
			while (i >= 0) {
				rs = balance.substring(i, i + 3) + "." + rs;
				i -= 3;
			}
			i = 3 + i;
			if (i > 0) {
				rs = balance.substring(0, i) + "." + rs;
			}
		}
		return rs;
	}
}
