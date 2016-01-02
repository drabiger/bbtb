package net.raebiger.bbtb.api.domain;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import net.raebiger.bbtb.model.Position;

@XmlRootElement
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
public class PositionDomain extends AbstractDomain<Position> {

	private String name;

	private String abbreviation;

	private int quantity;

	public PositionDomain() {
		// needed by JAX-RS
		super();
	}

	public PositionDomain(Position model) {
		super(model);
		name = model.getName();
		abbreviation = model.getAbbreviation();
		quantity = model.getQuantity();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAbbreviation() {
		return abbreviation;
	}

	public void setAbbreviation(String abbreviation) {
		this.abbreviation = abbreviation;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

}
