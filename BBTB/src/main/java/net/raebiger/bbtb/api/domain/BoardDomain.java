package net.raebiger.bbtb.api.domain;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import net.raebiger.bbtb.model.Board;
import net.raebiger.bbtb.model.BoardPlacement;
import net.raebiger.bbtb.model.Race;

@XmlRootElement
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
public class BoardDomain extends AbstractDomain<Board> {

	private String name;

	private AbstractDomainLink<Race> race1;

	private AbstractDomainLink<Race> race2;

	private String colorRace1;

	private String colorRace2;

	private List<BoardPlacementDomain> placements = new ArrayList<BoardPlacementDomain>();

	public BoardDomain() {
		// needed by JAXB
		super();
	}

	public BoardDomain(Board model) {
		super(model);
		setName(model.getName());
		setColorRace1(model.getColorRace1());
		setColorRace2(model.getColorRace2());
		if (model.getRace1() != null) {
			race1 = new RaceDomainLink(model.getRace1());
		}
		if (model.getRace2() != null) {
			race2 = new RaceDomainLink(model.getRace2());
		}

		for (BoardPlacement placement : model.getPlacements()) {
			BoardPlacementDomain placementDomain = new BoardPlacementDomain(placement);
			placementDomain.setX(placement.getX());
			placementDomain.setY(placement.getY());
			PositionDomain positionDomain = new PositionDomain(placement.getPosition());
			placementDomain.setPosition(positionDomain);
			placements.add(placementDomain);
		}

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public AbstractDomainLink<Race> getRace1() {
		return race1;
	}

	public void setRace1(AbstractDomainLink<Race> race1) {
		this.race1 = race1;
	}

	public AbstractDomainLink<Race> getRace2() {
		return race2;
	}

	public void setRace2(AbstractDomainLink<Race> race2) {
		this.race2 = race2;
	}

	public String getColorRace1() {
		return colorRace1;
	}

	public void setColorRace1(String colorRace1) {
		this.colorRace1 = colorRace1;
	}

	public String getColorRace2() {
		return colorRace2;
	}

	public void setColorRace2(String colorRace2) {
		this.colorRace2 = colorRace2;
	}

	public List<BoardPlacementDomain> getPlacements() {
		return placements;
	}

	public void setPlacements(List<BoardPlacementDomain> placements) {
		this.placements = placements;
	}

	public void setHref(String href) {
		// do nothing. this is a read-only property
	}

	public String getHref() {
		// TODO can we get the 'race' part dynamically?
		return getBaseApiUrl() + "races/" + getUUID();
	}
}
