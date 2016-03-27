package net.raebiger.bbtb.api.domain;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import net.raebiger.bbtb.model.BoardPlacement;
import net.raebiger.bbtb.model.BoardPlacement.Team;

@XmlRootElement
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
public class BoardPlacementDomain extends AbstractDomain<BoardPlacement> {
	private PositionDomain position;

	private int x;

	private int y;

	private Team team;

	public BoardPlacementDomain() {
		super();
	}

	public BoardPlacementDomain(BoardPlacement placement) {
		super(placement);
	}

	public Team getTeam() {
		return team;
	}

	public void setTeam(Team team) {
		this.team = team;
	}

	public PositionDomain getPosition() {
		return position;
	}

	public void setPosition(PositionDomain position) {
		this.position = position;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

}
