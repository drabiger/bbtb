package net.raebiger.bbtb.api.domain;

import net.raebiger.bbtb.model.AbstractEntityWithUUID;

public abstract class AbstractDomainLink<T extends AbstractEntityWithUUID> {

	protected String name;
	protected String uuid;

	T model;

	protected AbstractDomainLink() {
		super();
	}

	public AbstractDomainLink(T model) {
		this.model = model;
		name = model.getName();
		uuid = model.getUUID();
	}

	public String getName() {
		return name;
	}

	public String getUUID() {
		return uuid;
	}

	public void setUUID(String uuid) {
		this.uuid = uuid;
	}

	public void setHref(String href) {
		// do nothing. this is a read-only property
	}

	public abstract String getApiResourceExtension();

	public String getHref() {
		return AbstractDomain.getBaseApiUrl() + getApiResourceExtension() + "/" + getUUID();
	}

}