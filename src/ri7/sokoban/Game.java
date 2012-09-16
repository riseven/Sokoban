package ri7.sokoban;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Game extends Canvas{
	private int cellSize;
	private Level level;
	private OptimisticMap optMap;
	private Point playerPos;
	private Point boxPos;
	private List<Move> solution;
	
	public Game() throws Exception{
		level = new Level("level.txt");
		
		playerPos = (Point)level.getPlayerPos().clone();
		boxPos = (Point)level.getBoxPos().clone();

		// Calculate optimal cell size
		int maxWidht = 800 / level.getMapSize().x;
		int maxHeigh = 600 / level.getMapSize().y;		
		cellSize = Math.min(maxWidht, maxHeigh);

		// Init window
		initWindow();
		
		optMap = new OptimisticMap(level);
		
		solution = Solver.solve(level);
		
		// Draw initial level
		drawLevelInitial();
		
		
		
		loop();
	}
	
	private void initWindow(){
		JFrame container = new JFrame("Let's Sokoban!");
		JPanel panel = (JPanel) container.getContentPane();
		panel.setPreferredSize(new Dimension(800, 600));
		panel.setLayout(null);
		setBounds(0, 0, 800, 600);
		panel.add(this);
		
		container.pack();
		container.setResizable(false);
		container.setVisible(true);
		
		createBufferStrategy(2);
		
		container.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
	}
	
	private void drawLevelInitial(){
		Graphics2D g = (Graphics2D) getBufferStrategy().getDrawGraphics();
		
		drawMap(g);
		
		drawExit(g, level.getExitPos().x, level.getExitPos().y);
		drawBox(g, level.getBoxPos().x, level.getBoxPos().y);
		drawPlayer(g, level.getPlayerPos().x, level.getPlayerPos().y);
		
		g.dispose();
		getBufferStrategy().show();
	}
	
	private void drawMap(Graphics2D g){
		g.setColor(Color.black);
		g.fillRect(0, 0, 800, 600);
		
		for ( int x = 0 ; x < level.getMapSize().x ; x++ ){
			for ( int y = 0 ; y < level.getMapSize().y ; y++){
				if ( level.isWallAt(x, y) ){
					g.setColor(Color.darkGray);
				} else {
					if ( optMap.getValue(x, y) == Integer.MAX_VALUE ){
						g.setColor(Color.lightGray);
					} else {
						g.setColor(new Color(
								1.0f - (optMap.getValue(x, y)*1.0f/optMap.getMaxValue())/2.0f,
								1.0f - (optMap.getValue(x, y)*1.0f/optMap.getMaxValue())/2.0f,
								0));
					}
				}
				g.fillRect(x * cellSize, y * cellSize, cellSize, cellSize);
			}
		}
	}

	private void drawBox(Graphics2D g, int x, int y){
		g.setColor(Color.blue);
		
		g.fillRect(x * cellSize + (cellSize/10), y * cellSize + (cellSize/10), cellSize*8/10, cellSize*8/10);
	}

	private void drawPlayer(Graphics2D g, int x, int y){
		g.setColor(Color.green);
		
		g.fillOval(x * cellSize + cellSize/5, y * cellSize + cellSize/5, cellSize*3/5, cellSize*3/5);
	}
	
	private void drawExit(Graphics2D g, int x, int y){
		g.setColor(Color.red);
		
		g.fillOval(x * cellSize + cellSize/10, y * cellSize + cellSize/10, cellSize*8/10, cellSize*8/10);
	}
	
	private void loop() throws Exception{
		int step = -2;
		
		while ( true ){
			Graphics2D g = (Graphics2D) getBufferStrategy().getDrawGraphics();
			
			drawMap(g);
			
			drawExit(g, level.getExitPos().x, level.getExitPos().y);
			drawBox(g, boxPos.x, boxPos.y);
			drawPlayer(g, playerPos.x, playerPos.y);
			
			g.dispose();
			getBufferStrategy().show();
			
			
			
			Thread.sleep(1000);
			
			if ( step >= 0 && step < solution.size() ){
				playerPos.x += solution.get(step) == Move.Right ? 1 : 0;
				playerPos.x -= solution.get(step) == Move.Left ? 1 : 0;
				playerPos.y += solution.get(step) == Move.Down ? 1 : 0;
				playerPos.y -= solution.get(step) == Move.Up ? 1 : 0;
			}
			step++;
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		Game game = new Game();
	}

}
