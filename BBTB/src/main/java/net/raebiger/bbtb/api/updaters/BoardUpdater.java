package net.raebiger.bbtb.api.updaters;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.ws.rs.core.Response;

import net.raebiger.bbtb.api.domain.BoardDomain;
import net.raebiger.bbtb.api.domain.BoardPlacementDomain;
import net.raebiger.bbtb.model.Board;
import net.raebiger.bbtb.model.BoardDao;
import net.raebiger.bbtb.model.BoardPlacement;
import net.raebiger.bbtb.model.Race;
import net.raebiger.bbtb.model.RaceDao;

public class BoardUpdater {
	private BoardDomain	input;
	private BoardDao	boardDao;

	private static final Logger	LOG	= Logger.getLogger("BBTB");
	private RaceDao				raceDao;

	public BoardUpdater(BoardDomain input, BoardDao boardDao, RaceDao raceDao) {
		this.input = input;
		this.boardDao = boardDao;
		this.raceDao = raceDao;
	}

	public Response update() {

		Board existingBoard = boardDao.find(input.getUUID());
		if (existingBoard == null) {
			return Response.status(Response.Status.NOT_FOUND).entity("There is no board for given uuid.").build();
		}

		boolean dirty = false;
		String trimmedBoardName = input.getName().trim();
		if (trimmedBoardName != null && !trimmedBoardName.equals(existingBoard.getName())) {
			existingBoard.setName(trimmedBoardName);
			dirty = true;
		}

		if (input.getRace1() != null && !input.getRace1().getUUID().equals(existingBoard.getRace1().getUUID())) {
			Race newRace1 = raceDao.find(input.getRace1().getUUID());
			if (newRace1 == null) {
				LOG.log(Level.INFO, "UUID of race1 '{0}' does not match any existing race", input.getRace1().getUUID());
				return Response.status(Response.Status.BAD_REQUEST)
						.entity("UUID of race1 does not match any existing race").build();
			}
			existingBoard.setRace1(newRace1);
			dirty = true;
		}

		if (input.getRace2() != null && !input.getRace2().getUUID().equals(existingBoard.getRace2().getUUID())) {
			Race newRace2 = raceDao.find(input.getRace2().getUUID());
			if (newRace2 == null) {
				LOG.log(Level.INFO, "UUID of race2 '{0}' does not match any existing race", input.getRace2().getUUID());
				return Response.status(Response.Status.BAD_REQUEST)
						.entity("UUID of race2 does not match any existing race").build();
			}
			existingBoard.setRace2(newRace2);
			dirty = true;
		}

		if (input.getColorRace1() != null && !input.getColorRace1().equals(existingBoard.getColorRace1())) {
			existingBoard.setColorRace1(input.getColorRace1());
			dirty = true;
		}

		if (input.getColorRace2() != null && !input.getColorRace2().equals(existingBoard.getColorRace2())) {
			existingBoard.setColorRace2(input.getColorRace2());
			dirty = true;
		}

		Map<String, BoardPlacementDomain> uuidsOfInputPlacements = input.getPlacements().stream()
				.collect(Collectors.toMap(p -> p.getUUID(), p -> p));

		Map<String, BoardPlacement> uuidsOfExistingPlacements = existingBoard.getPlacements().stream()
				.collect(Collectors.toMap(p -> p.getUUID(), p -> p));

		Map<String, BoardPlacement> uuidsOfRemovedPlacements = existingBoard.getPlacements().stream()
				.filter(p -> !uuidsOfInputPlacements.containsKey(p.getUUID()))
				.collect(Collectors.toMap(p -> p.getUUID(), p -> p));

		Map<String, BoardPlacementDomain> uuidsOfAddedPlacements = input.getPlacements().stream()
				.filter(p -> !uuidsOfInputPlacements.containsKey(p.getUUID()))
				.collect(Collectors.toMap(p -> p.getUUID(), p -> p));

		Map<String, BoardPlacementDomain> uuidsOfProbablyModifiedPlacements = input.getPlacements().stream()
				.filter(p -> uuidsOfInputPlacements.containsKey(p.getUUID()))
				.collect(Collectors.toMap(p -> p.getUUID(), p -> p));

		// TODO this was never finished

		// for (Entry<String, BoardPlacementDomain> entry :
		// uuidsOfProbablyModifiedPlacements.entrySet()) {
		// String uuid = entry.getKey();
		// BoardPlacementDomain inputPlacement = entry.getValue();
		// BoardPlacement existingPlacement =
		// uuidsOfExistingPlacements.get(uuid);
		// if (existingPlacement.getX() != inputPlacement.getX()) {
		// dirty = true;
		// existingPlacement.setX(inputPlacement.set);
		// }
		// }

		if (dirty) {
			boardDao.persist(existingBoard);
		}

		Response response = Response.ok().build();
		return response;
	}
}
