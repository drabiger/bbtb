package net.raebiger.bbtb.permission;

import net.raebiger.bbtb.model.User;

public interface DeletePermission<T> {
	boolean mayUserDelete(User user, T object);
}
