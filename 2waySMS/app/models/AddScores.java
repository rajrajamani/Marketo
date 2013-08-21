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
public class AddScores extends Model {

	public AddScores() {
	}

	public String munchkinId; // from token
	public String leadId;
	public int score1;
	public int score2;
}