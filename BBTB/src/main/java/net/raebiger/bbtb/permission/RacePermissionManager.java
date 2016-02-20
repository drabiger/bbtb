package net.raebiger.bbtb.permission;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import net.raebiger.bbtb.model.Race;
import net.raebiger.bbtb.model.User;
import net.raebiger.bbtb.sessioninfo.SessionInfo;

@Service("racePermissionManager")
@Transactional(propagation = Propagation.REQUIRED)
public class RacePermissionManager implements PermissionManager<Race> {
	private List<ReadPermission<Race>> authorizationsUserMayReadObject = new LinkedList<ReadPermission<Race>>();

	private List<DeletePermission<Race>> authorizationsUserMayDeleteObject = new LinkedList<DeletePermission<Race>>();

	private List<ModificationPermission<Race>> authorizationsUserMayModifyObject = new LinkedList<ModificationPermission<Race>>();

	@Autowired
	private SessionInfo sessionInfo;

	public RacePermissionManager() {
		initAuthorizations();
	}

	private void initAuthorizations() {
		authorizationsUserMayReadObject.add(new ReadPermission<Race>() {

			@Override
			public boolean mayUserRead(User user, Race object) {
				// TODO
				return true;
			}

		});

		authorizationsUserMayModifyObject.add(new ModificationPermission<Race>() {

			@Override
			public boolean mayUserModify(User user, Race object) {
				// TODO
				return true;
			}
		});

		authorizationsUserMayDeleteObject.add(new DeletePermission<Race>() {

			@Override
			public boolean mayUserDelete(User user, Race object) {
				// TODO
				return true;
			}
		});
	}

	@Override
	public boolean mayUserRead(Race object) {

		for (ReadPermission<Race> auth : authorizationsUserMayReadObject) {
			if (auth.mayUserRead(sessionInfo.getCurrentUser(), object)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean mayUserModify(Race object) {

		for (ModificationPermission<Race> auth : authorizationsUserMayModifyObject) {
			if (auth.mayUserModify(sessionInfo.getCurrentUser(), object)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean mayUserDelete(Race object) {
		for (DeletePermission<Race> auth : authorizationsUserMayDeleteObject) {
			if (auth.mayUserDelete(sessionInfo.getCurrentUser(), object)) {
				return true;
			}
		}

		return false;
	}

}
