package Mobs;

import Algorithms.Direction2D;
import Algorithms.Direction2D.Direction;
import Maze.Maze;
import Panel.MainForm;

public final class PacMan extends Mob {
	/*
	 * 玩家物件
	 */
	
	// 計時器
	private static final int maxMoveTick = (int) MainForm.FPS >> 2, maxGraphicTick = (int) MainForm.FPS / 10;
	private int moveTick, graphicTick;
	
	// 供繪圖用的狀態
	private int graphicState;
	
	// 用於判斷玩家是否已按下第一次移動鍵
	private boolean moving;

	public PacMan(int row, int col) {
		this.row = row;
		this.col = col;
		this.facing = Direction.right;
		this.moveTick = maxMoveTick;
		this.graphicTick = this.graphicState = 0;
		this.moving = false;
	}

	public int getGraphicState() {
		return graphicState;
	}

	public boolean isMoving() {
		return moving;
	}

	public void setMoving(boolean moving) {
		this.moving = moving;
	}

	@Override
	public void move(Maze maze) {
		
		// 移動判斷
		if (moveTick < maxMoveTick)
			moveTick++;

		if (moving) {
			if (moveTick == maxMoveTick) {
				moveTick = 0;
				int[] nextDirection = Direction2D.directionToArray(facing);
				int nextRow = row + nextDirection[0];
				int nextCol = col + nextDirection[1];

				if (!maze.isWall(nextRow, nextCol)) {
					row = nextRow;
					col = nextCol;
				}
			}
		}

		// 更新繪圖狀態
		if (graphicTick < maxGraphicTick)
			graphicTick++;
		else {
			graphicTick = 0;
			graphicState++;

			if (graphicState == 4)
				graphicState = 0;
		}

	}

}
