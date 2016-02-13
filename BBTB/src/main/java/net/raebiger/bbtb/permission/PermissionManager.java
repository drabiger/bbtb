package net.raebiger.bbtb.permission;

import java.util.LinkedList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import net.raebiger.bbtb.model.Board;
import net.raebiger.bbtb.model.User;

@Service("authManager")
@Transactional(propagation = Propagation.REQUIRED)
public class PermissionManager {
	private List<ReadPermission> authorizationsUserMayReadObject = new LinkedList<ReadPermission>();

	public PermissionManager() {
		initAuthorizations();
	}

	private void initAuthorizations() {
		authorizationsUserMayReadObject.add(new ReadPermission() {

			@Override
			public boolean mayUserRead(User user, PermissionObject object) {
				if (object instanceof Board) {
					if (user.equals(((Board) object).getCreator())) {
						return true;
					}
				}
				return false;
			}
		});
	}

	public boolean mayUserRead(User user, PermissionObject object) {

		for (ReadPermission auth : authorizationsUserMayReadObject) {
			if (auth.mayUserRead(user, object)) {
				return true;
			}
		}

		return false;
	}
}
