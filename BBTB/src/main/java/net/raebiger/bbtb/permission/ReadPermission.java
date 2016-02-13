package net.raebiger.bbtb.permission;

import net.raebiger.bbtb.model.User;

public interface ReadPermission {

	boolean mayUserRead(User user, PermissionObject object);

}
