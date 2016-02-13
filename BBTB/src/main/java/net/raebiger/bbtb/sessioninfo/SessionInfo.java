package net.raebiger.bbtb.sessioninfo;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

import net.raebiger.bbtb.model.User;

@Service("sessionInfo")
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class SessionInfo {
	private User currentUser;

	public void setCurrentUser(User user) {
		currentUser = user;
	}

	public User getCurrentUser() {
		return currentUser;
	}
}
