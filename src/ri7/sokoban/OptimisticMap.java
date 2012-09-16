package ri7.sokoban;

import java.awt.Point;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class OptimisticMap {
	private int[][] value;
	private int maxValue;
	
	public OptimisticMap(Level level){
		value = new int[level.getMapSize().x][level.getMapSize().y];
		
		for ( int x = 0; x < value.length ; x ++ ){
			for ( int y = 0 ; y < value[0].length ; y++){
				value[x][y] = Integer.MAX_VALUE;
			}
		}
		
		// Start from the exit point and expand
		List<Point> pendingPoints = new LinkedList<Point>();
		value[level.getExitPos().x][level.getExitPos().y] = 0;
		maxValue = 0;
		pendingPoints.add(new Point(level.getExitPos().x-1, level.getExitPos().y));
		pendingPoints.add(new Point(level.getExitPos().x+1, level.getExitPos().y));
		pendingPoints.add(new Point(level.getExitPos().x, level.getExitPos().y-1));
		pendingPoints.add(new Point(level.getExitPos().x, level.getExitPos().y+1));
		
		while ( pendingPoints.size() > 0 ){
			Point p = pendingPoints.get(0);
			pendingPoints.remove(0);
			
			if ( p.x < 0 || p.y < 0 || p.x >= level.getMapSize().x || p.y >= level.getMapSize().y ){
				continue;
			}
			
			if ( level.isWallAt(p.x, p.y)){
				continue;
			}
			
			int newValue = Integer.MAX_VALUE;
			
			if ( level.isClearSafe(p.x-1, p.y) ){
				newValue = Math.min(newValue, getValueSafe(p.x+1, p.y));
			}
			if ( level.isClearSafe(p.x+1, p.y)){
				newValue = Math.min(newValue, getValueSafe(p.x-1, p.y));
			}
			if ( level.isClearSafe(p.x, p.y-1)){
				newValue = Math.min(newValue, getValueSafe(p.x, p.y+1));
			}
			if ( level.isClearSafe(p.x, p.y+1)){
				newValue = Math.min(newValue, getValueSafe(p.x, p.y-1));
			}
			
			if ( newValue != Integer.MAX_VALUE ){
				newValue++;
			}
			
			if ( value[p.x][p.y] > newValue ){
				value[p.x][p.y] = newValue;
				maxValue = Math.max(newValue, maxValue);
				pendingPoints.add(new Point(p.x+1,p.y));
				pendingPoints.add(new Point(p.x-1,p.y));
				pendingPoints.add(new Point(p.x,p.y+1));
				pendingPoints.add(new Point(p.x,p.y-1));
			}
		}
	}
	
	private int getValueSafe(int x, int y){
		if ( x < 0 || y < 0 || x >= value.length || y >= value[0].length ){
			return Integer.MAX_VALUE;
		}
		return value[x][y];
	}
	
	public int getValue(int x, int y){
		return value[x][y];
	}

	public int getMaxValue() {
		return maxValue;
	}
}
