package net.raebiger.bbtb.api.domain;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import net.raebiger.bbtb.model.Board;
import net.raebiger.bbtb.model.User;

@XmlRootElement
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
public class UserDomain extends AbstractDomain<User> {

	private String email;

	private String displayName;

	private List<BoardDomainLink> createdBoards;

	public UserDomain() {
	}

	public UserDomain(User model) {
		super(model);
		email = model.getEmail();
		displayName = model.getName();
		createdBoards = new ArrayList<BoardDomainLink>();
		for (Board b : model.getCreatedBoards()) {
			createdBoards.add(new BoardDomainLink(b));
		}
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public List<BoardDomainLink> getCreatedBoards() {
		return createdBoards;
	}

	public void setCreatedBoards(List<BoardDomainLink> boards) {
		createdBoards = boards;
	}

	public void setHref(String href) {
		// do nothing. this is a read-only property
	}

	public String getHref() {
		// TODO can we get the 'race' part dynamically?
		return getBaseApiUrl() + "users/" + getUUID();
	}
}
