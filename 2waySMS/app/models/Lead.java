package models;

import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class Lead extends Model {
	public String munchkinId;  // keep customers' data separate
	public Integer leadId;
	//public String email;
	public String phoneNumber;
	public String country;
	public boolean unsubscribed;
}
