package net.raebiger.bbtb.util;

public class StringUtil {
	public static String getTrimmedValueOrNull(String valueOrNull) {
		if (valueOrNull == null) {
			return null;
		} else {
			return valueOrNull.trim();
		}
	}
}
