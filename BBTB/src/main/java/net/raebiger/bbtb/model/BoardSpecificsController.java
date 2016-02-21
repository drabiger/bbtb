package net.raebiger.bbtb.model;

import java.util.List;

public interface BoardSpecificsController extends AccessController<Board> {
	public BoardPlacement findPlacementOrNull(Board board, int x, int y);

	public List<Board> getMyBoards();
}
