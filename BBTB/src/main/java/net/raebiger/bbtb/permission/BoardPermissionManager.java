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

@Service("boardPermissionManager")
@Transactional(propagation = Propagation.REQUIRED)
public class BoardPermissionManager implements PermissionManager<Board> {
	private List<ReadPermission<Board>> authorizationsUserMayReadObject = new LinkedList<ReadPermission<Board>>();

	private List<DeletePermission<Board>> authorizationsUserMayDeleteObject = new LinkedList<DeletePermission<Board>>();

	private List<ModificationPermission<Board>> authorizationsUserMayModifyObject = new LinkedList<ModificationPermission<Board>>();

	@Autowired
	private SessionInfo sessionInfo;

	public BoardPermissionManager() {
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
		authorizationsUserMayReadObject.add(new ReadPermission<Board>() {

			@Override
			public boolean mayUserRead(User user, Board object) {
				return isOwnerOfBoard(user, object);
			}

		});

		authorizationsUserMayModifyObject.add(new ModificationPermission<Board>() {

			@Override
			public boolean mayUserModify(User user, Board object) {
				return isOwnerOfBoard(user, object);
			}
		});

		authorizationsUserMayDeleteObject.add(new DeletePermission<Board>() {

			@Override
			public boolean mayUserDelete(User user, Board object) {
				return isOwnerOfBoard(user, object);
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.raebiger.bbtb.permission.PermissionManager#mayUserRead(net.raebiger.
	 * bbtb.permission.PermissionObject)
	 */
	@Override
	public boolean mayUserRead(Board object) {

		for (ReadPermission<Board> auth : authorizationsUserMayReadObject) {
			if (auth.mayUserRead(sessionInfo.getCurrentUser(), object)) {
				return true;
			}
		}

		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.raebiger.bbtb.permission.PermissionManager#mayUserModify(net.raebiger
	 * .bbtb.permission.PermissionObject)
	 */
	@Override
	public boolean mayUserModify(Board object) {

		for (ModificationPermission<Board> auth : authorizationsUserMayModifyObject) {
			if (auth.mayUserModify(sessionInfo.getCurrentUser(), object)) {
				return true;
			}
		}

		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.raebiger.bbtb.permission.PermissionManager#mayUserDelete(net.raebiger
	 * .bbtb.permission.PermissionObject)
	 */
	@Override
	public boolean mayUserDelete(Board object) {
		for (DeletePermission<Board> auth : authorizationsUserMayDeleteObject) {
			if (auth.mayUserDelete(sessionInfo.getCurrentUser(), object)) {
				return true;
			}
		}

		return false;
	}

}
