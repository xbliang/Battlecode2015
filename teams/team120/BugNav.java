package team120;

import battlecode.common.*;

public class BugNav {
	private RobotController rc;
	private MapLocation myHQ;
	private MapLocation enemyHQ;
	private MapLocation[] myTowers;
	private MapLocation[] enemyTowers;
	
	private boolean movingToLocation, bugging, buggingClockwise;
	private MapLocation startLocation, endLocation;
	private MapLocation[] mLineLocations;
	private int farthestProgress; // progress along array of m-line locations
	private Direction lastMoveDir; // direction of most recent move
	private MapLocation lastBuggingStartLocation;
	private boolean turned; // whether we just turned after hitting the edge
	private Direction queuedDir;
	
	public BugNav(RobotController myrc) {
		rc = myrc;
		myHQ = rc.senseHQLocation();
		enemyHQ = rc.senseEnemyHQLocation();
		myTowers = rc.senseTowerLocations();
		enemyTowers = rc.senseEnemyTowerLocations();
	}
	
	public TerrainTile senseTerrainInDirection(Direction dir) {
		return rc.senseTerrainTile(rc.getLocation().add(dir));
	}
	
	public boolean withinEnemyRange(MapLocation loc) {
		// doesn't count destination (if it's a tower or HQ) among list of enemies to avoid
		for (int i = 0; i < enemyTowers.length + 1; i++) {
			MapLocation enemy = (i == enemyTowers.length) ? enemyHQ : enemyTowers[i];
			if (!enemy.equals(endLocation) && loc.distanceSquaredTo(enemy) <= 24) {
				return true;
			}
		}
		return false;
	}
	
	public boolean canTraverse(Direction dir, boolean avoidEnemyTowers) {
		// TODO: don't treat HQs and towers as traversable, in general
		//return (senseTerrainInDirection(dir).isTraversable() && !rc.getLocation().add(dir).equals(myHQ));
		
		if (!rc.canMove(dir)) {
			return false;
		}
		
		// avoid enemy towers and HQ, if applicable
		
		if (avoidEnemyTowers) {
			MapLocation myLoc = rc.getLocation();
			
			// everything is traversable if we're in a location within enemy range and we're avoiding towers
			if (withinEnemyRange(myLoc)) {
				return true;
			}
			
			if (rc.canMove(dir)) {            	
				if (withinEnemyRange(rc.getLocation().add(dir))) {
					return false;
				}
			}
		}
		return true;
	}
	
	private Direction nextBugDirection(Direction prevDir, boolean clockwise, boolean avoidEnemyTowers) {
		Direction nextDir;
		
		if (clockwise) {
			nextDir = prevDir.rotateRight().rotateRight();
		}
		else {
			nextDir = prevDir.rotateLeft().rotateLeft();
		}
		
		while(!canTraverse(nextDir, avoidEnemyTowers)) {
			if (senseTerrainInDirection(nextDir) == TerrainTile.OFF_MAP) {
				// if we hit the edge of the map, turn around
				buggingClockwise = !buggingClockwise;
				turned = true;
				return prevDir.opposite();
			}
			else {
				if (clockwise) {
					nextDir = nextDir.rotateLeft();
				}
				else {
					nextDir = nextDir.rotateRight();
				}
			}
		}
		return nextDir;
	}

	private MapLocation[] getMLineLocations(MapLocation start, MapLocation end) {
		// returns array of locations in m-line (only approximately straight)
		int dx = Math.abs(end.x - start.x);
		int dy = Math.abs(end.y - start.y);
		int numLocs = Math.max(dx,dy) + 1;
		MapLocation[] locs = new MapLocation[numLocs];

		MapLocation currLoc = start;
		for (int i=0; i<numLocs; i++) {
			locs[i] = currLoc;
			//System.out.println(currLoc.x + ", " + currLoc.y);
			currLoc = currLoc.add(currLoc.directionTo(end));
		}
		return locs;
	}

	public Direction nextMoveDirection(boolean avoidEnemyTowers) {
		// straightforward implementation of bug2 algorithm
		MapLocation currLocation = rc.getLocation();

		if (bugging) {
			// stop bugging if we're on the straight line, strictly farther than we've been before
			// TODO: binary search
			for (int i=farthestProgress+1; i<mLineLocations.length; i++) {
				if (currLocation.equals(mLineLocations[i])) {
					farthestProgress = i;
					bugging = false;
					break;
				}
			}
		}

		// otherwise, if we just moved diagonally, check if we're adjacent to the m-line and move there
		if (bugging) {
			if (lastMoveDir == Direction.NORTH_EAST || lastMoveDir == Direction.NORTH_WEST
					|| lastMoveDir == Direction.SOUTH_EAST || lastMoveDir == Direction.SOUTH_WEST ) {
				
				// check if we're at a 2x2 intersection of our path and the m-line (the only kind that matters (?))
				Direction[] tryDirs = {lastMoveDir.opposite().rotateLeft(), lastMoveDir.opposite().rotateRight()};
				MapLocation[] tryLocs = {currLocation.add(tryDirs[0]), currLocation.add(tryDirs[1])};
				
				boolean found2x2Intersection = false;
				Direction dirToMLine = Direction.NONE; // the farther one in the 2x2 square
				for (int i=farthestProgress+1; i<mLineLocations.length-1; i++) {
					if (mLineLocations[i].equals(tryLocs[0])) {
						if (mLineLocations[i+1].equals(tryLocs[1])) {
							found2x2Intersection = true;
							farthestProgress = i;
							dirToMLine = tryDirs[1];
						}
						break;
					}
					else if (mLineLocations[i].equals(tryLocs[1])) {
						if (mLineLocations[i+1].equals(tryLocs[0])) {
							found2x2Intersection = true;
							farthestProgress = i;
							dirToMLine = tryDirs[0];
						}
						break;
					}
				}
				
				if (found2x2Intersection) {
					//System.out.println("found 2x2 intersection");
					if (canTraverse(dirToMLine, avoidEnemyTowers)) {
						// get back on the m-line if it's not blocked
						bugging = false;
						return dirToMLine;
					}
					else {
						// start bugging in opposite direction
						//System.out.println("reversing bugging direction");
						buggingClockwise = !buggingClockwise;
					}
				}
			}
		}

		// now figure out how to move in all other cases
		if (!bugging) {
			if (canTraverse(currLocation.directionTo(endLocation), avoidEnemyTowers)) {
				// continue motion on m-line
				farthestProgress++;
				return currLocation.directionTo(endLocation);
			}
			else {
				// obstacle detected, so start bugging
				bugging = true;
				lastBuggingStartLocation = currLocation;
				
				// decide whether to bug clockwise or counterclockwise, based on distance to goal
				
				Direction clockwiseTryDir = currLocation.directionTo(endLocation).rotateLeft();
				while (!canTraverse(clockwiseTryDir, avoidEnemyTowers)) {
					clockwiseTryDir = clockwiseTryDir.rotateLeft();
				}
				MapLocation clockwiseTryLoc = currLocation.add(clockwiseTryDir);
				
				Direction counterclockwiseTryDir = currLocation.directionTo(endLocation).rotateRight();
				while (!canTraverse(counterclockwiseTryDir, avoidEnemyTowers)) {
					counterclockwiseTryDir = counterclockwiseTryDir.rotateRight();
				}
				MapLocation counterclockwiseTryLoc = currLocation.add(counterclockwiseTryDir);
				
				if (counterclockwiseTryLoc.distanceSquaredTo(endLocation) >= clockwiseTryLoc.distanceSquaredTo(endLocation)) {
					buggingClockwise = true;
					return clockwiseTryDir;
				}
				else {
					buggingClockwise = false;
					return counterclockwiseTryDir;
				}
			}
		}
		else {
			return nextBugDirection(lastMoveDir, buggingClockwise, avoidEnemyTowers);
		}
	}

	private void resetMoveData(MapLocation dest) {
		movingToLocation = true;
		bugging = false;
		queuedDir = Direction.NONE;
		startLocation = rc.getLocation();
		endLocation = dest;
		mLineLocations = getMLineLocations(startLocation, endLocation);
		farthestProgress = 0;
		lastBuggingStartLocation = startLocation;
		turned = false;
	}
	
	private void resetEnemyTowers() {
		enemyTowers = rc.senseEnemyTowerLocations();
	}
	
	public void moveTo(MapLocation dest, boolean avoidEnemyTowers) throws GameActionException {
		
		if (Clock.getRoundNum() % 10 == 0) {
			resetEnemyTowers();
		}
		
		if (rc.getLocation().equals(dest)) {
			movingToLocation = false;
			return;
		}

		if (!dest.equals(endLocation)) {
			// reset navigation data when moving somewhere new
			resetMoveData(dest);
		}

		if (rc.getLocation().add(queuedDir) == lastBuggingStartLocation) {
			if (turned) {
				turned = false;
			}
			else {
				resetMoveData(dest);
				return;
			}
		}
		
		if (queuedDir == Direction.NONE) {
			queuedDir = nextMoveDirection(avoidEnemyTowers);
			//System.out.println(queuedDir);
		}
		
		if (rc.isCoreReady() && rc.canMove(queuedDir)) {
			rc.move(queuedDir);
			lastMoveDir = queuedDir;
			queuedDir = Direction.NONE;
		}
	}
	
	public void moveTo(MapLocation dest) throws GameActionException{
		moveTo(dest, true);
	}
	
	public boolean isMovingToLocation() {
		return movingToLocation;
	}
	
	public Direction facing() {
		return lastMoveDir;
	}
}