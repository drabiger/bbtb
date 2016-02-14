package net.raebiger.bbtb.permission;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import net.raebiger.bbtb.model.Board;
import net.raebiger.bbtb.model.User;
import net.raebiger.bbtb.sessioninfo.SessionInfo;

@Service("authManager")
@Transactional(propagation = Propagation.REQUIRED)
public class PermissionManager {
	private List<ReadPermission> authorizationsUserMayReadObject = new LinkedList<ReadPermission>();

	private List<DeletePermission> authorizationsUserMayDeleteObject = new LinkedList<DeletePermission>();

	@Autowired
	private SessionInfo sessionInfo;

	public PermissionManager() {
		initAuthorizations();
	}

	private boolean isOwnerOfBoard(User user, PermissionObject object) {
		if (object instanceof Board) {
			if (user.equals(((Board) object).getCreator())) {
				return true;
			}
		}
		return false;
	}

	private void initAuthorizations() {
		authorizationsUserMayReadObject.add(new ReadPermission() {

			@Override
			public boolean mayUserRead(User user, PermissionObject object) {
				return isOwnerOfBoard(user, object);
			}

		});

		authorizationsUserMayDeleteObject.add(new DeletePermission() {

			@Override
			public boolean mayUserDelete(User user, PermissionObject object) {
				return isOwnerOfBoard(user, object);
			}
		});
	}

	public boolean mayUserRead(PermissionObject object) {

		for (ReadPermission auth : authorizationsUserMayReadObject) {
			if (auth.mayUserRead(sessionInfo.getCurrentUser(), object)) {
				return true;
			}
		}

		return false;
	}

	public boolean mayDelete(PermissionObject object) {
		for (DeletePermission auth : authorizationsUserMayDeleteObject) {
			if (auth.mayUserDelete(sessionInfo.getCurrentUser(), object)) {
				return true;
			}
		}

		return false;
	}
}
