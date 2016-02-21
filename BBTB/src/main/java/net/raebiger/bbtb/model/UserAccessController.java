package net.raebiger.bbtb.model;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.persistence.NoResultException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import net.raebiger.bbtb.permission.NotAuthorizedException;
import net.raebiger.bbtb.permission.PermissionManager;
import net.raebiger.bbtb.sessioninfo.SessionInfo;

@Repository("userAccessController")
@Transactional(propagation = Propagation.REQUIRED)
public class UserAccessController implements UserSpecificsAccessController {
	@Autowired
	@Qualifier("userPermissionManager")
	PermissionManager<User> permissionManager;

	@Autowired
	private UserDao userDao;

	@Autowired
	private SessionInfo sessionInfo;

	private static final Logger LOG = Logger.getLogger("BBTB");

	public void persist(User user) {
		if (mayUserModify(user)) {
			userDao.persist(user);
		} else {
			LOG.log(Level.SEVERE, "VIOLATION - User {0} tried to modify user {1}",
					new Object[] { sessionInfo.getCurrentUser().getName(), user.getId() });
			throw new NotAuthorizedException();
		}
	}

	public List<User> getAll() {
		List<User> allUsers = userDao.getAllUsers();
		return filterForReadAccess(allUsers);
	}

	public User getByUuid(String uuid) {
		User user = userDao.find(uuid);
		if (isReadable(user)) {
			return user;
		}
		throw new NoResultException();
	}

	public void delete(User user) {
		if (mayUserDelete(user)) {
			userDao.delete(user);
		} else {
			LOG.log(Level.SEVERE, "VIOLATION - User {0} tried to delete user {1}",
					new Object[] { sessionInfo.getCurrentUser().getName(), user.getId() });
			throw new NotAuthorizedException();
		}
	}

	public List<User> filterForReadAccess(List<User> allUsers) {
		List<User> result = allUsers.stream().filter(b -> permissionManager.mayUserRead(b))
				.collect(Collectors.toList());
		return result;
	}

	public boolean isReadable(User object) {
		return permissionManager.mayUserRead(object);
	}

	public boolean mayUserDelete(User object) {
		if (permissionManager.mayUserDelete(object)) {
			return true;
		} else {
			LOG.log(Level.SEVERE, "VIOLATION User '{0}' tried to delete user '{1}'!",
					new Object[] { sessionInfo.getCurrentUser().getName(), object.getUUID() });
			return false;
		}
	}

	public boolean mayUserModify(User object) {
		return permissionManager.mayUserModify(object);
	}

	public User findByEmailOrNull(String email) {
		User userOrNull = userDao.findByEmailOrNull(email);
		if (userOrNull != null && permissionManager.mayUserRead(userOrNull)) {
			return userOrNull;
		}
		return null;
	}

	public User findByDisplayNameOrNull(String email) {
		User userOrNull = userDao.findByDisplayNameOrNull(email);
		if (userOrNull != null && permissionManager.mayUserRead(userOrNull)) {
			return userOrNull;
		}
		return null;
	}
}
