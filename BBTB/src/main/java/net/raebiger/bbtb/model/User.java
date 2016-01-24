package net.raebiger.bbtb.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

@Entity(name = "BBTBUser")
public class User extends AbstractEntityWithUUID {
	@Column(name = "email")
	private String email;

	@Column(name = "oauthtoken")
	private String oauthtoken;

	@OneToMany(mappedBy = "creator", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Board> createdBoards = new ArrayList<Board>();

	public User() {
		super();
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getDisplayName() {
		return name;
	}

	public void setDisplayName(String displayName) {
		this.name = displayName;
	}

	public String getOauthtoken() {
		return oauthtoken;
	}

	public void setOauthtoken(String oauthtoken) {
		this.oauthtoken = oauthtoken;
	}

	public List<Board> getCreatedBoards() {
		return createdBoards;
	}

	@Override
	public String toString() {
		return getDisplayName();
	}
}
