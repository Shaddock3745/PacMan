package Panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;

import Algorithms.Search.SearchingType;
import Maze.Maze;

public final class MainForm extends JFrame {

	/**
	 * 主視窗物件
	 */
	private static final long serialVersionUID = 1L;
	public static final double FPS = 10.0;
	private static MainForm mainForm;
	private static JPanel mainPane;
	public static int formWidth = 1200, formHeight = 900;
	public static int ScreenWidth, ScreenHeight;
	private static GamePanel gamePanel;

	public static void main(String[] args) {

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					boolean debugMode = false;
					SearchingType searchingType = SearchingType.Astar;

					for (String s : args)

						if (s.equals("-debug"))
							debugMode = true;

						else if (s.equals("-dfs"))
							searchingType = SearchingType.DFS;

						else if (s.equals("-bfs"))
							searchingType = SearchingType.BFS;

						else if (s.equals("-iddfs"))
							searchingType = SearchingType.IDDFS;

						else if (s.equals("-ucs"))
							searchingType = SearchingType.UCS;

					mainForm = new MainForm(debugMode, searchingType);
					mainForm.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

	public MainForm(boolean debug, SearchingType defaultSearchingType) {
		ScreenWidth = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
		ScreenHeight = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
		setLocation((int) Math.floor((ScreenWidth - formWidth) / 2),
				(int) Math.floor(((ScreenHeight - formHeight) / 2)));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setTitle("PacMan");
		mainPane = new JPanel();
		mainPane.setBackground(Color.BLACK);
		mainPane.setPreferredSize(new Dimension(formWidth, formHeight));
		setContentPane(mainPane);
		pack();
		mainPane.setLayout(new BorderLayout(0, 0));
		gamePanel = new GamePanel(Maze.createClassicMaze(), defaultSearchingType, debug);
		mainPane.add(gamePanel);
		gamePanel.gameStart();
	}

}
