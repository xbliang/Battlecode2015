package justineplayer1;

import battlecode.common.*;

public class Helipad extends BaseBot {
    public Helipad(RobotController rc) {
        super(rc);
    }

    public void execute() throws GameActionException {
    	if (rc.readBroadcast(numDronesChannel) < 5){
        	spawnAndBroadcast(RobotType.DRONE);
    	}

    	
    	/**
    	if (rc.readBroadcast(stratChannel) == Strategy.NONE.value){
    		if (rc.getLocation().distanceSquaredTo(theirHQ) > 35){
    			moveTowardsEnemyHQ();
    		}
    		else {
    			moveRandomly();
    		}
    		
    		if (rc.readBroadcast(targetXChannel) != 0 && rc.readBroadcast(targetYChannel) != 0){
    			// go towards there at attack
    		}
    		
    		RobotInfo[] nearbyEnemyRobots = rc.senseNearbyRobots(24, theirTeam);
    		for (int i = 0; i < nearbyEnemyRobots.length; i++){
    			if (nearbyEnemyRobots[i].type == RobotType.BARRACKS || nearbyEnemyRobots[i].type == RobotType.MINERFACTORY
    					|| nearbyEnemyRobots[i].type == RobotType.HELIPAD){
    				// broadcast location to attack for other drones
    				// somehow make sure don't attack empty place
    			}
    		}
    	}
    	*/
    }
}