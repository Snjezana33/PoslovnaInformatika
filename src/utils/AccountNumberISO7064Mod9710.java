package utils;

public class AccountNumberISO7064Mod9710 {
	
	public static boolean verify(String digits) {
		return (Long.parseLong(digits) % 97 == 1);
	}
	
	public static int computeCheck(String digits) {
		return ((int)(98 - (Long.parseLong(digits) * 100) % 97L)) % 97;
	}
	
	public static String computeCheckAsString(String digits) {
		int check = computeCheck(digits);
		return check > 9 ? String.valueOf(check) : '0' + String.valueOf(check);
	}
	
}
