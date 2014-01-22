package com.marketo.rest.oauth.client;

public class TokenScope {

	public boolean isValid;
	public String userId;
	
	public class Permission {
		public String name;
		public String accessType;
		public Permission[] permissions;
	}
	public class UserRole {
		public String roleName;
		public String roleDescription;
		public String workspaces;
		public boolean allZones;
		public boolean onlyAllZones;
		public boolean hidden;
		public Permission[] permissions;
	};
	public class Info {
		public UserRole[] userRoles;
		public String munchkinId;
		public String language;
		public String locale;
		public String timeZone;
		public boolean apiOnlyUser;
	};
	public Info info; 
	public Integer timeToLive;
}
