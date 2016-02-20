package net.raebiger.bbtb.permission;

import net.raebiger.bbtb.model.User;

public interface ModificationPermission<T> {
	boolean mayUserModify(User user, T object);
}
