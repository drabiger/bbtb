package net.raebiger.bbtb.model;

import java.util.List;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("positionDao")
@Transactional(propagation = Propagation.REQUIRED)
public class PositionDao {

	private EntityManager entityManager;

	private static final Logger LOG = Logger.getLogger("BBTB");

	public EntityManager getEntityManager() {
		return entityManager;
	}

	@PersistenceContext
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public void persist(Position position) {
		entityManager.persist(position);
	}

	public List<Position> getAllPositions() {
		LOG.entering(getClass().getName(), "getAllPositions");
		TypedQuery<Position> query = entityManager.createQuery("SELECT p FROM Position p ORDER BY p.id",
				Position.class);
		List<Position> resultList = query.getResultList();
		return resultList;
	}

	public Position find(String uuid) {
		TypedQuery<Position> query = entityManager.createQuery("SELECT p FROM Position p WHERE p.uuid = :uuid",
				Position.class);
		query.setParameter("uuid", uuid);
		return query.getSingleResult();
	}

	public void delete(Position race) {
		entityManager.remove(race);
	}

}
