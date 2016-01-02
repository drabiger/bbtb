package net.raebiger.bbtb.api.domain;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import net.raebiger.bbtb.model.Position;
import net.raebiger.bbtb.model.Race;

@XmlRootElement
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
public class RaceDomain extends AbstractDomain<Race> {

	private String name;

	private List<PositionDomain> positions = new ArrayList<PositionDomain>();

	public RaceDomain() {
		// needed by JAX-RS
		super();
	}

	public RaceDomain(Race model) {
		super(model);
		name = model.getName();
		for (Position p : model.getPositions()) {
			positions.add(new PositionDomain(p));
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<PositionDomain> getPositions() {
		return positions;
	}

	@XmlTransient
	public void addPosition(PositionDomain position) {
		positions.add(position);
	}

	public void setHref(String href) {
		// do nothing. this is a read-only property
	}

	public String getHref() {
		// TODO can we get the 'race' part dynamically?
		return getBaseApiUrl() + "races/" + getUUID();
	}

}
