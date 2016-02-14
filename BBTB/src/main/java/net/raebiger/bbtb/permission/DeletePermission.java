package net.raebiger.bbtb.permission;

import net.raebiger.bbtb.model.User;

public interface DeletePermission {
	boolean mayUserDelete(User user, PermissionObject object);
}
