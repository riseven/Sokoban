package ri7.sokoban;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class PathNode {
	private List<Move> moves = new ArrayList<Move>();
	private Point pos = new Point();
	
	public List<Move> getMoves() {
		return moves;
	}

	public Point getPos() {
		return pos;
	}
}
