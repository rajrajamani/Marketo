package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import play.db.jpa.Model;

@Entity
public class PhoneQuery extends Model {

	public PhoneQuery() {
	}

	public String phoneNum; // provided by marketer
	public String format;
	public String munchkinId; // from token
	public String leadId;
	public String formattedNum;
	public String city;
	public String state;
	public String country;
	public String type;
}