package common;

public class Country {

	String name;
	String iso2;
	String iso3;
	int callPrefix;
	
	public Country(String name, String iso2, String iso3, int cp) {
		this.name = name;
		this.iso2 = iso2;
		this.iso3 = iso3;
		this.callPrefix = cp;
	}
}
