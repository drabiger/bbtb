package net.raebiger.bbtb.model;

public interface UserSpecificsAccessController extends AccessController<User> {
	public User findByEmailOrNull(String email);

	public User findByDisplayNameOrNull(String displayName);
}
