package net.raebiger.bbtb.model;

import java.util.List;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("raceDao")
@Transactional(propagation = Propagation.REQUIRED)
public class RaceDao {

	private EntityManager entityManager;

	private static final Logger LOG = Logger.getLogger("BBTB");

	public EntityManager getEntityManager() {
		return entityManager;
	}

	@PersistenceContext
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public void persist(Race race) {
		entityManager.persist(race);
	}

	public List<Race> getAllRaces() {
		LOG.entering(getClass().getName(), "getAllRaces");
		TypedQuery<Race> query = entityManager.createQuery("SELECT r FROM Race r ORDER BY r.id", Race.class);
		List<Race> resultList = query.getResultList();
		return resultList;
	}

	public Race find(String uuid) {
		TypedQuery<Race> query = entityManager.createQuery("SELECT r FROM Race r WHERE r.uuid = :uuid", Race.class);
		query.setParameter("uuid", uuid);
		return query.getSingleResult();
	}

	public void delete(Race race) {
		entityManager.remove(race);
	}

}
