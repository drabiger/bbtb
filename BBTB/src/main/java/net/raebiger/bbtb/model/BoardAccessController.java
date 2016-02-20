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

@Repository("boardAccessController")
@Transactional(propagation = Propagation.REQUIRED)
public class BoardAccessController implements AccessController<Board>, BoardSpecificsController {
	@Autowired
	BoardDao boardDao;

	@Autowired
	@Qualifier("boardPermissionManager")
	PermissionManager<Board> permissionManager;

	@Autowired
	private SessionInfo sessionInfo;

	private static final Logger LOG = Logger.getLogger("BBTB");

	public Board getByUuid(String uuid) {
		Board board = boardDao.find(uuid);
		if (isReadable(board)) {
			return board;
		}
		throw new NoResultException();
	}

	public List<Board> getAll() {
		List<Board> allBoards = boardDao.getAllBoards();
		List<Board> result = filterForReadAccess(allBoards);
		return result;
	}

	public void persist(Board board) {
		if (mayUserModify(board)) {
			boardDao.persist(board);
		} else {
			throw new NotAuthorizedException();
		}
	}

	public void delete(Board board) {
		if (mayUserDelete(board)) {
			boardDao.delete(board);
		} else {
			throw new NotAuthorizedException();
		}
	}

	public BoardPlacement findPlacementOrNull(Board board, int x, int y) {
		if (isReadable(board)) {
			return boardDao.findPlacementOrNull(board, x, y);
		}
		throw new NoResultException();
	}

	public List<Board> filterForReadAccess(List<Board> allBoards) {
		List<Board> result = allBoards.stream().filter(b -> permissionManager.mayUserRead(b))
				.collect(Collectors.toList());
		return result;
	}

	public boolean isReadable(Board object) {
		return permissionManager.mayUserRead(object);
	}

	public boolean mayUserDelete(Board object) {
		if (permissionManager.mayUserDelete(object)) {
			return true;
		} else {
			LOG.log(Level.SEVERE, "VIOLATION User '{0}' tried to delete board '{1}'!",
					new Object[] { sessionInfo.getCurrentUser().getName(), object.getUUID() });
			return false;
		}
	}

	public boolean mayUserModify(Board object) {
		return permissionManager.mayUserModify(object);
	}

}
