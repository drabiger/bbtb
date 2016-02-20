package net.raebiger.bbtb.permission;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import net.raebiger.bbtb.model.Position;
import net.raebiger.bbtb.model.Race;
import net.raebiger.bbtb.model.User;
import net.raebiger.bbtb.sessioninfo.SessionInfo;

@Service("positionPermissionManager")
@Transactional(propagation = Propagation.REQUIRED)
public class PositionPermissionManager implements PermissionManager<Position> {
	private List<ReadPermission<Position>> authorizationsUserMayReadObject = new LinkedList<ReadPermission<Position>>();

	private List<DeletePermission<Position>> authorizationsUserMayDeleteObject = new LinkedList<DeletePermission<Position>>();

	private List<ModificationPermission<Position>> authorizationsUserMayModifyObject = new LinkedList<ModificationPermission<Position>>();

	@Autowired
	private SessionInfo sessionInfo;

	@Autowired
	@Qualifier("racePermissionManager")
	private PermissionManager<Race> racePermissionManager;

	public PositionPermissionManager() {
		initAuthorizations();
	}

	private void initAuthorizations() {
		authorizationsUserMayReadObject.add(new ReadPermission<Position>() {

			@Override
			public boolean mayUserRead(User user, Position object) {
				return racePermissionManager.mayUserRead(object.getRace());
			}

		});

		authorizationsUserMayModifyObject.add(new ModificationPermission<Position>() {

			@Override
			public boolean mayUserModify(User user, Position object) {
				return racePermissionManager.mayUserModify(object.getRace());
			}
		});

		authorizationsUserMayDeleteObject.add(new DeletePermission<Position>() {

			@Override
			public boolean mayUserDelete(User user, Position object) {
				return racePermissionManager.mayUserDelete(object.getRace());
			}
		});
	}

	@Override
	public boolean mayUserRead(Position object) {

		for (ReadPermission<Position> auth : authorizationsUserMayReadObject) {
			if (auth.mayUserRead(sessionInfo.getCurrentUser(), object)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean mayUserModify(Position object) {

		for (ModificationPermission<Position> auth : authorizationsUserMayModifyObject) {
			if (auth.mayUserModify(sessionInfo.getCurrentUser(), object)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean mayUserDelete(Position object) {
		for (DeletePermission<Position> auth : authorizationsUserMayDeleteObject) {
			if (auth.mayUserDelete(sessionInfo.getCurrentUser(), object)) {
				return true;
			}
		}

		return false;
	}

}
