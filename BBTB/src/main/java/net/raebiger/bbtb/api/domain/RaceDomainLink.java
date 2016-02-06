package net.raebiger.bbtb.api.domain;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import net.raebiger.bbtb.model.Race;

@XmlRootElement
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
public class RaceDomainLink extends AbstractDomainLink<Race> {

	public RaceDomainLink() {
		// needed by JAX-RS
	}

	public RaceDomainLink(Race race) {
		super(race);
	}

	@Override
	@XmlTransient
	public String getApiResourceExtension() {
		return "races";
	}
}
