package models;

import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class User extends Model {
	//public String email;
	public String password;
	public String munchkinId;
	public String suid;
	public String skey;
	public String secret1;
	public String secret2;
}
