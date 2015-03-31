package justineplayer1;

import battlecode.common.*;

public class Tower extends BaseBot {
	private static double prevHealth;
	
    public Tower(RobotController rc) {
        super(rc);
        prevHealth = rc.getHealth();
    }

    public void execute() throws GameActionException {
    	if (rc.getHealth() < prevHealth - 10){
    		// Alert defensive tanks
    		rc.broadcast(rally1XChannel, rc.getLocation().x);
    		rc.broadcast(rally1YChannel, rc.getLocation().y);
    	}
    	if (rc.isWeaponReady()) {
    		attackLeastHealthEnemy(getEnemiesInAttackingRange());
    	} if (rc.senseNearbyRobots(rc.getType().sensorRadiusSquared, theirTeam).length == 0) {
    		
    		// If tower is now safe, call off defense of the swarm
    		if (rc.getLocation().x == rc.readBroadcast(rally1XChannel) && 
    			rc.getLocation().y == rc.readBroadcast(rally1YChannel)){
    			
    			MapLocation rallyPoint = new MapLocation( (3*myHQ.x + theirHQ.x) / 4,
    					(3*myHQ.y + theirHQ.y)/ 4);
    			
    			rc.broadcast(rally1XChannel, rallyPoint.x);
    			rc.broadcast(rally1YChannel, rallyPoint.y);
    		}
    	}
    }
}