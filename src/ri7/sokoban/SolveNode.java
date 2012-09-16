package ri7.sokoban;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class SolveNode {
	public List<Move> moves = new ArrayList<Move>();
	public Point box = new Point();
	public Move playerDir;
}
