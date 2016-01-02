package net.raebiger.bbtb.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;

@Entity
public class Race extends AbstractEntityWithUUID {
	@OneToMany(mappedBy = "race")
	private List<Position> positions = new ArrayList<Position>();;

	public Race() {
		super();
	}

	public void addPosition(Position position) {
		positions.add(position);
		if (position.getRace() != this) {
			position.setRace(this);
		}
	}

	public List<Position> getPositions() {
		return positions;
	}

}
