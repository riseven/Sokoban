package ri7.sokoban;

import java.awt.Point;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class Solver {
	public static List<Move> solve(Level level){
		return pathSolve(level);
	}
	
	private static int pathHeuristic(Level level, Point player, Point dst){
		return Math.abs(player.x - dst.x) + Math.abs(player.y - dst.y);
	}
	
	private static List<Move> pathTo(Level level, Point player, Point box, Point dst){
		int[][] arrived = new int[level.getMapSize().x][level.getMapSize().y];
		for ( int x = 0 ; x < level.getMapSize().x ; x++ ){
			for ( int y = 0 ; y < level.getMapSize().y ; y++ ){
				arrived[x][y] = Integer.MAX_VALUE;
			}
		}
		
		PathNode initNode = new PathNode();
		initNode.getPos().x = player.x;
		initNode.getPos().y = player.y;
		List<PathNode> initNodeList = new LinkedList<PathNode>();
		initNodeList.add(initNode);
		
		SortedMap<Integer, List<PathNode>> nodes = new TreeMap<Integer, List<PathNode>>();
		nodes.put(pathHeuristic(level, player, dst), initNodeList);
		
		while ( nodes.size() > 0 ){
			List<PathNode> bestScoreList = nodes.get(nodes.firstKey());
			PathNode node = bestScoreList.get(0);
			bestScoreList.remove(0);
			if ( bestScoreList.size() == 0 ){
				nodes.remove(nodes.firstKey());
			}
			
			if ( node.getPos().equals(dst) ){
				return node.getMoves();
			}
			
			List<PathNode> newNodes = new LinkedList<PathNode>();
			if ( level.isClearSafe(node.getPos().x-1, node.getPos().y) ){
				PathNode newNode = new PathNode();
				newNode.getPos().x = node.getPos().x-1;
				newNode.getPos().y = node.getPos().y;
				newNode.getMoves().addAll( node.getMoves() );
				newNode.getMoves().add(Move.Left);
				newNodes.add(newNode);
			}
			if ( level.isClearSafe(node.getPos().x+1, node.getPos().y) ){
				PathNode newNode = new PathNode();
				newNode.getPos().x = node.getPos().x+1;
				newNode.getPos().y = node.getPos().y;
				newNode.getMoves().addAll( node.getMoves() );
				newNode.getMoves().add(Move.Right);
				newNodes.add(newNode);
			}
			if ( level.isClearSafe(node.getPos().x, node.getPos().y-1) ){
				PathNode newNode = new PathNode();
				newNode.getPos().x = node.getPos().x;
				newNode.getPos().y = node.getPos().y-1;
				newNode.getMoves().addAll( node.getMoves() );
				newNode.getMoves().add(Move.Up);
				newNodes.add(newNode);
			}
			if ( level.isClearSafe(node.getPos().x, node.getPos().y+1) ){
				PathNode newNode = new PathNode();
				newNode.getPos().x = node.getPos().x;
				newNode.getPos().y = node.getPos().y+1;
				newNode.getMoves().addAll( node.getMoves() );
				newNode.getMoves().add(Move.Down);
				newNodes.add(newNode);
			}
			
			for ( PathNode newNode : newNodes ){
				
				if ( arrived[newNode.getPos().x][newNode.getPos().y] <= newNode.getMoves().size() ){
					continue;
				}
				if ( newNode.getPos().equals(box) ){
					continue;
				}
				
				arrived[newNode.getPos().x][newNode.getPos().y] = newNode.getMoves().size();
				
				int score = newNode.getMoves().size() + pathHeuristic(level, newNode.getPos(), dst);
				
				if ( ! nodes.containsKey(score) ){
					nodes.put(score, new LinkedList<PathNode>());
				}
				
				nodes.get(score).add(newNode);
			}
		}
		
		return null;
	}
	
	private static void addSolveNode(SortedMap<Integer, List<SolveNode>> nodes, SolveNode node, int score){
		if ( nodes.containsKey(score) == false ){
			nodes.put(score, new LinkedList<SolveNode>());
		}
		nodes.get(score).add(node);
	}
	
	private static SolveNode removeSolveNode(SortedMap<Integer, List<SolveNode>> nodes){
		List<SolveNode> nodeList = nodes.get(nodes.firstKey());
		SolveNode node = nodeList.get(0);
		nodeList.remove(0);
		if ( nodeList.isEmpty() ){
			nodes.remove(nodes.firstKey());
		}
		return node;
	}
	
	private static List<Move> pathSolve(Level level){
		// Calculate the optimistic map that would serve as heuristics function
		OptimisticMap optMap = new OptimisticMap(level);
		
		Point dst = level.getExitPos();
		
		// Open nodes
		SortedMap<Integer, List<SolveNode>> nodes = new TreeMap<Integer, List<SolveNode>>();
		
		// Current costs to arrive to solve nodes
		int[][][] arrived = new int[level.getMapSize().x][level.getMapSize().y][Move.values().length];
		for ( int x = 0 ; x < level.getMapSize().x ; x++ ){
			for ( int y = 0 ; y < level.getMapSize().y ; y++ ){
				for ( int d = 0 ; d < Move.values().length ; d++ ){
					arrived[x][y][d] = Integer.MAX_VALUE;
				}
			}
		}
		
		// Create initial nodes
		{
			Point box = level.getBoxPos();
			Point player = level.getPlayerPos();
			for ( Move dir : Move.values()){
				Point playerDst = dir.nextPosition(box);
				if ( level.isClearSafe(playerDst.x, playerDst.y)){
					List<Move> pathNoDesiredPlayerPosition = pathTo(level, player, box, playerDst);
					if ( pathNoDesiredPlayerPosition != null ){
						SolveNode node = new SolveNode();
						node.moves = pathNoDesiredPlayerPosition;
						node.box = (Point)box.clone();
						node.playerDir = dir;
						addSolveNode(nodes, node, node.moves.size() + optMap.getValue(node.box.x, node.box.y));
					}
				}
			}
		}
		
		while ( nodes.isEmpty() == false ){
			// Get node to process
			SolveNode node = removeSolveNode(nodes);
			if ( node.box.equals(dst) ){
				// This is the best solution
				return node.moves;
			}
			
			// Create new nodes trying to move the box in each direction
			for ( Move dir : Move.values() ){
				SolveNode newNode = new SolveNode();
				newNode.box = dir.nextPosition(node.box);
				newNode.playerDir = dir.opposite();
				
				// First check if the box can be moved to that position (is clear)
				if ( level.isClearSafe(newNode.box.x, newNode.box.y) == false ){
					continue;
				}
				
				// Check if the player can move to the pushing position
				Point player = node.playerDir.nextPosition(node.box);
				Point playerDst = dir.opposite().nextPosition(node.box);
				List<Move> playerMoves = pathTo(level, player, node.box, playerDst);
				if ( playerMoves == null ){
					// The player can't move itself to the pushing position
					continue;
				}
				
				// Check if the cost to arrive to the new node is less that the previous known
				if ( node.moves.size() + playerMoves.size() + 1 >= arrived[newNode.box.x][newNode.box.y][dir.index()] ){
					continue;
				}
				
				System.out.println("" + newNode.box.x + " " + newNode.box.y);
				
				// Add the new node to the open nodes
				newNode.moves.addAll(node.moves);
				newNode.moves.addAll(playerMoves);
				newNode.moves.add(dir);
				addSolveNode(nodes, newNode, newNode.moves.size() + optMap.getValue(newNode.box.x, newNode.box.y));
				arrived[newNode.box.x][newNode.box.y][dir.index()] = newNode.moves.size();
			}
		}
		
		// There is no solution
		return null;
	}
}
