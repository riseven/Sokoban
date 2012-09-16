package ri7.sokoban;

import java.awt.Point;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class Level {
	public static final char WALL = '#';
	public static final char CLEAR = ' ';
	public static final char PLAYER = '@';
	public static final char BOX = 'X';
	public static final char EXIT = '!';
	public static final char NEW_LINE = '\n';
	
	private List<List<Boolean>> isWall;
	private Point playerPos;
	private Point boxPos;
	private Point exitPos;
	private Point mapSize;
	
	public Level(String fileName) throws Exception{
		FileInputStream fis = new FileInputStream(fileName);
		int c = fis.read();
		
		isWall = new ArrayList<List<Boolean>>();
		int x = 0;
		int y = 0;
		while ( c != -1 ){
			if ( x == 0 ){
				isWall.add(new ArrayList<Boolean>());
			}
			
			if ( c == WALL ){
				isWall.get(y).add(true);
				x++;
			} else if ( c == NEW_LINE ){
				y++;
				x = 0;
			} else if ( c == PLAYER ){
				isWall.get(y).add(false);
				playerPos = new Point(x, y);
				x++;
			} else if ( c == BOX ){
				isWall.get(y).add(false);
				boxPos = new Point(x, y);
				x++;
			} else if ( c == EXIT ){
				isWall.get(y).add(false);
				exitPos = new Point(x, y);
				x++;
			} else if ( c == CLEAR ){
				isWall.get(y).add(false);
				x++;
			}
			
			c = fis.read();
		}
		
		mapSize = new Point(isWall.get(0).size(), isWall.size());
	}

	public Point getPlayerPos() {
		return playerPos;
	}

	public Point getBoxPos() {
		return boxPos;
	}

	public Point getExitPos() {
		return exitPos;
	}

	public Point getMapSize() {
		return mapSize;
	}

	public boolean isWallAt(int x, int y){
		return isWall.get(y).get(x);
	}
	
	public boolean isClearSafe(int x, int y){
		if ( x < 0 || y <0 || x >= mapSize.x || y >= mapSize.y ){
			return false;
		}
		
		return ! isWallAt(x, y);
	}
}
