package Mobs;

import Algorithms.Direction2D.Direction;
import Maze.Maze;

public abstract class Mob {
	/*
	 * 抽象類別，僅用於繼承
	 */

	protected int row, col;
	protected Direction facing;

	public final int getRow() {
		return row;
	}

	public final void setRow(int row) {
		this.row = row;
	}

	public final int getCol() {
		return col;
	}

	public final void setCol(int col) {
		this.col = col;
	}

	public final Direction getDirection() {
		return facing;
	}

	public final void setDirection(Direction facing) {
		this.facing = facing;
	}

	// 用於執行每幀行動
	public abstract void move(Maze maze);

}
