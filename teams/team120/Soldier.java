package team120;

import battlecode.common.*;

public class Soldier extends BaseBot {
	private static int myRallyXChannel = 100;
	private static int myRallyYChannel = 101;
	
    public Soldier(RobotController rc) {
        super(rc);
    }
    
    public Soldier(RobotController rc, int group) {
    	super(rc);
    }

    public void execute() throws GameActionException {
    	if (getEnemiesInAttackingRange().length > 0) {
    		if (rc.isWeaponReady()){
    			attackOptimized(getEnemiesInAttackingRange());
    		}
    	}
    	else if (rc.isCoreReady()) {
            int rallyX = rc.readBroadcast(myRallyXChannel);
            int rallyY = rc.readBroadcast(myRallyYChannel);
            
            MapLocation rallyPoint = new MapLocation(rallyX, rallyY);
            rc.setIndicatorString(1, rallyPoint.toString());
            
            if (rc.getLocation().distanceSquaredTo(rallyPoint) <= 40) {
            	rc.setIndicatorString(0, "near tower");
            	this.nav.moveTo(rallyPoint, false);
            } else {
            	rc.setIndicatorString(0, "not near tower");
            	this.nav.moveTo(rallyPoint, true);
            }
        }
    }
}