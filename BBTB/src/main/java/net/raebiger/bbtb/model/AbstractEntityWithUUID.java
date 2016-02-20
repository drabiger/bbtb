package net.raebiger.bbtb.model;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import net.raebiger.bbtb.permission.PermissionObject;

@MappedSuperclass
public abstract class AbstractEntityWithUUID implements PermissionObject {
	// TODO uuid needs to be secured before handing over to clients

	@Column(name = "uuid")
	protected String uuid;

	@Column(name = "name", length = 32)
	protected String name;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private long id;

	public AbstractEntityWithUUID() {
		uuid = UUID.randomUUID().toString();
	}

	protected long getId() {
		return id;
	}

	public String getUUID() {
		return uuid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
