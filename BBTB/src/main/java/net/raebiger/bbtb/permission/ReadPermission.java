package net.raebiger.bbtb.permission;

import net.raebiger.bbtb.model.User;

public interface ReadPermission<T extends PermissionObject> {

	boolean mayUserRead(User user, T object);

}
