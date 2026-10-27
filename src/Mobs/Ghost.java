package Mobs;

import java.awt.Color;

import Algorithms.Direction2D;
import Algorithms.Direction2D.Direction;
import Algorithms.Search.SearchingType;
import Maze.Maze;
import Panel.MainForm;

public final class Ghost extends Mob {
	/*
	 * 幽靈物件
	 */

	// 追擊方式
	public static enum ChasingType {
		Blinky, Pinky, Inky, Clyde
	}

	public static Color[] ghostColors = { Color.RED, Color.PINK, Color.CYAN, Color.ORANGE };

	// 狀態
	public static enum GhostState {
		Normal, Scared, Dead
	}

	// 計時器
	private static final int maxMoveTick = (int) MainForm.FPS / 3, deadMaxMoveTick = (int) MainForm.FPS >> 3,
			maxScaredTick = (int) MainForm.FPS * 10;
	private int scaredTick, moveTick;

	// 用於繪圖，透過閃爍效果提示玩家Scared Mode時間
	private boolean flashing;

	private SearchingType searchingType;
	private ChasingType chasingType;
	private GhostState state;

	public Ghost(int[] defaultPosition, ChasingType ghostType, SearchingType searchingType) {
		this.row = defaultPosition[0];
		this.col = defaultPosition[1];
		this.chasingType = ghostType;
		this.searchingType = searchingType;
		this.state = GhostState.Normal;
		this.scaredTick = 0;
		this.facing = Direction.down;
		this.flashing = false;
	}

	public final SearchingType getSearchingType() {
		return searchingType;
	}

	public final ChasingType getChasingType() {
		return chasingType;
	}

	// 根據追擊方式回傳對應顏色
	public final Color getColor() {
		return ghostColors[chasingType.ordinal()];
	}

	// 玩家吃到大力丸，進入Scared Mode，並重置計時器
	public final void beenScared() {
		if (state != GhostState.Dead) {
			state = GhostState.Scared;
			scaredTick = 0;
			flashing = false;
		}
	}

	// 被吃，進入Dead Mode，並重置計時器
	public final void beenEaten() {
		state = GhostState.Dead;
		moveTick = 0;
		scaredTick = 0;
		flashing = false;
	}

	// 自Dead Mode恢復正常
	public final void recover() {
		state = GhostState.Normal;
		moveTick = 0;
	}

	public final boolean isDead() {
		return state == GhostState.Dead;
	}

	public final boolean isScared() {
		return state == GhostState.Scared;
	}

	public final boolean isFlashing() {
		return flashing;
	}

	@Override
	public void move(Maze maze) {

		// Dead Mode 速度更快
		if (moveTick < (isDead() ? deadMaxMoveTick : maxMoveTick))
			moveTick++;

		if (moveTick == (isDead() ? deadMaxMoveTick : maxMoveTick)) {
			moveTick = 0;
			int[] nextDrection = Direction2D.directionToArray(facing);
			int nextRow = row + nextDrection[0];
			int nextCol = col + nextDrection[1];

			if (!maze.isWall(nextRow, nextCol)) {
				row = nextRow;
				col = nextCol;
			}
		}

		if (state == GhostState.Scared) {
			scaredTick++;

			// 自Scared Mode恢復正常
			if (scaredTick == maxScaredTick) {
				scaredTick = 0;
				flashing = false;
				state = GhostState.Normal;

			}

			// 更新閃爍狀態
			else if (scaredTick > maxScaredTick / 10 * 6 && scaredTick % (maxScaredTick / 30) == 0)
				flashing = !flashing;
		}

	}

}
