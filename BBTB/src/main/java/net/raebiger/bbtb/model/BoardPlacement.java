package net.raebiger.bbtb.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class BoardPlacement extends AbstractEntityWithUUID {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "POSITION_ID", nullable = false)
	private Position position;

	@Column(name = "x", nullable = false)
	private int x;

	@Column(name = "y", nullable = false)
	private int y;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "BOARD_ID", nullable = false)
	private Board board;

	public BoardPlacement() {
		// needed by JPA
		super();
	}

	public BoardPlacement(Board board, int x, int y, Position position) {
		super();
		setBoard(board);
		setX(x);
		setY(y);
		setPosition(position);
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		if (x > 14 || x < 0) {
			throw new IndexOutOfBoundsException("x value must be >= 0, <= 14.");
		}
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		if (y > 25 || y < 0) {
			throw new IndexOutOfBoundsException("y value must be >= 0, <= 25.");
		}
		this.y = y;
	}

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	public Board getBoard() {
		return board;
	}

	public void setBoard(Board board) {
		this.board = board;
	}

}
