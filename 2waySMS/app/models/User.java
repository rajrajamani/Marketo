package models;

import javax.persistence.Column;
import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class User extends Model {
	//public String email;
	@Column(length = 2000)
	public String password;
	public String munchkinId;
	@Column(length = 2000)
	public String suid;
	@Column(length = 2000)
	public String skey;
	@Column(length = 2000)
	public String secret1;
	@Column(length = 2000)
	public String secret2;
	
	public Boolean eulaAccepted;
	public Long eulaAcceptedTS;
}
