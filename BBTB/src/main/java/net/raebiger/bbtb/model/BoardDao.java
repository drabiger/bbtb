package net.raebiger.bbtb.model;

import java.util.List;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("boardDao")
@Transactional(propagation = Propagation.REQUIRED)
public class BoardDao {

	private EntityManager entityManager;

	private static final Logger LOG = Logger.getLogger("BBTB");

	public EntityManager getEntityManager() {
		return entityManager;
	}

	@PersistenceContext
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public void persist(Board board) {
		entityManager.persist(board);
	}

	public List<Board> getAllBoards() {
		LOG.entering(getClass().getName(), "getAllBoards");
		TypedQuery<Board> query = entityManager.createQuery("SELECT b FROM Board b ORDER BY b.id", Board.class);
		List<Board> resultList = query.getResultList();
		return resultList;
	}

	public Board find(String uuid) {
		TypedQuery<Board> query = entityManager.createQuery("SELECT b FROM Board b WHERE b.uuid = :uuid", Board.class);
		query.setParameter("uuid", uuid);
		return query.getSingleResult();
	}

	public BoardPlacement findPlacementOrNull(Board board, int x, int y) {
		TypedQuery<BoardPlacement> query = entityManager.createQuery(
				"SELECT p FROM BoardPlacement p WHERE p.x = :pX AND p.y = :pY AND p.id = :boardId",
				BoardPlacement.class);
		query.setParameter("pX", x);
		query.setParameter("pY", y);
		query.setParameter("boardId", board.getId());
		List<BoardPlacement> resultList = query.getResultList();
		if (resultList.size() == 0) {
			return null;
		} else if (resultList.size() == 1) {
			return resultList.get(0);
		} else {
			throw new NonUniqueResultException();
		}

	}

	public void delete(Board board) {
		entityManager.remove(board);
	}

}
