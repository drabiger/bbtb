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

@Repository("raceAccessController")
@Transactional(propagation = Propagation.REQUIRED)
public class RaceAccessController implements AccessController<Race> {
	@Autowired
	@Qualifier("racePermissionManager")
	PermissionManager<Race> permissionManager;

	@Autowired
	private RaceDao raceDao;

	@Autowired
	private SessionInfo sessionInfo;

	private static final Logger LOG = Logger.getLogger("BBTB");

	public void persist(Race race) {
		if (mayUserModify(race)) {
			raceDao.persist(race);
		} else {
			LOG.log(Level.SEVERE, "VIOLATION - User {0} tried to modify position {1}",
					new Object[] { sessionInfo.getCurrentUser().getName(), race.getId() });
			throw new NotAuthorizedException();
		}
	}

	public List<Race> getAll() {
		List<Race> allRaces = raceDao.getAllRaces();
		return filterForReadAccess(allRaces);
	}

	public Race getByUuid(String uuid) {
		Race race = raceDao.find(uuid);
		if (isReadable(race)) {
			return race;
		}
		throw new NoResultException();
	}

	public void delete(Race race) {
		if (mayUserDelete(race)) {
			raceDao.delete(race);
		} else {
			LOG.log(Level.SEVERE, "VIOLATION - User {0} tried to delete race {1}",
					new Object[] { sessionInfo.getCurrentUser().getName(), race.getId() });
			throw new NotAuthorizedException();
		}
	}

	public List<Race> filterForReadAccess(List<Race> allRaces) {
		List<Race> result = allRaces.stream().filter(b -> permissionManager.mayUserRead(b))
				.collect(Collectors.toList());
		return result;
	}

	public boolean isReadable(Race object) {
		return permissionManager.mayUserRead(object);
	}

	public boolean mayUserDelete(Race object) {
		if (permissionManager.mayUserDelete(object)) {
			return true;
		} else {
			LOG.log(Level.SEVERE, "VIOLATION User '{0}' tried to delete race '{1}'!",
					new Object[] { sessionInfo.getCurrentUser().getName(), object.getUUID() });
			return false;
		}
	}

	public boolean mayUserModify(Race object) {
		return permissionManager.mayUserModify(object);
	}
}
