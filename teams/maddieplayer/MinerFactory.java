package maddieplayer;

import battlecode.common.*;

public class MinerFactory extends BaseBot {
	
    public MinerFactory(RobotController rc) {
        super(rc);
    }

    public void execute() throws GameActionException {
    	if (rc.isWeaponReady())
        	this.attackLeastHealthEnemy(this.getEnemiesInAttackingRange());
    	
    	if (rc.isCoreReady()) {
    		Direction spawnDir = getSpawnDirection(RobotType.MINER);
    		
    		if (spawnDir != null && rc.senseNearbyRobots(20, this.myTeam).length <= 15 ) {
            	rc.spawn(spawnDir, RobotType.MINER);
            }
    	}
    }
}