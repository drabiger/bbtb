package net.raebiger.bbtb.permission;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import net.raebiger.bbtb.model.User;
import net.raebiger.bbtb.sessioninfo.SessionInfo;

@Service("userPermissionManager")
@Transactional(propagation = Propagation.REQUIRED)
public class UserPermissionManager implements PermissionManager<User> {
	private List<ReadPermission<User>> authorizationsUserMayReadObject = new LinkedList<ReadPermission<User>>();

	private List<DeletePermission<User>> authorizationsUserMayDeleteObject = new LinkedList<DeletePermission<User>>();

	private List<ModificationPermission<User>> authorizationsUserMayModifyObject = new LinkedList<ModificationPermission<User>>();

	@Autowired
	private SessionInfo sessionInfo;

	public UserPermissionManager() {
		initAuthorizations();
	}

	private void initAuthorizations() {
		authorizationsUserMayReadObject.add(new ReadPermission<User>() {

			@Override
			public boolean mayUserRead(User user, User object) {
				// TODO
				return true;
			}

		});

		authorizationsUserMayModifyObject.add(new ModificationPermission<User>() {

			@Override
			public boolean mayUserModify(User user, User object) {
				// TODO
				return true;
			}
		});

		authorizationsUserMayDeleteObject.add(new DeletePermission<User>() {

			@Override
			public boolean mayUserDelete(User user, User object) {
				// TODO
				return false;
			}
		});
	}

	@Override
	public boolean mayUserRead(User object) {

		for (ReadPermission<User> auth : authorizationsUserMayReadObject) {
			if (auth.mayUserRead(sessionInfo.getCurrentUser(), object)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean mayUserModify(User object) {

		for (ModificationPermission<User> auth : authorizationsUserMayModifyObject) {
			if (auth.mayUserModify(sessionInfo.getCurrentUser(), object)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean mayUserDelete(User object) {
		for (DeletePermission<User> auth : authorizationsUserMayDeleteObject) {
			if (auth.mayUserDelete(sessionInfo.getCurrentUser(), object)) {
				return true;
			}
		}

		return false;
	}

}
