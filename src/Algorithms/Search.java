package Algorithms;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

import Algorithms.Direction2D.Direction;
import Maze.Maze;

public final class Search {

	/*
	 * 搜尋演算法：DFS、BFS、IDDFS、UCS、A*
	 * UCS 與 A* 中，經過大力丸的格子 Cost 較高，讓幽靈傾向避開大力丸
	 */

	// 搜尋方法種類
	public static enum SearchingType {
		DFS, BFS, IDDFS, UCS, Astar
	}

	// 用於記錄搜尋過程節點
	private static class SearchNode implements Comparable<SearchNode> {
		int row, col, cost, priority;
		
		// 用於回推路徑
		SearchNode last;
		Direction direction;

		SearchNode(int row, int col, SearchNode last, Direction direction) {
			this.row = row;
			this.col = col;
			this.last = last;
			this.direction = direction;
		}

		SearchNode(int row, int col, SearchNode last, Direction direction, int cost, int priority) {
			this(row, col, last, direction);
			this.cost = cost;
			this.priority = priority;
		}

		// 用於Priority Queue排序
		@Override
		public int compareTo(SearchNode n) {
			return Integer.compare(this.priority, n.priority);
		}
	}

	// 輸入一格(可超出迷宮範圍)，BFS取得最近的有效格
	public static int[] closestVaildBlock(Maze maze, int startRow, int startCol) {

		// 修正在迷宮範圍內
		startRow = Math.max(0, Math.min(startRow, maze.getRows() - 1));
		startCol = Math.max(0, Math.min(startCol, maze.getCols() - 1));

		boolean[][] visited = new boolean[maze.getRows()][maze.getCols()];

		Queue<int[]> queue = new ArrayDeque<int[]>();
		queue.add(new int[] { startRow, startCol });

		// BFS
		while (!queue.isEmpty()) {
			int[] temp = queue.poll();

			// 該格有效則回傳
			if (!maze.isWall(temp[0], temp[1]))
				return temp;

			visited[temp[0]][temp[1]] = true;

			// 該格無效，在迷宮範圍內尋找鄰近有效格
			for (Direction direction : Direction.values()) {
				int[] tempDrection = Direction2D.directionToArray(direction);
				int nextRow = temp[0] + tempDrection[0];
				int nextCol = temp[1] + tempDrection[1];

				if (nextRow >= 0 && nextRow < maze.getRows() && nextCol >= 0 && nextCol < maze.getCols())
					queue.add(new int[] { nextRow, nextCol });
			}
		}

		return null;
	}

	// 取得遠離玩家的方向
	public static Direction getEscapeDirection(Maze maze, int curRow, int curCol, int pacManRow, int pacManCol) {
		int[][] distanceMap = distaceMap(maze, pacManRow, pacManCol);
		Direction result = null;
		int maxDistance = Integer.MIN_VALUE;

		// 根據距離表，回傳距離玩家最遠的方向
		for (Direction direction : Direction.values()) {
			int[] tempDrection = Direction2D.directionToArray(direction);
			int nextRow = curRow + tempDrection[0];
			int nextCol = curCol + tempDrection[1];

			if (maxDistance < distanceMap[nextRow][nextCol] && !maze.isWall(nextRow, nextCol)) {
				result = direction;
				maxDistance = distanceMap[nextRow][nextCol];
			}
		}

		return result;
	}

	// BFS建立距離表，表示迷宮內每格與玩家最短距離，幽靈處於Scared Mode時用於逃離玩家
	private static int[][] distaceMap(Maze maze, int pacManRow, int pacManCol) {
		int[][] result = new int[maze.getRows()][maze.getCols()];

		for (int i = 0; i < maze.getRows(); i++)
			for (int j = 0; j < maze.getCols(); j++)
				result[i][j] = Integer.MAX_VALUE;

		Queue<int[]> queue = new ArrayDeque<int[]>();

		// 以玩家位置為起點
		queue.add(new int[] { pacManRow, pacManCol });
		int distance = 0;

		// BFS
		while (!queue.isEmpty()) {
			int size = queue.size();

			for (int i = 0; i < size; i++) {
				int[] temp = queue.poll();
				result[temp[0]][temp[1]] = distance;

				for (Direction direction : Direction.values()) {
					int[] tempDrection = Direction2D.directionToArray(direction);
					int nextRow = temp[0] + tempDrection[0];
					int nextCol = temp[1] + tempDrection[1];

					if (!maze.isWall(nextRow, nextCol) && result[nextRow][nextCol] > result[temp[0]][temp[1]])
						queue.add(new int[] { nextRow, nextCol });
				}

			}

			distance++;
		}

		return result;

	}

	// 根據輸入的搜尋方法，回傳路徑
	public static List<Direction> searchForPath(SearchingType type, Maze maze, int startRow, int startCol,
			int targetRow, int targetCol) {

		switch (type) {
		case Astar:
			return Astar(maze, startRow, startCol, targetRow, targetCol);
		case UCS:
			return UCS(maze, startRow, startCol, targetRow, targetCol);
		case IDDFS:
			return IDDFS(maze, startRow, startCol, targetRow, targetCol);
		case BFS:
			return BFS(maze, startRow, startCol, targetRow, targetCol);
		case DFS:
			return DFS(maze, startRow, startCol, targetRow, targetCol);
		default:
			return Collections.emptyList();
		}
	}

	// DFS
	private static List<Direction> DFS(Maze maze, int startRow, int startCol, int targetRow, int targetCol) {
		boolean[][] visited = new boolean[maze.getRows()][maze.getCols()];
		visited[startRow][startCol] = true;

		Deque<SearchNode> stack = new ArrayDeque<SearchNode>();
		stack.push(new SearchNode(startRow, startCol, null, null));

		int curRow, curCol;

		while (!stack.isEmpty()) {
			SearchNode node = stack.pop();
			curRow = node.row;
			curCol = node.col;

			if (curRow == targetRow && curCol == targetCol) {
				List<Direction> result = new ArrayList<Direction>();

				while (node.last != null) {
					result.add(node.direction);
					node = node.last;
				}

				Collections.reverse(result);
				return result;
			}

			for (Direction direction : Direction.values()) {
				int[] tempDrection = Direction2D.directionToArray(direction);
				int nextRow = curRow + tempDrection[0];
				int nextCol = curCol + tempDrection[1];

				if (!maze.isWall(nextRow, nextCol) && !visited[nextRow][nextCol]) {
					visited[nextRow][nextCol] = true;
					stack.push(new SearchNode(nextRow, nextCol, node, direction));
				}
			}
		}

		return Collections.emptyList();
	}

	// BFS
	private static List<Direction> BFS(Maze maze, int startRow, int startCol, int targetRow, int targetCol) {
		boolean[][] visited = new boolean[maze.getRows()][maze.getCols()];
		visited[startRow][startCol] = true;

		Queue<SearchNode> queue = new ArrayDeque<SearchNode>();
		queue.add(new SearchNode(startRow, startCol, null, null));

		int curRow, curCol;

		while (!queue.isEmpty()) {
			SearchNode node = queue.poll();
			curRow = node.row;
			curCol = node.col;

			if (curRow == targetRow && curCol == targetCol) {
				List<Direction> result = new ArrayList<Direction>();

				while (node.last != null) {
					result.add(node.direction);
					node = node.last;
				}

				Collections.reverse(result);
				return result;
			}

			for (Direction direction : Direction.values()) {
				int[] tempDrection = Direction2D.directionToArray(direction);
				int nextRow = curRow + tempDrection[0];
				int nextCol = curCol + tempDrection[1];

				if (!maze.isWall(nextRow, nextCol) && !visited[nextRow][nextCol]) {
					visited[nextRow][nextCol] = true;
					queue.add(new SearchNode(nextRow, nextCol, node, direction));
				}
			}
		}

		return Collections.emptyList();
	}

	// IDDFS
	private static List<Direction> IDDFS(Maze maze, int startRow, int startCol, int targetRow, int targetCol) {

		for (int maxDepth = 1;; maxDepth++) {
			boolean[][] visited = new boolean[maze.getRows()][maze.getCols()];
			List<Direction> path = new ArrayList<Direction>();

			if (DLS(maze, startRow, startCol, targetRow, targetCol, 0, maxDepth, visited, path))
				return path;
		}
	}

	// 有深度限制的DFS，供IDFFS使用
	private static boolean DLS(Maze maze, int curRow, int curCol, int targetRow, int targetCol, int curDepth,
			int maxDepth, boolean[][] visited, List<Direction> path) {
		if (curRow == targetRow && curCol == targetCol)
			return true;

		if (curDepth == maxDepth)
			return false;

		visited[curRow][curCol] = true;

		for (Direction direction : Direction.values()) {
			int[] tempDrection = Direction2D.directionToArray(direction);
			int nextRow = curRow + tempDrection[0];
			int nextCol = curCol + tempDrection[1];

			if (!maze.isWall(nextRow, nextCol) && !visited[nextRow][nextCol]) {
				path.add(direction);

				if (DLS(maze, nextRow, nextCol, targetRow, targetCol, curDepth + 1, maxDepth, visited, path))
					return true;

				path.removeLast();
			}
		}

		visited[curRow][curCol] = false;
		return false;
	}

	// UCS
	private static List<Direction> UCS(Maze maze, int startRow, int startCol, int targetRow, int targetCol) {
		int[][] Costs = new int[maze.getRows()][maze.getCols()];

		for (int i = 0; i < maze.getRows(); i++)
			for (int j = 0; j < maze.getCols(); j++)
				Costs[i][j] = Integer.MAX_VALUE;

		Costs[startRow][startCol] = 0;

		PriorityQueue<SearchNode> queue = new PriorityQueue<SearchNode>();
		queue.add(new SearchNode(startRow, startCol, null, null, 0, 0));

		int curRow, curCol;

		while (!queue.isEmpty()) {
			SearchNode node = queue.poll();
			curRow = node.row;
			curCol = node.col;

			if (node.cost > Costs[curRow][curCol])
				continue;

			if (curRow == targetRow && curCol == targetCol) {
				List<Direction> result = new ArrayList<Direction>();

				while (node.last != null) {
					result.add(node.direction);
					node = node.last;
				}

				Collections.reverse(result);
				return result;
			}

			for (Direction direction : Direction.values()) {
				int[] tempDrection = Direction2D.directionToArray(direction);
				int nextRow = curRow + tempDrection[0];
				int nextCol = curCol + tempDrection[1];
				int nextCost = node.cost + (maze.isPower(nextRow, nextCol) ? 10 : 1);

				if (!maze.isWall(nextRow, nextCol) && nextCost < Costs[nextRow][nextCol]) {
					Costs[nextRow][nextCol] = nextCost;
					queue.add(new SearchNode(nextRow, nextCol, node, direction, nextCost, nextCost));
				}
			}
		}

		return Collections.emptyList();
	}

	// A*
	private static List<Direction> Astar(Maze maze, int startRow, int startCol, int targetRow, int targetCol) {
		int[][] Costs = new int[maze.getRows()][maze.getCols()];

		for (int i = 0; i < maze.getRows(); i++)
			for (int j = 0; j < maze.getCols(); j++)
				Costs[i][j] = Integer.MAX_VALUE;

		Costs[startRow][startCol] = 0;

		PriorityQueue<SearchNode> queue = new PriorityQueue<SearchNode>();
		queue.add(new SearchNode(startRow, startCol, null, null, 0, 0));

		int curRow, curCol;

		while (!queue.isEmpty()) {
			SearchNode node = queue.poll();
			curRow = node.row;
			curCol = node.col;

			if (node.cost > Costs[curRow][curCol])
				continue;

			if (curRow == targetRow && curCol == targetCol) {
				List<Direction> result = new ArrayList<Direction>();

				while (node.last != null) {
					result.add(node.direction);
					node = node.last;
				}

				Collections.reverse(result);
				return result;
			}

			for (Direction direction : Direction.values()) {
				int[] tempDrection = Direction2D.directionToArray(direction);
				int nextRow = curRow + tempDrection[0];
				int nextCol = curCol + tempDrection[1];
				int nextCost = node.cost + (maze.isPower(nextRow, nextCol) ? 10 : 1);

				if (!maze.isWall(nextRow, nextCol) && nextCost < Costs[nextRow][nextCol]) {
					Costs[nextRow][nextCol] = nextCost;
					queue.add(new SearchNode(nextRow, nextCol, node, direction, nextCost,
							nextCost + manhattanDistance(nextRow, nextCol, targetRow, targetCol)));
				}
			}
		}

		return Collections.emptyList();
	}

	// 計算曼哈頓距離
	public static int manhattanDistance(int startRow, int startCol, int targetRow, int targetCol) {
		return Math.abs(targetRow - startRow) + Math.abs(targetCol - startCol);
	}
}
