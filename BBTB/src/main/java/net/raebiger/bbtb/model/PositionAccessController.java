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

@Repository("positionAccessController")
@Transactional(propagation = Propagation.REQUIRED)
public class PositionAccessController implements AccessController<Position> {
	@Autowired
	PositionDao positionDao;

	@Autowired
	@Qualifier("positionPermissionManager")
	PermissionManager<Position> permissionManager;

	@Autowired
	private SessionInfo sessionInfo;

	private static final Logger LOG = Logger.getLogger("BBTB");

	public void persist(Position position) {
		if (mayUserModify(position)) {
			positionDao.persist(position);
		} else {
			LOG.log(Level.SEVERE, "VIOLATION - User {0} tried to modify position {1}",
					new Object[] { sessionInfo.getCurrentUser().getName(), position.getId() });
			throw new NotAuthorizedException();
		}
	}

	public List<Position> getAll() {
		List<Position> allPositions = positionDao.getAllPositions();
		List<Position> result = filterForReadAccess(allPositions);
		return result;
	}

	public Position getByUuid(String uuid) {
		Position position = positionDao.find(uuid);
		if (isReadable(position)) {
			return position;
		}
		throw new NoResultException();
	}

	public void delete(Position position) {
		if (mayUserDelete(position)) {
			positionDao.delete(position);
		} else {
			LOG.log(Level.SEVERE, "VIOLATION - User {0} tried to delete position {1}",
					new Object[] { sessionInfo.getCurrentUser().getName(), position.getId() });
			throw new NotAuthorizedException();
		}
	}

	public List<Position> filterForReadAccess(List<Position> allBoards) {
		List<Position> result = allBoards.stream().filter(b -> permissionManager.mayUserRead(b))
				.collect(Collectors.toList());
		return result;
	}

	public boolean isReadable(Position object) {
		return permissionManager.mayUserRead(object);
	}

	public boolean mayUserDelete(Position object) {
		if (permissionManager.mayUserDelete(object)) {
			return true;
		} else {
			LOG.log(Level.SEVERE, "VIOLATION User '{0}' tried to delete board '{1}'!",
					new Object[] { sessionInfo.getCurrentUser().getName(), object.getUUID() });
			return false;
		}
	}

	public boolean mayUserModify(Position object) {
		return permissionManager.mayUserModify(object);
	}
}
