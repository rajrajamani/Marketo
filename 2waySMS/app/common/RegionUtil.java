package common;

import java.util.HashMap;

import org.apache.commons.lang.WordUtils;

public class RegionUtil {

	private static HashMap<String, String> statesList = null;
	private static HashMap<String, Country> countryList = null;

	public static String getCountryCode(String name) {
		String countryCode = "US";
		if (countryList == null) {
			instantiateCountryList();
		}
		Country cty = countryList.get(name);
		if (cty != null) {
			countryCode = cty.iso2;
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

	public static String getStateShortCode(String state) {
		String canoncialState = state.toLowerCase();
		canoncialState = WordUtils.capitalize(canoncialState);
		if (statesList == null) {
			instantiateStatesList();
		}
		String shortCode = statesList.get(canoncialState);
		if (shortCode == null) {
			return "";
		} else {
			return shortCode;
		}
	}

	private static void instantiateCountryList() {
		countryList = new HashMap<String, Country>();

		countryList.put("Afghanistan", new Country("Afghanistan", "AF", "AFG",
				93));
		countryList.put("Albania", new Country("Albania", "AL", "ALB", 355));
		countryList.put("Algeria", new Country("Algeria", "DZ", "DZA", 213));
		countryList.put("American Samoa", new Country("American Samoa", "AS",
				"ASM", 1684));
		countryList.put("Andorra", new Country("Andorra", "AD", "AND", 376));
		countryList.put("Angola", new Country("Angola", "AO", "AGO", 244));
		countryList.put("Anguilla", new Country("Anguilla", "AI", "AIA", 1264));
		countryList.put("Antarctica", new Country("Antarctica", "AQ", "ATA",
				672));
		countryList.put("Antigua and Barbuda", new Country(
				"Antigua and Barbuda", "AG", "ATG", 1268));
		countryList.put("Argentina", new Country("Argentina", "AR", "ARG", 54));
		countryList.put("Armenia", new Country("Armenia", "AM", "ARM", 374));
		countryList.put("Aruba", new Country("Aruba", "AW", "ABW", 297));
		countryList.put("Australia", new Country("Australia", "AU", "AUS", 61));
		countryList.put("Austria", new Country("Austria", "AT", "AUT", 43));
		countryList.put("Azerbaijan", new Country("Azerbaijan", "AZ", "AZE",
				994));
		countryList.put("Bahamas", new Country("Bahamas", "BS", "BHS", 1242));
		countryList.put("Bahrain", new Country("Bahrain", "BH", "BHR", 973));
		countryList.put("Bangladesh", new Country("Bangladesh", "BD", "BGD",
				880));
		countryList.put("Barbados", new Country("Barbados", "BB", "BRB", 1246));
		countryList.put("Belarus", new Country("Belarus", "BY", "BLR", 375));
		countryList.put("Belgium", new Country("Belgium", "BE", "BEL", 32));
		countryList.put("Belize", new Country("Belize", "BZ", "BLZ", 501));
		countryList.put("Benin", new Country("Benin", "BJ", "BEN", 229));
		countryList.put("Bermuda", new Country("Bermuda", "BM", "BMU", 1441));
		countryList.put("Bhutan", new Country("Bhutan", "BT", "BTN", 975));
		countryList.put("Bolivia", new Country("Bolivia", "BO", "BOL", 591));
		countryList.put("Bosnia and Herzegovina", new Country(
				"Bosnia and Herzegovina", "BA", "BIH", 387));
		countryList.put("Botswana", new Country("Botswana", "BW", "BWA", 267));
		countryList.put("Brazil", new Country("Brazil", "BR", "BRA", 55));
		countryList.put("British Virgin Islands", new Country(
				"British Virgin Islands", "VG", "VGB", 1284));
		countryList.put("Brunei", new Country("Brunei", "BN", "BRN", 673));
		countryList.put("Bulgaria", new Country("Bulgaria", "BG", "BGR", 359));
		countryList.put("Burkina Faso", new Country("Burkina Faso", "BF",
				"BFA", 226));
		countryList.put("Burma (Myanmar)", new Country("Burma (Myanmar)", "MM",
				"MMR", 95));
		countryList.put("Burundi", new Country("Burundi", "BI", "BDI", 257));
		countryList.put("Cambodia", new Country("Cambodia", "KH", "KHM", 855));
		countryList.put("Cameroon", new Country("Cameroon", "CM", "CMR", 237));
		countryList.put("Canada", new Country("Canada", "CA", "CAN", 1));
		countryList.put("Cape Verde", new Country("Cape Verde", "CV", "CPV",
				238));
		countryList.put("Cayman Islands", new Country("Cayman Islands", "KY",
				"CYM", 1345));
		countryList.put("Central African Republic", new Country(
				"Central African Republic", "CF", "CAF", 236));
		countryList.put("Chad", new Country("Chad", "TD", "TCD", 235));
		countryList.put("Chile", new Country("Chile", "CL", "CHL", 56));
		countryList.put("China", new Country("China", "CN", "CHN", 86));
		countryList.put("Christmas Island", new Country("Christmas Island",
				"CX", "CXR", 61));
		countryList.put("Cocos(Keeling) Islands", new Country(
				"Cocos(Keeling) Islands", "CC", "CCK", 61));
		countryList.put("Colombia", new Country("Colombia", "CO", "COL", 57));
		countryList.put("Comoros", new Country("Comoros", "KM", "COM", 269));
		countryList.put("Cook Islands", new Country("Cook Islands", "CK",
				"COK", 682));
		countryList.put("Costa Rica", new Country("Costa Rica", "CR", "CRC",
				506));
		countryList.put("Croatia", new Country("Croatia", "HR", "HRV", 385));
		countryList.put("Cuba", new Country("Cuba", "CU", "CUB", 53));
		countryList.put("Cyprus", new Country("Cyprus", "CY", "CYP", 357));
		countryList.put("Czech Republic", new Country("Czech Republic", "CZ",
				"CZE", 420));
		countryList.put("Democratic Republic of the Congo", new Country(
				"Democratic Republic of the Congo", "CD", "COD", 243));
		countryList.put("Denmark", new Country("Denmark", "DK", "DNK", 45));
		countryList.put("Djibouti", new Country("Djibouti", "DJ", "DJI", 253));
		countryList.put("Dominica", new Country("Dominica", "DM", "DMA", 1767));
		countryList.put("Dominican Republic", new Country("Dominican Republic",
				"DO", "DOM", 1809));
		countryList.put("Ecuador", new Country("Ecuador", "EC", "ECU", 593));
		countryList.put("Egypt", new Country("Egypt", "EG", "EGY", 20));
		countryList.put("El Salvador", new Country("El Salvador", "SV", "SLV",
				503));
		countryList.put("Equatorial Guinea", new Country("Equatorial Guinea",
				"GQ", "GNQ", 240));
		countryList.put("Eritrea", new Country("Eritrea", "ER", "ERI", 291));
		countryList.put("Estonia", new Country("Estonia", "EE", "EST", 372));
		countryList.put("Ethiopia", new Country("Ethiopia", "ET", "ETH", 251));
		countryList.put("Falkland Islands", new Country("Falkland Islands",
				"FK", "FLK", 500));
		countryList.put("Faroe Islands", new Country("Faroe Islands", "FO",
				"FRO", 298));
		countryList.put("Fiji", new Country("Fiji", "FJ", "FJI", 679));
		countryList.put("Finland", new Country("Finland", "FI", "FIN", 358));
		countryList.put("France", new Country("France", "FR", "FRA", 33));
		countryList.put("French Polynesia", new Country("French Polynesia",
				"PF", "PYF", 689));
		countryList.put("Gabon", new Country("Gabon", "GA", "GAB", 241));
		countryList.put("Gambia", new Country("Gambia", "GM", "GMB", 220));
		countryList.put("Gaza Strip", new Country("Gaza Strip", "GZ", "GZA",
				970));
		countryList.put("Georgia", new Country("Georgia", "GE", "GEO", 995));
		countryList.put("Germany", new Country("Germany", "DE", "DEU", 49));
		countryList.put("Ghana", new Country("Ghana", "GH", "GHA", 233));
		countryList
				.put("Gibraltar", new Country("Gibraltar", "GI", "GIB", 350));
		countryList.put("Greece", new Country("Greece", "GR", "GRC", 30));
		countryList
				.put("Greenland", new Country("Greenland", "GL", "GRL", 299));
		countryList.put("Grenada", new Country("Grenada", "GD", "GRD", 1473));
		countryList.put("Guam", new Country("Guam", "GU", "GUM", 1671));
		countryList
				.put("Guatemala", new Country("Guatemala", "GT", "GTM", 502));
		countryList.put("Guinea", new Country("Guinea", "GN", "GIN", 224));
		countryList.put("Guinea Bissau", new Country("Guinea Bissau", "GW",
				"GNB", 245));
		countryList.put("Guyana", new Country("Guyana", "GY", "GUY", 592));
		countryList.put("Haiti", new Country("Haiti", "HT", "HTI", 509));
		countryList.put("Holy See(Vatican City)", new Country(
				"Holy See(Vatican City)", "VA", "VAT", 39));
		countryList.put("Honduras", new Country("Honduras", "HN", "HND", 504));
		countryList
				.put("Hong Kong", new Country("Hong Kong", "HK", "HKG", 852));
		countryList.put("Hungary", new Country("Hungary", "HU", "HUN", 36));
		countryList.put("Iceland", new Country("Iceland", "IS", "IS", 354));
		countryList.put("India", new Country("India", "IN", "IND", 91));
		countryList.put("Indonesia", new Country("Indonesia", "ID", "IDN", 62));
		countryList.put("Iran", new Country("Iran", "IR", "IRN", 98));
		countryList.put("Iraq", new Country("Iraq", "IQ", "IRQ", 964));
		countryList.put("Ireland", new Country("Ireland", "IE", "IRL", 353));
		countryList.put("Isle of Man", new Country("Isle of Man", "IM", "IMN",
				44));
		countryList.put("Israel", new Country("Israel", "IL", "ISR", 972));
		countryList.put("Italy", new Country("Italy", "IT", "ITA", 39));
		countryList.put("Ivory Coast", new Country("Ivory Coast", "CI", "CIV",
				225));
		countryList.put("Jamaica", new Country("Jamaica", "JM", "JAM", 1876));
		countryList.put("Japan", new Country("Japan", "JP", "JPN", 81));
		countryList.put("Jordan", new Country("Jordan", "JO", "JOR", 962));
		countryList
				.put("Kazakhstan", new Country("Kazakhstan", "KZ", "KAZ", 7));
		countryList.put("Kenya", new Country("Kenya", "KE", "KEN", 254));
		countryList.put("Kiribati", new Country("Kiribati", "KI", "KIR", 686));
		countryList.put("Kosovo", new Country("Kosovo", "KS", "KSO", 381));
		countryList.put("Kuwait", new Country("Kuwait", "KW", "KWT", 965));
		countryList.put("Kyrgyzstan", new Country("Kyrgyzstan", "KG", "KGZ",
				996));
		countryList.put("Laos", new Country("Laos", "LA", "LAO", 856));
		countryList.put("Latvia", new Country("Latvia", "LV", "LVA", 371));
		countryList.put("Lebanon", new Country("Lebanon", "LB", "LBN", 961));
		countryList.put("Lesotho", new Country("Lesotho", "LS", "LSO", 266));
		countryList.put("Liberia", new Country("Liberia", "LR", "LBR", 231));
		countryList.put("Libya", new Country("Libya", "LY", "LBY", 218));
		countryList.put("Liechtenstein", new Country("Liechtenstein", "LI",
				"LIE", 423));
		countryList
				.put("Lithuania", new Country("Lithuania", "LT", "LTU", 370));
		countryList.put("Luxembourg", new Country("Luxembourg", "LU", "LUX",
				352));
		countryList.put("Macau", new Country("Macau", "MO", "MAC", 853));
		countryList
				.put("Macedonia", new Country("Macedonia", "MK", "MKD", 389));
		countryList.put("Madagascar", new Country("Madagascar", "MG", "MDG",
				261));
		countryList.put("Malawi", new Country("Malawi", "MW", "MWI", 265));
		countryList.put("Malaysia", new Country("Malaysia", "MY", "MYS", 60));
		countryList.put("Maldives", new Country("Maldives", "MV", "MDV", 960));
		countryList.put("Mali", new Country("Mali", "ML", "MLI", 223));
		countryList.put("Malta", new Country("Malta", "MT", "MLT", 356));
		countryList.put("Marshall Islands", new Country("Marshall Islands",
				"MH", "MHL", 692));
		countryList.put("Mauritania", new Country("Mauritania", "MR", "MRT",
				222));
		countryList
				.put("Mauritius", new Country("Mauritius", "MU", "MUS", 230));
		countryList.put("Mayotte", new Country("Mayotte", "YT", "MYT", 262));
		countryList.put("Mexico", new Country("Mexico", "MX", "MEX", 52));
		countryList.put("Micronesia", new Country("Micronesia", "FM", "FSM",
				691));
		countryList.put("Moldova", new Country("Moldova", "MD", "MDA", 373));
		countryList.put("Monaco", new Country("Monaco", "MC", "MCO", 377));
		countryList.put("Mongolia", new Country("Mongolia", "MN", "MNG", 976));
		countryList.put("Montenegro", new Country("Montenegro", "ME", "MNE",
				382));
		countryList.put("Montserrat", new Country("Montserrat", "MS", "MSR",
				1664));
		countryList.put("Morocco", new Country("Morocco", "MA", "MAR", 212));
		countryList.put("Mozambique", new Country("Mozambique", "MZ", "MOZ",
				258));
		countryList.put("Namibia", new Country("Namibia", "NA", "NAM", 264));
		countryList.put("Nauru", new Country("Nauru", "NR", "NRU", 674));
		countryList.put("Nepal", new Country("Nepal", "NP", "NPL", 977));
		countryList.put("Netherlands", new Country("Netherlands", "NL", "NLD",
				31));
		countryList.put("Netherlands Antilles", new Country(
				"Netherlands Antilles", "AN", "ANT", 599));
		countryList.put("New Caledonia", new Country("New Caledonia", "NC",
				"NCL", 687));
		countryList.put("New Zealand", new Country("New Zealand", "NZ", "NZL",
				64));
		countryList
				.put("Nicaragua", new Country("Nicaragua", "NI", "NIC", 505));
		countryList.put("Niger", new Country("Niger", "NE", "NER", 227));
		countryList.put("Nigeria", new Country("Nigeria", "NG", "NGA", 234));
		countryList.put("Niue", new Country("Niue", "NU", "NIU", 683));
		countryList.put("Norfolk Island", new Country("Norfolk Island", "NF",
				"NFK", 672));
		countryList.put("North Korea", new Country("North Korea", "KP", "PRK",
				850));
		countryList.put("Northern Mariana Islands", new Country(
				"Northern Mariana Islands", "MP", "MNP", 1670));
		countryList.put("Norway", new Country("Norway", "NO", "NOR", 47));
		countryList.put("Oman", new Country("Oman", "OM", "OMN", 968));
		countryList.put("Pakistan", new Country("Pakistan", "PK", "PAK", 92));
		countryList.put("Palau", new Country("Palau", "PW", "PLW", 680));
		countryList.put("Panama", new Country("Panama", "PA", "PAN", 507));
		countryList.put("Papua New Guinea", new Country("Papua New Guinea",
				"PG", "PNG", 675));
		countryList.put("Paraguay", new Country("Paraguay", "PY", "PRY", 595));
		countryList.put("Peru", new Country("Peru", "PE", "PER", 51));
		countryList.put("Philippines", new Country("Philippines", "PH", "PHL",
				63));
		countryList.put("Pitcairn Islands", new Country("Pitcairn Islands",
				"PN", "PCN", 870));
		countryList.put("Poland", new Country("Poland", "PL", "POL", 48));
		countryList.put("Portugal", new Country("Portugal", "PT", "PRT", 351));
		countryList.put("Puerto Rico", new Country("Puerto Rico", "PR", "PRI",
				1));
		countryList.put("Qatar", new Country("Qatar", "QA", "QAT", 974));
		countryList.put("Republic of the Congo", new Country(
				"Republic of the Congo", "CG", "COG", 242));
		countryList.put("Romania", new Country("Romania", "RO", "ROU", 40));
		countryList.put("Russia", new Country("Russia", "RU", "RUS", 7));
		countryList.put("Rwanda", new Country("Rwanda", "RW", "RWA", 250));
		countryList.put("Saint Barthelemy", new Country("Saint Barthelemy",
				"BL", "BLM", 590));
		countryList.put("Saint Helena", new Country("Saint Helena", "SH",
				"SHN", 290));
		countryList.put("Saint Kitts and Nevis", new Country(
				"Saint Kitts and Nevis", "KN", "KNA", 1869));
		countryList.put("Saint Lucia", new Country("Saint Lucia", "LC", "LCA",
				1758));
		countryList.put("Saint Martin", new Country("Saint Martin", "MF",
				"MAF", 1599));
		countryList.put("Saint Pierre and Miquelon", new Country(
				"Saint Pierre and Miquelon", "PM", "SPM", 508));
		countryList.put("Saint Vincent and the Grenadines", new Country(
				"Saint Vincent and the Grenadines", "VC", "VCT", 1784));
		countryList.put("Samoa", new Country("Samoa", "WS", "WSM", 685));
		countryList.put("San Marino", new Country("San Marino", "SM", "SMR",
				378));
		countryList.put("Sao Tome and Principe", new Country(
				"Sao Tome and Principe", "ST", "STP", 239));
		countryList.put("Saudi Arabia", new Country("Saudi Arabia", "SA",
				"SAU", 966));
		countryList.put("Senegal", new Country("Senegal", "SN", "SEN", 221));
		countryList.put("Serbia", new Country("Serbia", "RS", "SRB", 381));
		countryList.put("Seychelles", new Country("Seychelles", "SC", "SYC",
				248));
		countryList.put("Sierra Leone", new Country("Sierra Leone", "SL",
				"SLE", 232));
		countryList.put("Singapore", new Country("Singapore", "SG", "SGP", 65));
		countryList.put("Slovakia", new Country("Slovakia", "SK", "SVK", 421));
		countryList.put("Slovenia", new Country("Slovenia", "SI", "SVN", 386));
		countryList.put("Solomon Islands", new Country("Solomon Islands", "SB",
				"SLB", 677));
		countryList.put("Somalia", new Country("Somalia", "SO", "SOM", 252));
		countryList.put("South Africa", new Country("South Africa", "ZA",
				"ZAF", 27));
		countryList.put("South Korea", new Country("South Korea", "KR", "KOR",
				82));
		countryList.put("Spain", new Country("Spain", "ES", "ESP", 34));
		countryList.put("Sri Lanka", new Country("Sri Lanka", "LK", "LKA", 94));
		countryList.put("Sudan", new Country("Sudan", "SD", "SDN", 249));
		countryList.put("Suriname", new Country("Suriname", "SR", "SUR", 597));
		countryList
				.put("Swaziland", new Country("Swaziland", "SZ", "SWZ", 268));
		countryList.put("Sweden", new Country("Sweden", "SE", "SWE", 46));
		countryList.put("Switzerland", new Country("Switzerland", "CH", "CHE",
				41));
		countryList.put("Syria", new Country("Syria", "SY", "SYR", 963));
		countryList.put("Taiwan", new Country("Taiwan", "TW", "TWN", 886));
		countryList.put("Tajikistan", new Country("Tajikistan", "TJ", "TJK",
				992));
		countryList.put("Tanzania", new Country("Tanzania", "TZ", "TZA", 255));
		countryList.put("Thailand", new Country("Thailand", "TH", "THA", 66));
		countryList.put("Timor Leste", new Country("Timor Leste", "TL", "TLS",
				670));
		countryList.put("Togo", new Country("Togo", "TG", "TGO", 228));
		countryList.put("Tokelau", new Country("Tokelau", "TK", "TKL", 690));
		countryList.put("Tonga", new Country("Tonga", "TO", "TON", 676));
		countryList.put("Trinidad and Tobago", new Country(
				"Trinidad and Tobago", "TT", "TTO", 1868));
		countryList.put("Tunisia", new Country("Tunisia", "TN", "TUN", 216));
		countryList.put("Turkey", new Country("Turkey", "TR", "TUR", 90));
		countryList.put("Turkmenistan", new Country("Turkmenistan", "TM",
				"TKM", 993));
		countryList.put("Turks and Caicos Islands", new Country(
				"Turks and Caicos Islands", "TC", "TCA", 1649));
		countryList.put("Tuvalu", new Country("Tuvalu", "TV", "TUV", 688));
		countryList.put("Uganda", new Country("Uganda", "UG", "UGA", 256));
		countryList.put("Ukraine", new Country("Ukraine", "UA", "UKR", 380));
		countryList.put("United Arab Emirates", new Country(
				"United Arab Emirates", "AE", "ARE", 971));
		countryList.put("United Kingdom", new Country("United Kingdom", "GB",
				"GBR", 44));
		countryList.put("United States", new Country("United States", "US",
				"USA", 1));
		countryList.put("Uruguay", new Country("Uruguay", "UY", "URY", 598));
		countryList.put("US Virgin Islands", new Country("US Virgin Islands",
				"VI", "VIR", 1340));
		countryList.put("Uzbekistan", new Country("Uzbekistan", "UZ", "UZB",
				998));
		countryList.put("Vanuatu", new Country("Vanuatu", "VU", "VUT", 678));
		countryList.put("Venezuela", new Country("Venezuela", "VE", "VEN", 58));
		countryList.put("Vietnam", new Country("Vietnam", "VN", "VNM", 84));
		countryList.put("Wallis and Futuna", new Country("Wallis and Futuna",
				"WF", "WLF", 681));
		countryList
				.put("West Bank", new Country("West Bank", "WB", "WBA", 970));
		countryList.put("Yemen", new Country("Yemen", "YE", "YEM", 967));
		countryList.put("Zambia", new Country("Zambia", "ZM", "ZMB", 260));
		countryList.put("Zimbabwe", new Country("Zimbabwe", "ZW", "ZWE", 263));
	}

	private static void instantiateStatesList() {
		statesList = new HashMap<String, String>();

		statesList.put("Alabama", "AL");
		statesList.put("Alaska", "AK");
		statesList.put("Arizona", "AZ");
		statesList.put("Arkansas", "AR");
		statesList.put("California", "CA");

		statesList.put("Colorado", "CO");
		statesList.put("Connecticut", "CT");
		statesList.put("Delaware", "DE");
		statesList.put("Florida", "FL");
		statesList.put("Georgia", "GA");

		statesList.put("Hawaii", "HI");
		statesList.put("Idaho", "ID");
		statesList.put("Illinois", "IL");
		statesList.put("Indiana", "IN");
		statesList.put("Iowa", "IA");

		statesList.put("Kansas", "KS");
		statesList.put("Kentucky", "KY");
		statesList.put("Louisiana", "LA");
		statesList.put("Maine", "ME");
		statesList.put("Maryland", "MD");

		statesList.put("Massachusetts", "MA");
		statesList.put("Michigan", "MI");
		statesList.put("Minnesota", "MN");
		statesList.put("Mississippi", "MS");
		statesList.put("Missouri", "MO");

		statesList.put("Montana", "MT");
		statesList.put("Nebraska", "NE");
		statesList.put("Nevada", "NV");
		statesList.put("New Hampshire", "NH");
		statesList.put("New Jersey", "NJ");

		statesList.put("New Mexico", "NM");
		statesList.put("New York", "NY");
		statesList.put("North Carolina", "NC");
		statesList.put("North Dakota", "ND");
		statesList.put("Ohio", "OH");

		statesList.put("Oklahoma", "OK");
		statesList.put("Oregon", "OR");
		statesList.put("Pennsylvania", "PA");
		statesList.put("Rhode Island", "RI");
		statesList.put("South Carolina", "SC");

		statesList.put("South Dakota", "SD");
		statesList.put("Tennessee", "TN");
		statesList.put("Texas", "TX");
		statesList.put("Utah", "UT");
		statesList.put("Vermont", "VT");

		statesList.put("Virginia", "VA");
		statesList.put("Washington", "WA");
		statesList.put("West Virginia", "WV");
		statesList.put("Wisconsin", "WI");
		statesList.put("Wyoming", "WY");

		statesList.put("United States", "US");
		statesList.put("Alberta", "AB");
		statesList.put("British Columbia", "BC");
		statesList.put("Manitoba", "MB");
		statesList.put("New Brunswick", "NB");

		statesList.put("Newfoundland and Labrador", "NL");
		statesList.put("Northwest Territories", "NT");
		statesList.put("Nova Scotia", "NS");
		statesList.put("Nunavut", "NU");
		statesList.put("Ontario", "ON");

		statesList.put("Prince Edward Island", "PE");
		statesList.put("Quebec", "QC");
		statesList.put("Québec", "QC");
		statesList.put("Saskatchewan", "SK");
		statesList.put("Yukon", "YT");

	}
}
