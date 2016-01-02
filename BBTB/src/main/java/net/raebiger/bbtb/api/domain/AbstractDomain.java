package net.raebiger.bbtb.api.domain;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import net.raebiger.bbtb.model.AbstractEntityWithUUID;

@XmlRootElement
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
public class AbstractDomain<T extends AbstractEntityWithUUID> {
	// TODO make this configurable
	static private String baseApiUrl = "http://localhost:8080/bbtb/api/";

	private T model;

	private String uuid;

	static String getBaseApiUrl() {
		return baseApiUrl;
	}

	public AbstractDomain() {
		// needed by JAX-RS
	}

	public AbstractDomain(T model) {
		this.model = model;
		uuid = model.getUUID();
	}

	public String getUUID() {
		return uuid;
	}

	public void setUUID(String uuid) {
		this.uuid = uuid;
	}

	@XmlTransient
	public T getModelOrNull() {
		return model;
	}

	@XmlTransient
	public void attachModel(T model) {
		this.model = model;
		uuid = model.getUUID();
	}
}
