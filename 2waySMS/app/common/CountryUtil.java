package common;

public class CountryUtil {

	public static String getCountryCode(String name) {
		String countryCode = "US";
		if (name.equals("United States")
				|| name.equals("USA")
				|| name.equals("United States of America")) {
			countryCode = "US";
		} else if (name.equals("China")
				|| name.equals("CHN")) {
			countryCode = "CN";
		} else if (name.equals("Hong Kong")
				|| name.equals("HKG")) {
			countryCode = "HK";
		} else {
			countryCode = "US";
		}
		return countryCode;
	}

	public static String inferCountryFromPhoneNumber(String from) {
		String name = "USA";
		String compStr = from;
		if (from.startsWith(" ")) {
			compStr = from.substring(1);
		}
		if (compStr.startsWith("+")) {
			compStr = from.substring(1);
		} 
		if (compStr.startsWith("1")) {
			name = "USA";
		} else if (compStr.startsWith("852")) {
			name = "HKG";
		} else if (compStr.startsWith("86")) {
			name = "China";
		}
		return name;
	}
}
