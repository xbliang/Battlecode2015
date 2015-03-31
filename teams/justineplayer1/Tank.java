package justineplayer1;

import battlecode.common.*;

public class Tank extends BaseBot {
	private static int swarmNum;
	
    public Tank(RobotController rc) throws GameActionException{
        super(rc);
        if (rc.readBroadcast(numTanksSwarm1) < 20) {
        	incrementBroadcast(numTanksSwarm1);
        	swarmNum = 1;
        } else if (rc.readBroadcast(numTanksSwarm2) < 20) {
        	incrementBroadcast(numTanksSwarm2);
        	swarmNum = 2;
        } else {
        	incrementBroadcast(numTanksSwarm3);
        	swarmNum = 3;
        }
    }

    public void execute() throws GameActionException {
    	if (getEnemiesInAttackingRange().length > 0) {
    		if (rc.isWeaponReady()){
    			attackOptimized(getEnemiesInAttackingRange());
    		}
    	} else if (rc.isCoreReady()) {
    		
    		// Default is defensive (near the HQ or heading towards a tower that needs help)
    		int rallyX = rc.readBroadcast(rally1XChannel);
            int rallyY = rc.readBroadcast(rally1YChannel);
            
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
    		
    		MapLocation rallyPoint = new MapLocation(rallyX, rallyY);
    		nav.moveTo(rallyPoint, false);
    		
        }
    }
}