package team120;

import battlecode.common.*;

public class Tank extends BaseBot {
	private static int myRallyXChannel = 100;
	private static int myRallyYChannel = 101;
	
    public Tank(RobotController rc) throws GameActionException {
        super(rc);
    }

    public void execute() throws GameActionException {
    	if (getEnemiesInAttackingRange().length > 0) {
    		if (rc.isWeaponReady()){
    			attackOptimized(getEnemiesInAttackingRange());
    		}
    	}
    	else if (rc.isCoreReady()) {
    		// Default is defensive (near the HQ or heading towards a tower that needs help)
    		int rallyX = rc.readBroadcast(myRallyXChannel);
            int rallyY = rc.readBroadcast(myRallyYChannel);
            
            /**
    		if (swarmNum == 1) {
    			if (rc.readBroadcast(numTanksSwarm1) >= 20) {
    				rallyX = rc.readBroadcast(rally2XChannel);
    				rallyY = rc.readBroadcast(rally2YChannel);
    			}
    		} else if (swarmNum == 2) {
    			if (rc.readBroadcast(numTanksSwarm2) >= 20) {
    				rallyX = rc.readBroadcast(rally2XChannel);
    				rallyY = rc.readBroadcast(rally2YChannel);
    			}
    		} else if (swarmNum == 3) {
    			if (rc.readBroadcast(numTanksSwarm3) >= 20) {
    				rallyX = rc.readBroadcast(rally2XChannel);
    				rallyY = rc.readBroadcast(rally2YChannel);
    			}
    		}
    		*/
            
            MapLocation rallyPoint = new MapLocation(rallyX, rallyY);
            rc.setIndicatorString(1, rallyPoint.toString());
    		
    		nav.moveTo(rallyPoint, false);
        }
    }
}