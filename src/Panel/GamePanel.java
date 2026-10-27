package Panel;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JPanel;

import Algorithms.Direction2D;
import Algorithms.Direction2D.Direction;
import Algorithms.Search;
import Algorithms.Search.SearchingType;
import Maze.Maze;
import Mobs.Ghost;
import Mobs.Ghost.ChasingType;
import Mobs.PacMan;

public final class GamePanel extends JPanel implements KeyListener {

	/**
	 * 遊戲畫面物件
	 */
	private static final long serialVersionUID = 1L;
	private final Object stateLock = new Object();
	private static final double nsPerFrame = 1_000_000_000.0 / MainForm.FPS;
	private ExecutorService threadPool;
	private volatile boolean gameRunning = false, isPausing = false;
	private final Maze maze;
	private final int blockWidth;
	private final int paintingStartX, paintingStartY;
	private PacMan pacMan;
	private Ghost[] ghosts;
	private final boolean debugMode;
	private final SearchingType searchingType;

	public GamePanel(Maze maze, SearchingType searchingType, boolean debugMode) {
		addKeyListener(this);
		this.debugMode = debugMode;
		this.searchingType = searchingType;
		threadPool = Executors.newFixedThreadPool(2);
		this.maze = maze;
		blockWidth = Math.min(MainForm.formHeight / maze.getRows(), MainForm.formWidth / maze.getCols());
		paintingStartX = (MainForm.formWidth - maze.getCols() * blockWidth) >> 1;
		paintingStartY = (MainForm.formHeight - maze.getRows() * blockWidth) >> 1;
		pacMan = new PacMan(maze.getPacManDefaultPosition()[0], maze.getPacManDefaultPosition()[1]);
		ghosts = new Ghost[maze.getGhostsNumber()];

		for (int i = 0; i < ghosts.length; i++)
			ghosts[i] = new Ghost(maze.getGhostDefaultPosition(i), maze.getGhostType(i), searchingType);
	}

	// 計算用執行緒
	private final Runnable mainRunnable = new Runnable() {

		@Override
		public void run() {
			long lastTime = System.nanoTime();
			double delta = 0;

			while (gameRunning) {
				if (!isPausing) {

					long now = System.nanoTime();
					delta += (now - lastTime) / nsPerFrame;
					lastTime = now;

					if (delta >= 1) {

						synchronized (stateLock) {
							pacMan.move(maze);
							eatenCheck();
							ghostSearch(searchingType);

							for (Ghost ghost : ghosts) {
								ghost.move(maze);
							}

							ghostsCheck();
						}

						delta--;
					}
				} else
					lastTime = System.nanoTime();

				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		}
	};

	// 繪圖用執行緒
	private final Runnable paintRunnable = new Runnable() {

		@Override
		public void run() {
			while (gameRunning) {
				repaint();
				try {
					Thread.sleep(5);
				} catch (Exception e) {
				}
			}
		}
	};

	public void gameStart() {
		setLocale(Locale.US);
		getInputContext().selectInputMethod(Locale.US);
		getInputContext().setCompositionEnabled(false);
		gameRunning = true;
		threadPool.execute(mainRunnable);
		threadPool.execute(paintRunnable);
		requestFocus();
	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	@Override
	public void keyPressed(KeyEvent e) {
		synchronized (stateLock) {
			switch (e.getKeyCode()) {
			case KeyEvent.VK_UP:
				pacMan.setDirection(Direction.up);
				pacMan.setMoving(true);
				break;
			case KeyEvent.VK_DOWN:
				pacMan.setDirection(Direction.down);
				pacMan.setMoving(true);
				break;
			case KeyEvent.VK_LEFT:
				pacMan.setDirection(Direction.left);
				pacMan.setMoving(true);
				break;
			case KeyEvent.VK_RIGHT:
				pacMan.setDirection(Direction.right);
				pacMan.setMoving(true);
				break;
			case KeyEvent.VK_ESCAPE:
				isPausing = !isPausing;
				break;
			}
		}

	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	private void eatenCheck() {
		maze.dotEaten(pacMan.getRow(), pacMan.getCol());

		// 吃到大力丸則使所有幽靈進入Scared Mode
		if (maze.powerEaten(pacMan.getRow(), pacMan.getCol()))
			for (Ghost ghost : ghosts)
				ghost.beenScared();
	}

	// 幽靈位置判定
	private void ghostsCheck() {
		for (int i = 0; i < ghosts.length; i++) {
			Ghost ghost = ghosts[i];

			// Dead Mode的幽靈若返回初始位置，恢復正常
			if (ghost.isDead()) {
				int[] temp = maze.getGhostDefaultPosition(i);

				if (ghost.getRow() == temp[0] && ghost.getCol() == temp[1])
					ghost.recover();

			}
			// 與玩家碰撞
			else if (ghost.getRow() == pacMan.getRow() && ghost.getCol() == pacMan.getCol())

				// 若處於Scared Mode被吃，否則遊戲結束
				if (ghost.isScared())
					ghost.beenEaten();
				else
					gameOver();
		}

	}

	// 幽靈搜尋，根據自身狀態與追擊方式，有不同「瞄準點」(瞄準點會修正為有效格)
	private void ghostSearch(SearchingType type) {
		if (debugMode)
			System.out.println("PacMan at: {" + pacMan.getRow() + ", " + pacMan.getCol() + "}, facing: "
					+ pacMan.getDirection().toString());

		for (int i = 0; i < ghosts.length; i++) {
			Ghost ghost = ghosts[i];
			int targetRow, targetCol;
			List<Direction> path;

			// 若處於Scared Mode，優先遠離玩家
			if (ghost.isScared())
				ghost.setDirection(Search.getEscapeDirection(maze, ghost.getRow(), ghost.getCol(), pacMan.getRow(),
						pacMan.getCol()));

			// 若處於Dead Mode，瞄準初始位置
			else if (ghost.isDead()) {
				int[] temp = maze.getGhostDefaultPosition(i);
				targetRow = temp[0];
				targetCol = temp[1];
				path = Search.searchForPath(type, maze, ghost.getRow(), ghost.getCol(), targetRow, targetCol);

				if (!path.isEmpty())
					ghost.setDirection(path.getFirst());

			}

			// 根據追擊方式搜尋
			else {
				int[] tempDrection = Direction2D.directionToArray(pacMan.getDirection());
				int[] vaildTarget;

				switch (ghost.getChasingType()) {

				// 直接瞄準玩家
				case Blinky:
					targetRow = pacMan.getRow();
					targetCol = pacMan.getCol();
					break;

				// 瞄準玩家前方四格
				case Pinky:

					// 距離過近，直接瞄準玩家
					if (Search.manhattanDistance(ghost.getRow(), ghost.getCol(), pacMan.getRow(), pacMan.getCol()) < 4) {
						targetRow = pacMan.getRow();
						targetCol = pacMan.getCol();

					}

					// 根據玩家面對方向延伸四格
					else {
						vaildTarget = Search.closestVaildBlock(maze, pacMan.getRow() + tempDrection[0] * 4,
								pacMan.getCol() + tempDrection[1] * 4);

						targetRow = vaildTarget[0];
						targetCol = vaildTarget[1];
					}
					break;

				// 以玩家前方兩點為基準，瞄準Blinky位置的對稱點
				case Inky:

					Ghost blinky = null;

					for (Ghost g : ghosts)
						if (g.getChasingType() == ChasingType.Blinky) {
							blinky = g;
							break;
						}

					// 如果找不到Blinky，則直接瞄準玩家
					if (blinky == null) {
						targetRow = pacMan.getRow();
						targetCol = pacMan.getCol();

					} else {
						int basicRow = pacMan.getRow() + tempDrection[0] * 2,
								basicCol = pacMan.getCol() + tempDrection[1] * 2;

						vaildTarget = Search.closestVaildBlock(maze, basicRow - (blinky.getRow() - basicRow),
								basicCol - (blinky.getCol() - basicCol));

						targetRow = vaildTarget[0];
						targetCol = vaildTarget[1];
					}
					break;

				// 直接瞄準玩家，若距離小於8，則瞄準迷宮左下角
				case Clyde:
					if (Search.manhattanDistance(ghost.getRow(), ghost.getCol(), pacMan.getRow(), pacMan.getCol()) > 8) {
						targetRow = pacMan.getRow();
						targetCol = pacMan.getCol();

					} else {
						vaildTarget = Search.closestVaildBlock(maze, maze.getRows() - 1, 0);

						targetRow = vaildTarget[0];
						targetCol = vaildTarget[1];
					}
					break;
				default:
					targetRow = targetCol = 0;
				}

				path = Search.searchForPath(type, maze, ghost.getRow(), ghost.getCol(), targetRow, targetCol);

				if (debugMode) {
					System.out.println(ghost.getChasingType().toString() + " at: {" + ghost.getRow() + ", "
							+ ghost.getCol() + "}, target: {" + targetRow + ", " + targetCol + "}");
					System.out.print("Path:{");

					for (Direction direction : path)
						System.out.print(direction.toString() + ", ");

					System.out.println("}");
				}

				// 根據搜尋到的路徑改變方向
				if (!path.isEmpty())
					ghost.setDirection(path.getFirst());
			}

		}

		if (debugMode)
			System.out.println();

	}

	private void gameOver() {
		gameRunning = false;
		repaint();
	}

	// 繪置迷宮
	private void paintMaze(Graphics g) {
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, MainForm.formWidth, MainForm.formHeight);
		for (int i = 0; i < maze.getRows(); i++) {
			for (int j = 0; j < maze.getCols(); j++) {
				switch (maze.getBlockContent(i, j)) {
				case wall:
					g.setColor(Color.BLUE);
					g.fillRect(paintingStartX + blockWidth * j, paintingStartY + blockWidth * i, blockWidth,
							blockWidth);
					break;
				case dot:
					g.setColor(Color.WHITE);
					g.fillOval(paintingStartX + blockWidth * j + (blockWidth * 3 >> 3),
							paintingStartY + blockWidth * i + (blockWidth * 3 >> 3), blockWidth >> 2, blockWidth >> 2);
					break;
				case power:
					g.setColor(Color.WHITE);
					g.fillOval(paintingStartX + blockWidth * j + (blockWidth >> 2),
							paintingStartY + blockWidth * i + (blockWidth >> 2), blockWidth >> 1, blockWidth >> 1);
					break;
				default:
					break;
				}
			}
		}
	}

	// 繪製玩家
	private void paintPacMan(Graphics g) {
		g.setColor(Color.YELLOW);
		int startAngle, arcAngle;

		switch (pacMan.getGraphicState()) {
		case 0:
			startAngle = 60;
			arcAngle = 240;
			break;
		case 3:
			startAngle = 0;
			arcAngle = 360;
			break;
		default:
			startAngle = 30;
			arcAngle = 300;
		}

		startAngle += pacMan.getDirection().ordinal() * 90;
		g.fillArc(paintingStartX + blockWidth * pacMan.getCol(), paintingStartY + blockWidth * pacMan.getRow(),
				blockWidth, blockWidth, startAngle, arcAngle);
	}

	// 繪製幽靈，顏色由追擊方式決定
	private void paintGhost(Graphics g) {

		for (Ghost ghost : ghosts) {
			if (!ghost.isDead()) {
				g.setColor(ghost.isScared() ? ghost.isFlashing() ? Color.LIGHT_GRAY : Color.BLUE : ghost.getColor());
				g.fillArc(paintingStartX + blockWidth * ghost.getCol(), paintingStartY + blockWidth * ghost.getRow(),
						blockWidth, blockWidth, 0, 180);
				g.fillRect(paintingStartX + blockWidth * ghost.getCol(),
						paintingStartY + blockWidth * ghost.getRow() + (blockWidth >> 1), blockWidth, blockWidth >> 2);

				for (int i = 0; i < 3; i++)
					g.fillArc(paintingStartX + blockWidth * ghost.getCol() + blockWidth / 3 * i,
							paintingStartY + blockWidth * ghost.getRow() + (blockWidth >> 1), blockWidth / 3,
							blockWidth / 3, 180, 180);
			}

			g.setColor(Color.WHITE);
			g.fillOval(paintingStartX + blockWidth * ghost.getCol() + (blockWidth >> 2) - (blockWidth >> 3),
					paintingStartY + blockWidth * ghost.getRow() + (blockWidth >> 2), blockWidth >> 2, blockWidth >> 2);
			g.fillOval(paintingStartX + blockWidth * ghost.getCol() + (blockWidth * 3 >> 2) - (blockWidth >> 3),
					paintingStartY + blockWidth * ghost.getRow() + (blockWidth >> 2), blockWidth >> 2, blockWidth >> 2);

			g.setColor(Color.BLACK);
			g.fillOval(paintingStartX + blockWidth * ghost.getCol() + (blockWidth >> 2) - (blockWidth >> 4),
					paintingStartY + blockWidth * ghost.getRow() + (blockWidth >> 2) + (blockWidth >> 4),
					blockWidth >> 3, blockWidth >> 3);
			g.fillOval(paintingStartX + blockWidth * ghost.getCol() + (blockWidth * 3 >> 2) - (blockWidth >> 4),
					paintingStartY + blockWidth * ghost.getRow() + (blockWidth >> 2) + (blockWidth >> 4),
					blockWidth >> 3, blockWidth >> 3);
		}
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		synchronized (stateLock) {
			paintMaze(g);
			paintGhost(g);
			paintPacMan(g);
		}
	}

}
