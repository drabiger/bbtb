package net.raebiger.bbtb.model;

import java.util.List;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("userDao")
@Transactional(propagation = Propagation.REQUIRED)
class UserDao {

	private EntityManager entityManager;

	private static final Logger LOG = Logger.getLogger("BBTB");

	public EntityManager getEntityManager() {
		return entityManager;
	}

	@PersistenceContext
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public void persist(User user) {
		entityManager.persist(user);
	}

	public List<User> getAllUsers() {
		LOG.entering(getClass().getName(), "getAllUsers");
		TypedQuery<User> query = entityManager.createQuery("SELECT u FROM BBTBUser u ORDER BY u.id", User.class);
		List<User> resultList = query.getResultList();
		return resultList;
	}

	public User find(String uuid) {
		TypedQuery<User> query = entityManager.createQuery("SELECT u FROM BBTBUser u WHERE u.uuid = :uuid", User.class);
		query.setParameter("uuid", uuid);
		return query.getSingleResult();
	}

	public User findByEmailOrNull(String email) {
		TypedQuery<User> query = entityManager.createQuery("SELECT u FROM BBTBUser u WHERE u.email = :email",
				User.class);
		query.setParameter("email", email);
		int size = query.getResultList().size();
		if (size == 0) {
			return null;
		} else {
			return query.getSingleResult();
		}
	}

	public User findByDisplayNameOrNull(String displayName) {
		TypedQuery<User> query = entityManager.createQuery("SELECT u FROM BBTBUser u WHERE u.name = :displayName",
				User.class);
		query.setParameter("displayName", displayName);
		int size = query.getResultList().size();
		if (size == 0) {
			return null;
		} else {
			return query.getSingleResult();
		}
	}

	public void delete(User user) {
		entityManager.remove(user);
	}

}
