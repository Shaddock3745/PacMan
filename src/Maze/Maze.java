package Maze;

import Mobs.Ghost.ChasingType;

public final class Maze {
	
	/*
	 * 迷宮物件
	 */

	// 記錄每格內容
	public static enum BlockContent {
		blank, wall, dot, power
	}

	private final int cols, rows;
	private final BlockContent[][] mazeMap;
	private final int[] pacManDefaultPosition;
	private final int[][] ghostDefaultPositions;
	private final ChasingType[] ghostTypes;

	public Maze(BlockContent[][] mazeMap, int[] pacManDefaultPosition, int[][] ghostDefaultPositions,
			ChasingType[] ghostTypes) {
		this.rows = mazeMap.length;
		this.cols = mazeMap[0].length;
		this.mazeMap = mazeMap;
		this.pacManDefaultPosition = pacManDefaultPosition;
		this.ghostDefaultPositions = ghostDefaultPositions;
		this.ghostTypes = ghostTypes;
	}

	public final BlockContent[][] getMazeMap() {
		return mazeMap;
	}

	public final BlockContent getBlockContent(int row, int col) {
		return mazeMap[row][col];
	}

	public final int getGhostsNumber() {
		return ghostDefaultPositions.length;
	}

	public final int[] getPacManDefaultPosition() {
		return pacManDefaultPosition;
	}

	public final int[] getGhostDefaultPosition(int i) {
		return ghostDefaultPositions[i];
	}

	public final ChasingType getGhostType(int i) {
		return ghostTypes[i];
	}

	public int getRows() {
		return rows;
	}

	public int getCols() {
		return cols;
	}

	public boolean isWall(int row, int col) {
		return mazeMap[row][col] == BlockContent.wall;
	}

	public boolean isBlank(int row, int col) {
		return mazeMap[row][col] == BlockContent.blank;
	}

	public boolean isDot(int row, int col) {
		return mazeMap[row][col] == BlockContent.dot;
	}

	public boolean isPower(int row, int col) {
		return mazeMap[row][col] == BlockContent.power;
	}

	public boolean dotEaten(int row, int col) {
		if (isDot(row, col)) {
			mazeMap[row][col] = BlockContent.blank;
			return true;
		}
		return false;
	}

	public boolean powerEaten(int row, int col) {
		if (isPower(row, col)) {
			mazeMap[row][col] = BlockContent.blank;
			return true;
		}
		return false;
	}

	// 經典迷宮配置（參考 UC Berkeley CS188 的 originalClassic 版圖）
	public static Maze createClassicMaze() {
		int[] classicPacMan = { 25, 14 };
		int[][] classicGhosts = { { 13, 10 }, { 13, 13 }, { 13, 14 }, { 13, 17 } };
		ChasingType[] ghostTypes = { ChasingType.Blinky, ChasingType.Pinky, ChasingType.Inky, ChasingType.Clyde };
		BlockContent[][] classicMap = {
				{ BlockContent.wall, BlockContent.wall, BlockContent.wall, BlockContent.wall, BlockContent.wall,
						BlockContent.wall, BlockContent.wall, BlockContent.wall, BlockContent.wall, BlockContent.wall,
						BlockContent.wall, BlockContent.wall, BlockContent.wall, BlockContent.wall, BlockContent.wall,
						BlockContent.wall, BlockContent.wall, BlockContent.wall, BlockContent.wall, BlockContent.wall,
						BlockContent.wall, BlockContent.wall, BlockContent.wall, BlockContent.wall, BlockContent.wall,
						BlockContent.wall, BlockContent.wall, BlockContent.wall },
				{ BlockContent.wall, BlockContent.dot, BlockContent.dot, BlockContent.dot, BlockContent.dot,
						BlockContent.dot, BlockContent.dot, BlockContent.dot, BlockContent.dot, BlockContent.dot,
						BlockContent.dot, BlockContent.dot, BlockContent.dot, BlockContent.wall, BlockContent.wall,
						BlockContent.dot, BlockContent.dot, BlockContent.dot, BlockContent.dot, BlockContent.dot,
						BlockContent.dot, BlockContent.dot, BlockContent.dot, BlockContent.dot, BlockContent.dot,
						BlockContent.dot, BlockContent.dot, BlockContent.wall },
				{ BlockContent.wall, BlockContent.dot, BlockContent.wall, BlockContent.wall, BlockContent.wall,
						BlockContent.wall, BlockContent.dot, BlockContent.wall, BlockContent.wall, BlockContent.wall,
						BlockContent.wall, BlockContent.wall, BlockContent.dot, BlockContent.wall, BlockContent.wall,
						BlockContent.dot, BlockContent.wall, BlockContent.wall, BlockContent.wall, BlockContent.wall,
						BlockContent.wall, BlockContent.dot, BlockContent.wall, BlockContent.wall, BlockContent.wall,
						BlockContent.wall, BlockContent.dot, BlockContent.wall },
				{ BlockContent.wall, BlockContent.power, BlockContent.wall, BlockContent.wall, BlockContent.wall,
						BlockContent.wall, BlockContent.dot, BlockContent.wall, BlockContent.wall, BlockContent.wall,
						BlockContent.wall, BlockContent.wall, BlockContent.dot, BlockContent.wall, BlockContent.wall,
						BlockContent.dot, BlockContent.wall, BlockContent.wall, BlockContent.wall, BlockContent.wall,
						BlockContent.wall, BlockContent.dot, BlockContent.wall, BlockContent.wall, BlockContent.wall,
						BlockContent.wall, BlockContent.power, BlockContent.wall },
				{ BlockContent.wall, BlockContent.dot, BlockContent.wall, BlockContent.wall, BlockContent.wall,
						BlockContent.wall, BlockContent.dot, BlockContent.wall, BlockContent.wall, BlockContent.wall,
						BlockContent.wall, BlockContent.wall, BlockContent.dot, BlockContent.wall, BlockContent.wall,
						BlockContent.dot, BlockContent.wall, BlockContent.wall, BlockContent.wall, BlockContent.wall,
						BlockContent.wall, BlockContent.dot, BlockContent.wall, BlockContent.wall, BlockContent.wall,
						BlockContent.wall, BlockContent.dot, BlockContent.wall },
				{ BlockContent.wall, BlockContent.dot, BlockContent.dot, BlockContent.dot, BlockContent.dot,
						BlockContent.dot, BlockContent.dot, BlockContent.dot, BlockContent.dot, BlockContent.dot,
						BlockContent.dot, BlockContent.dot, BlockContent.dot, BlockContent.dot, BlockContent.dot,
						BlockContent.dot, BlockContent.dot, BlockContent.dot, BlockContent.dot, BlockContent.dot,
						BlockContent.dot, BlockContent.dot, BlockContent.dot, BlockContent.dot, BlockContent.dot,
						BlockContent.dot, BlockContent.dot, BlockContent.wall },
				{ BlockContent.wall, BlockContent.dot, BlockContent.wall, BlockContent.wall, BlockContent.wall,
						BlockContent.wall, BlockContent.dot, BlockContent.wall, BlockContent.wall, BlockContent.dot,
						BlockContent.wall, BlockContent.wall, BlockContent.wall, BlockContent.wall, BlockContent.wall,
						BlockContent.wall, BlockContent.wall, BlockContent.wall, BlockContent.dot, BlockContent.wall,
						BlockContent.wall, BlockContent.dot, BlockContent.wall, BlockContent.wall, BlockContent.wall,
						BlockContent.wall, BlockContent.dot, BlockContent.wall },
				{ BlockContent.wall, BlockContent.dot, BlockContent.wall, BlockContent.wall, BlockContent.wall,
						BlockContent.wall, BlockContent.dot, BlockContent.wall, BlockContent.wall, BlockContent.dot,
						BlockContent.wall, BlockContent.wall, BlockContent.wall, BlockContent.wall, BlockContent.wall,
						BlockContent.wall, BlockContent.wall, BlockContent.wall, BlockContent.dot, BlockContent.wall,
						BlockContent.wall, BlockContent.dot, BlockContent.wall, BlockContent.wall, BlockContent.wall,
						BlockContent.wall, BlockContent.dot, BlockContent.wall },
				{ BlockContent.wall, BlockContent.dot, BlockContent.dot, BlockContent.dot, BlockContent.dot,
						BlockContent.dot, BlockContent.dot, BlockContent.wall, BlockContent.wall, BlockContent.dot,
						BlockContent.dot, BlockContent.dot, BlockContent.dot, BlockContent.wall, BlockContent.wall,
						BlockContent.dot, BlockContent.dot, BlockContent.dot, BlockContent.dot, BlockContent.wall,
						BlockContent.wall, BlockContent.dot, BlockContent.dot, BlockContent.dot, BlockContent.dot,
						BlockContent.dot, BlockContent.dot, BlockContent.wall },
				{ BlockContent.wall, BlockContent.wall, BlockContent.wall, BlockContent.wall, BlockContent.wall,
						BlockContent.wall, BlockContent.dot, BlockContent.wall, BlockContent.wall, BlockContent.wall,
						BlockContent.wall, BlockContent.wall, BlockContent.blank, BlockContent.wall, BlockContent.wall,
						BlockContent.blank, BlockContent.wall, BlockContent.wall, BlockContent.wall, BlockContent.wall,
						BlockContent.wall, BlockContent.dot, BlockContent.wall, BlockContent.wall, BlockContent.wall,
						BlockContent.wall, BlockContent.wall, BlockContent.wall },
				{ BlockContent.wall, BlockContent.wall, BlockContent.wall, BlockContent.wall, BlockContent.wall,
						BlockContent.wall, BlockContent.dot, BlockContent.wall, BlockContent.wall, BlockContent.wall,
						BlockContent.wall, BlockContent.wall, BlockContent.blank, BlockContent.wall, BlockContent.wall,
						BlockContent.blank, BlockContent.wall, BlockContent.wall, BlockContent.wall, BlockContent.wall,
						BlockContent.wall, BlockContent.dot, BlockContent.wall, BlockContent.wall, BlockContent.wall,
						BlockContent.wall, BlockContent.wall, BlockContent.wall },
				{ BlockContent.wall, BlockContent.wall, BlockContent.wall, BlockContent.wall, BlockContent.wall,
						BlockContent.wall, BlockContent.dot, BlockContent.wall, BlockContent.blank, BlockContent.blank,
						BlockContent.blank, BlockContent.blank, BlockContent.blank, BlockContent.blank,
						BlockContent.blank, BlockContent.blank, BlockContent.blank, BlockContent.blank,
						BlockContent.blank, BlockContent.blank, BlockContent.wall, BlockContent.dot, BlockContent.wall,
						BlockContent.wall, BlockContent.wall, BlockContent.wall, BlockContent.wall, BlockContent.wall },
				{ BlockContent.wall, BlockContent.wall, BlockContent.wall, BlockContent.wall, BlockContent.wall,
						BlockContent.wall, BlockContent.dot, BlockContent.wall, BlockContent.blank, BlockContent.wall,
						BlockContent.wall, BlockContent.wall, BlockContent.wall, BlockContent.blank, BlockContent.blank,
						BlockContent.wall, BlockContent.wall, BlockContent.wall, BlockContent.wall, BlockContent.blank,
						BlockContent.wall, BlockContent.dot, BlockContent.wall, BlockContent.wall, BlockContent.wall,
						BlockContent.wall, BlockContent.wall, BlockContent.wall },
				{ BlockContent.wall, BlockContent.blank, BlockContent.blank, BlockContent.blank, BlockContent.blank,
						BlockContent.blank, BlockContent.dot, BlockContent.blank, BlockContent.blank, BlockContent.wall,
						BlockContent.blank, BlockContent.blank, BlockContent.blank, BlockContent.blank,
						BlockContent.blank, BlockContent.blank, BlockContent.blank, BlockContent.blank,
						BlockContent.wall, BlockContent.blank, BlockContent.blank, BlockContent.dot, BlockContent.blank,
						BlockContent.blank, BlockContent.blank, BlockContent.blank, BlockContent.blank,
						BlockContent.wall },
				{ BlockContent.wall, BlockContent.wall, BlockContent.wall, BlockContent.wall, BlockContent.wall,
						BlockContent.wall, BlockContent.dot, BlockContent.wall, BlockContent.blank, BlockContent.wall,
						BlockContent.wall, BlockContent.wall, BlockContent.wall, BlockContent.wall, BlockContent.wall,
						BlockContent.wall, BlockContent.wall, BlockContent.wall, BlockContent.wall, BlockContent.blank,
						BlockContent.wall, BlockContent.dot, BlockContent.wall, BlockContent.wall, BlockContent.wall,
						BlockContent.wall, BlockContent.wall, BlockContent.wall },
				{ BlockContent.wall, BlockContent.wall, BlockContent.wall, BlockContent.wall, BlockContent.wall,
						BlockContent.wall, BlockContent.dot, BlockContent.wall, BlockContent.blank, BlockContent.blank,
						BlockContent.blank, BlockContent.blank, BlockContent.blank, BlockContent.blank,
						BlockContent.blank, BlockContent.blank, BlockContent.blank, BlockContent.blank,
						BlockContent.blank, BlockContent.blank, BlockContent.wall, BlockContent.dot, BlockContent.wall,
						BlockContent.wall, BlockContent.wall, BlockContent.wall, BlockContent.wall, BlockContent.wall },
				{ BlockContent.wall, BlockContent.wall, BlockContent.wall, BlockContent.wall, BlockContent.wall,
						BlockContent.wall, BlockContent.dot, BlockContent.wall, BlockContent.blank, BlockContent.wall,
						BlockContent.wall, BlockContent.wall, BlockContent.wall, BlockContent.wall, BlockContent.wall,
						BlockContent.wall, BlockContent.wall, BlockContent.wall, BlockContent.wall, BlockContent.blank,
						BlockContent.wall, BlockContent.dot, BlockContent.wall, BlockContent.wall, BlockContent.wall,
						BlockContent.wall, BlockContent.wall, BlockContent.wall },
				{ BlockContent.wall, BlockContent.dot, BlockContent.dot, BlockContent.dot, BlockContent.dot,
						BlockContent.dot, BlockContent.dot, BlockContent.dot, BlockContent.dot, BlockContent.dot,
						BlockContent.dot, BlockContent.dot, BlockContent.dot, BlockContent.wall, BlockContent.wall,
						BlockContent.dot, BlockContent.dot, BlockContent.dot, BlockContent.dot, BlockContent.dot,
						BlockContent.dot, BlockContent.dot, BlockContent.dot, BlockContent.dot, BlockContent.dot,
						BlockContent.dot, BlockContent.dot, BlockContent.wall },
				{ BlockContent.wall, BlockContent.dot, BlockContent.wall, BlockContent.wall, BlockContent.wall,
						BlockContent.wall, BlockContent.dot, BlockContent.wall, BlockContent.wall, BlockContent.wall,
						BlockContent.wall, BlockContent.wall, BlockContent.dot, BlockContent.wall, BlockContent.wall,
						BlockContent.dot, BlockContent.wall, BlockContent.wall, BlockContent.wall, BlockContent.wall,
						BlockContent.wall, BlockContent.dot, BlockContent.wall, BlockContent.wall, BlockContent.wall,
						BlockContent.wall, BlockContent.dot, BlockContent.wall },
				{ BlockContent.wall, BlockContent.dot, BlockContent.wall, BlockContent.wall, BlockContent.wall,
						BlockContent.wall, BlockContent.dot, BlockContent.wall, BlockContent.wall, BlockContent.wall,
						BlockContent.wall, BlockContent.wall, BlockContent.dot, BlockContent.wall, BlockContent.wall,
						BlockContent.dot, BlockContent.wall, BlockContent.wall, BlockContent.wall, BlockContent.wall,
						BlockContent.wall, BlockContent.dot, BlockContent.wall, BlockContent.wall, BlockContent.wall,
						BlockContent.wall, BlockContent.dot, BlockContent.wall },
				{ BlockContent.wall, BlockContent.power, BlockContent.dot, BlockContent.dot, BlockContent.wall,
						BlockContent.wall, BlockContent.dot, BlockContent.dot, BlockContent.dot, BlockContent.dot,
						BlockContent.dot, BlockContent.dot, BlockContent.dot, BlockContent.blank, BlockContent.blank,
						BlockContent.dot, BlockContent.dot, BlockContent.dot, BlockContent.dot, BlockContent.dot,
						BlockContent.dot, BlockContent.dot, BlockContent.wall, BlockContent.wall, BlockContent.dot,
						BlockContent.dot, BlockContent.power, BlockContent.wall },
				{ BlockContent.wall, BlockContent.wall, BlockContent.wall, BlockContent.dot, BlockContent.wall,
						BlockContent.wall, BlockContent.dot, BlockContent.wall, BlockContent.wall, BlockContent.dot,
						BlockContent.wall, BlockContent.wall, BlockContent.wall, BlockContent.wall, BlockContent.wall,
						BlockContent.wall, BlockContent.wall, BlockContent.wall, BlockContent.dot, BlockContent.wall,
						BlockContent.wall, BlockContent.dot, BlockContent.wall, BlockContent.wall, BlockContent.dot,
						BlockContent.wall, BlockContent.wall, BlockContent.wall },
				{ BlockContent.wall, BlockContent.wall, BlockContent.wall, BlockContent.dot, BlockContent.wall,
						BlockContent.wall, BlockContent.dot, BlockContent.wall, BlockContent.wall, BlockContent.dot,
						BlockContent.wall, BlockContent.wall, BlockContent.wall, BlockContent.wall, BlockContent.wall,
						BlockContent.wall, BlockContent.wall, BlockContent.wall, BlockContent.dot, BlockContent.wall,
						BlockContent.wall, BlockContent.dot, BlockContent.wall, BlockContent.wall, BlockContent.dot,
						BlockContent.wall, BlockContent.wall, BlockContent.wall },
				{ BlockContent.wall, BlockContent.dot, BlockContent.dot, BlockContent.dot, BlockContent.dot,
						BlockContent.dot, BlockContent.dot, BlockContent.wall, BlockContent.wall, BlockContent.dot,
						BlockContent.dot, BlockContent.dot, BlockContent.dot, BlockContent.wall, BlockContent.wall,
						BlockContent.dot, BlockContent.dot, BlockContent.dot, BlockContent.dot, BlockContent.wall,
						BlockContent.wall, BlockContent.dot, BlockContent.dot, BlockContent.dot, BlockContent.dot,
						BlockContent.dot, BlockContent.dot, BlockContent.wall },
				{ BlockContent.wall, BlockContent.dot, BlockContent.wall, BlockContent.wall, BlockContent.wall,
						BlockContent.wall, BlockContent.wall, BlockContent.wall, BlockContent.wall, BlockContent.wall,
						BlockContent.wall, BlockContent.wall, BlockContent.dot, BlockContent.wall, BlockContent.wall,
						BlockContent.dot, BlockContent.wall, BlockContent.wall, BlockContent.wall, BlockContent.wall,
						BlockContent.wall, BlockContent.wall, BlockContent.wall, BlockContent.wall, BlockContent.wall,
						BlockContent.wall, BlockContent.dot, BlockContent.wall },
				{ BlockContent.wall, BlockContent.dot, BlockContent.dot, BlockContent.dot, BlockContent.dot,
						BlockContent.dot, BlockContent.dot, BlockContent.dot, BlockContent.dot, BlockContent.dot,
						BlockContent.dot, BlockContent.dot, BlockContent.dot, BlockContent.dot, BlockContent.blank,
						BlockContent.dot, BlockContent.dot, BlockContent.dot, BlockContent.dot, BlockContent.dot,
						BlockContent.dot, BlockContent.dot, BlockContent.dot, BlockContent.dot, BlockContent.dot,
						BlockContent.dot, BlockContent.dot, BlockContent.wall },
				{ BlockContent.wall, BlockContent.wall, BlockContent.wall, BlockContent.wall, BlockContent.wall,
						BlockContent.wall, BlockContent.wall, BlockContent.wall, BlockContent.wall, BlockContent.wall,
						BlockContent.wall, BlockContent.wall, BlockContent.wall, BlockContent.wall, BlockContent.wall,
						BlockContent.wall, BlockContent.wall, BlockContent.wall, BlockContent.wall, BlockContent.wall,
						BlockContent.wall, BlockContent.wall, BlockContent.wall, BlockContent.wall, BlockContent.wall,
						BlockContent.wall, BlockContent.wall, BlockContent.wall } };
		return new Maze(classicMap, classicPacMan, classicGhosts, ghostTypes);
	}

}
