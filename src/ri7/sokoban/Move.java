package ri7.sokoban;

import java.awt.Point;

public enum Move {
	Up,
	Right,
	Down,
	Left;
	
	public Point nextPosition(Point p){
		if ( this == Up ){
			return new Point(p.x, p.y-1);
		} else if ( this == Down ){
			return new Point(p.x, p.y+1);
		} else if ( this == Left ){
			return new Point(p.x-1, p.y);
		} else if ( this == Right ){
			return new Point(p.x+1, p.y);
		} else {
			throw new RuntimeException("Unknwnon move");
		}
	}
	
	public Move opposite(){
		if ( this == Up ){
			return Down;
		} else if ( this == Down ){
			return Up;
		} else if ( this == Left ){
			return Right;
		} else if ( this == Right ){
			return Left;
		} else {
			throw new RuntimeException("Unknown move");
		}
	}
	
	public int index(){
		if ( this == Up ){
			return 0;
		} else if ( this == Down ){
			return 1;
		} else if ( this == Left ){
			return 2;
		} else if ( this == Right ){
			return 3;
		} else {
			throw new RuntimeException("Unknown move");
		}
	}
}
