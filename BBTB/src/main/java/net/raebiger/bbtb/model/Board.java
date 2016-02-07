package net.raebiger.bbtb.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class Board extends AbstractEntityWithUUID {
	@Column(name = "description")
	private String description;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "RACE1_ID")
	private Race race1;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "RACE2_ID")
	private Race race2;

	@Column(name = "colorRace1")
	private String colorRace1;

	@Column(name = "colorRace2")
	private String colorRace2;

	@OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<BoardPlacement> placements = new ArrayList<BoardPlacement>();

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USER_ID")
	private User creator;

	public User getCreator() {
		return creator;
	}

	public void setCreator(User creator) {
		this.creator = creator;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String value) {
		description = value;
	}

	public Race getRace1() {
		return race1;
	}

	public void setRace1(Race race1) {
		this.race1 = race1;
	}

	public Race getRace2() {
		return race2;
	}

	public void setRace2(Race race2) {
		this.race2 = race2;
	}

	public String getColorRace1() {
		return colorRace1;
	}

	public void setColorRace1(String colorRace1) {
		this.colorRace1 = colorRace1;
	}

	public String getColorRace2() {
		return colorRace2;
	}

	public void setColorRace2(String colorRace2) {
		this.colorRace2 = colorRace2;
	}

	public BoardPlacement addPlacement(int x, int y, Position position) {
		if (position.getRace() != getRace1() && position.getRace() != getRace2()) {
			throw new IndexOutOfBoundsException(
					"The input position does not belong to either race1 or race2 of this board.");
		}

		long numberOfPlacementsForThisRace = placements.stream()
				.filter(p -> p.getPosition().getRace().equals(position.getRace())).count();
		if (numberOfPlacementsForThisRace > 10) {
			throw new IndexOutOfBoundsException("Limit of 11 for this race on board reached.");
		}

		long numberOfPlacementsForThisPosition = placements.stream().filter(p -> p.getPosition().equals(position))
				.count();
		if (numberOfPlacementsForThisPosition >= position.getQuantity()) {
			throw new IndexOutOfBoundsException("Limit for given position reached.");
		}

		BoardPlacement placement = new BoardPlacement(this, x, y, position);
		placements.add(placement);
		return placement;
	}

	public List<BoardPlacement> getPlacements() {
		return placements;
	}

	public boolean removePlacement(BoardPlacement placement) {
		return placements.remove(placement);
	}
}
