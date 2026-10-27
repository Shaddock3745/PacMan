package Algorithms;

public final class Direction2D {
	
	/*
	 * 僅用於方向操作，無實作物件
	 */

	// 常數陣列用於取值
	public static final int[][] directionArray = { { 0, 1 }, { -1, 0 }, { 0, -1 }, { 1, 0 } };

	// 列舉提升易讀性
	public static enum Direction {
		right, up, left, down;
	}

	// 取得實際值
	public static int[] directionToArray(Direction direction) {
		return directionArray[direction.ordinal()];
	}

}
