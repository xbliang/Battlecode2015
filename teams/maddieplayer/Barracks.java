package maddieplayer;

import battlecode.common.*;

public class Barracks extends BaseBot {
    public Barracks(RobotController rc) {
        super(rc);
    }

    public void execute() throws GameActionException {
    	if (rc.isWeaponReady())
        	this.attackLeastHealthEnemy(this.getEnemiesInAttackingRange());
    	
    	if (rc.isCoreReady()) {
    		Direction spawnDir = getSpawnDirection(RobotType.SOLDIER);
    		
    		if (spawnDir != null && rc.senseNearbyRobots(rc.getType().sensorRadiusSquared, this.myTeam).length <= 20 ) {
            	rc.spawn(spawnDir, RobotType.SOLDIER);
            }
    	}
    }
}