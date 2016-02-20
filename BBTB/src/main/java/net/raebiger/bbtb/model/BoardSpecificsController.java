package net.raebiger.bbtb.model;

public interface BoardSpecificsController {
	public BoardPlacement findPlacementOrNull(Board board, int x, int y);
}
