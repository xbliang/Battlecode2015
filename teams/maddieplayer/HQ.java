package maddieplayer;

import battlecode.common.*;

public class HQ extends BaseBot {
	private int beaverCount = 0;
	
    public HQ(RobotController rc) {
        super(rc);
    }

    public void execute() throws GameActionException {
        if (rc.isWeaponReady())
        	this.attackLeastHealthEnemy(this.getEnemiesInAttackingRange());
        
        Direction spawnDir = getSpawnDirection(RobotType.BEAVER);
        
        if (rc.isCoreReady() && spawnDir != null && rc.senseNearbyRobots(20, this.myTeam).length <= 15 && beaverCount < 20) {
        	rc.spawn(spawnDir, RobotType.BEAVER);
        	beaverCount++;
        	
        	rc.broadcast(4, beaverCount);
        }
    }
}