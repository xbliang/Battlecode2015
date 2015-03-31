package team120;

import team120.State;
import battlecode.common.*;

// Scan as much of map as possible

// Go as far away from enemy HQ as possible. go to that corner, then scan everywhere
// Go left, go up and down along border, then go right/left along top border
// Go up/down, depending on which one is farther to enemy HQ
// Go left/right, depending on which is farther
// Go up/down all the way, then come back up
// Go right/left all the way, the come back. 



public class Drone extends BaseBot {	
	public static Direction facing = Direction.NONE;
	public static int turnsCounter = 0;
	public static State state = State.STRAIGHT_TO_ENEMY_HQ;
	public static MapLocation scanPoint = new MapLocation(0, 0);
	public static int offset = 4;
	
	public Drone(RobotController rc) {
		super(rc);
	}
	
	
	public void scanDrone() throws GameActionException{
		if (state == State.DIAGONAL) {
			rc.setIndicatorString(1, "Diagonal");
		} else if (state == State.DIAGONAL_BACK) {
			rc.setIndicatorString(1, "DIAGONAL_BACK");
		} else if (state == State.TURNING1) {
			rc.setIndicatorString(1, "turning1");
		} else if (state == State.TURNING2) {
			rc.setIndicatorString(1, "turning2");
		} else if (state == State.TURNING3) {
			rc.setIndicatorString(1, "turning3");
		} else if (state == State.TURNING4) {
			rc.setIndicatorString(1, "turning4");
		} else if (state == State.STRAIGHT_TO_ENEMY_HQ) {
			rc.setIndicatorString(1, "STRAIGHT TO ENEMY HQ");
		} 
		
		
		if (rc.getLocation().x > rc.readBroadcast(maxXChannel)){
			rc.broadcast(maxXChannel, rc.getLocation().x);
		} if (rc.getLocation().x < rc.readBroadcast(minXChannel)){
			rc.broadcast(minXChannel, rc.getLocation().x);
		} if (rc.getLocation().y > rc.readBroadcast(maxYChannel)){
			rc.broadcast(maxYChannel, rc.getLocation().y);
		} if (rc.getLocation().y < rc.readBroadcast(minYChannel)){
			rc.broadcast(minYChannel, rc.getLocation().y);
		}
		
		// Diagram of how the scanning works http://gyazo.com/cc7c01ef6e107bb13e20623be31b9cf2
		// Drones expand search over time http://gyazo.com/4e9db516f43c46097efe9b863045178b
		
		// Travel to (theirHQ.x, theirHQ.y)
		// Then to (theirHQ.x +/- offset, theirHQ.y)
		// Then to (myHQ.x, myHQ.y +/- offset)
		// Then to (myHQ.x, myHQ.y)
		// Then to (myHQ.x +/- offset, myHQ.y)
		// Then to (theirHQ.x, theirHQ.y +/- offset)
		// (their HQ.x, their.x)
		// Then to (theirHQ.x +/- 2*offset, theirHQ.y)
		// Then to (myHQ.x, myHQ.y +/- 2*offset)
		// Then to (myHQ.x, myHQ.y)
		// Then to (myHQ.x +/- 2*offset, myHQ.y)
		// Then to (theirHQ.x, theirHQ.y +/- 2*offset)
		// (their HQ.x, their.x)
		
		int xOffset = 1;
		if (theirHQ.x > myHQ.x){
			xOffset = 1;
		} else {
			xOffset = -1;
		}
		
		int yOffset = 1;
		if (theirHQ.y < myHQ.y){
			yOffset = 1;
		} else {
			yOffset = -1;
		}
		
		if (state == State.STRAIGHT_TO_ENEMY_HQ){
			turnsCounter++;
			scanPoint = theirHQ;
			nav.moveTo(theirHQ);
			if (rc.getLocation().distanceSquaredTo(scanPoint) < 26 || turnsCounter > 100) {
				state = State.TURNING1;
				scanPoint = new MapLocation(scanPoint.x - xOffset*offset, scanPoint.y);
				turnsCounter = 0;
				return;
			}
		} else if (state == State.TURNING1){
			turnsCounter++;
			nav.moveTo(scanPoint);
			if (rc.getLocation().distanceSquaredTo(scanPoint) < 10 || turnsCounter > 50) {
				state = State.DIAGONAL_BACK;
				scanPoint = new MapLocation(myHQ.x, myHQ.y - yOffset*offset);
				turnsCounter = 0;
				return;
			}
		} else if (state == State.DIAGONAL_BACK){
			turnsCounter ++; 
			nav.moveTo(scanPoint);
			if (rc.getLocation().distanceSquaredTo(scanPoint) < 10 || turnsCounter > 100) {
				state = State.TURNING2;
				scanPoint = myHQ;
				turnsCounter = 0;
				return;
			}
		} else if (state == State.TURNING2){
			turnsCounter ++ ;
			nav.moveTo(scanPoint);
			if (rc.getLocation().distanceSquaredTo(scanPoint) < 10 || turnsCounter > 50) {
				state = State.TURNING3;
				scanPoint = new MapLocation(myHQ.x + xOffset*offset, myHQ.y);
				turnsCounter = 0;
				return;
			}
		} else if (state == State.TURNING3){
			turnsCounter++;
			nav.moveTo(scanPoint);
			if (rc.getLocation().distanceSquaredTo(scanPoint) < 10 || turnsCounter > 50) {
				state = State.DIAGONAL;
				scanPoint = new MapLocation(theirHQ.x, theirHQ.y + yOffset*offset);
				turnsCounter = 0;
				return;
			}
		} else if (state == State.DIAGONAL){
			turnsCounter++;
			nav.moveTo(scanPoint);
			if (rc.getLocation().distanceSquaredTo(scanPoint) < 10 || turnsCounter > 100) {
				state = State.TURNING4;
				scanPoint = theirHQ;
				turnsCounter = 0;
				return;
			}
		} else if (state == State.TURNING4){
			turnsCounter++;
			nav.moveTo(scanPoint);
			if (rc.getLocation().distanceSquaredTo(scanPoint) < 26 || turnsCounter > 50) {
				state = State.TURNING1;
				offset = offset*2;
				scanPoint = new MapLocation(scanPoint.x - xOffset*offset, scanPoint.y);
				turnsCounter = 0;
				return;
			}
		}

	}
	

	public void execute() throws GameActionException {
		scanDrone();
		
		if (getEnemiesInAttackingRange().length > 0) {
			if (rc.isWeaponReady()) {
				attackDrone(getEnemiesInAttackingRange());
			}
		}
	}

	public void attackDrone(RobotInfo[] enemies) throws GameActionException{
		if (enemies.length == 0) {
    		return;
    	}
    	MapLocation toAttack = null;
    	for (RobotInfo info : enemies) {
    		if (info.type == RobotType.MINER)
    			toAttack = info.location;
    		if (info.type == RobotType.MINERFACTORY && toAttack == null)
    			toAttack = info.location;
    		if (info.type == RobotType.BEAVER && toAttack == null)
    			toAttack = info.location;
    	}
    	if (toAttack == null)
    		attackLeastHealthEnemy(enemies);
    	else
    		rc.attackLocation(toAttack);
	}
}