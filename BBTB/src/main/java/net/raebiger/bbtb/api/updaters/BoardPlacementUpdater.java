package net.raebiger.bbtb.api.updaters;

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.core.Response;

import net.raebiger.bbtb.api.domain.BoardPlacementDomain;
import net.raebiger.bbtb.model.AccessController;
import net.raebiger.bbtb.model.Board;
import net.raebiger.bbtb.model.BoardPlacement;
import net.raebiger.bbtb.model.BoardSpecificsController;
import net.raebiger.bbtb.model.Position;

public class BoardPlacementUpdater {
	private BoardPlacementDomain		input;
	private AccessController<Board>		boardAccManager;
	private AccessController<Position>	positionAccController;
	private String						boardUUID;

	private static final Logger	LOG	= Logger.getLogger("BBTB");
	private String				placementUUID;

	public BoardPlacementUpdater(String placementUUIDOrNullIfPlacementIsToBeCreated, BoardPlacementDomain input,
			String boardUUID, AccessController<Board> boardAccManager,
			AccessController<Position> positionAccController) {
		this.placementUUID = placementUUIDOrNullIfPlacementIsToBeCreated;
		this.input = input;
		this.boardUUID = boardUUID;
		this.boardAccManager = boardAccManager;
		this.positionAccController = positionAccController;
	}

	public Response update() {
		BoardPlacement existingPlacement;
		Board board = boardAccManager.getByUuid(boardUUID);
		if (((BoardSpecificsController) boardAccManager).findPlacementOrNull(board, input.getX(),
				input.getY()) != null) {
			return Response.status(Response.Status.BAD_REQUEST).entity("Target coordinates are occupied by a placement")
					.build();
		}

		boolean dirty = false;
		if (placementUUID != null) {
			Optional<BoardPlacement> placementOptional = board.getPlacements().stream()
					.filter(p -> placementUUID.equals(p.getUUID())).findAny();
			if (!placementOptional.isPresent()) {
				LOG.log(Level.INFO, "Placement UUID {} does not exist", placementUUID);
				return Response.status(Response.Status.BAD_REQUEST).entity("Placement UUID does not exist").build();
			}
			existingPlacement = placementOptional.get();
		} else {
			// placement must be created
			Position position = positionAccController.getByUuid(input.getPosition().getUUID());
			existingPlacement = board.addPlacement(input.getX(), input.getY(), position, input.getTeam());
			dirty = true;
		}

		if (!existingPlacement.getPosition().getUUID().equals(input.getPosition().getUUID())) {
			return Response.status(Response.Status.BAD_REQUEST)
					.entity("You are not allowed to modify the position of a placement").build();
		}

		if (existingPlacement.getX() != input.getX()) {
			existingPlacement.setX(input.getX());
			dirty = true;
		}

		if (existingPlacement.getY() != input.getY()) {
			existingPlacement.setY(input.getY());
			dirty = true;
		}

		if (dirty) {
			boardAccManager.persist(board);
		}
		return Response.ok().build();
	}

	public Response delete() {
		BoardPlacement existingPlacement;
		Board board = boardAccManager.getByUuid(boardUUID);
		Optional<BoardPlacement> placementOptional = board.getPlacements().stream()
				.filter(p -> placementUUID.equals(p.getUUID())).findAny();
		if (!placementOptional.isPresent()) {
			LOG.log(Level.INFO, "Placement UUID {} does not exist", placementUUID);
			return Response.status(Response.Status.BAD_REQUEST).entity("Placement UUID does not exist").build();
		}
		existingPlacement = placementOptional.get();

		if (!board.removePlacement(existingPlacement)) {
			return Response.status(Response.Status.BAD_REQUEST).entity("Placement is not on given board.").build();
		}

		boardAccManager.persist(board);

		return Response.noContent().build();
	}

}
