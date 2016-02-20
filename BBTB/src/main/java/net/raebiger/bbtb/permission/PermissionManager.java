package net.raebiger.bbtb.permission;

public interface PermissionManager<T extends PermissionObject> {

	boolean mayUserRead(T object);

	boolean mayUserModify(T object);

	boolean mayUserDelete(T object);

}