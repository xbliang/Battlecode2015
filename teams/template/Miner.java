package template;

import battlecode.common.*;

public class Miner extends BaseBot {
    public Miner(RobotController rc) {
        super(rc);
    }

    public void execute() throws GameActionException {
    	if (rc.isWeaponReady()) {
        	this.attackLeastHealthEnemy(this.getEnemiesInAttackingRange());
        }
    }
    
    //public MapLocation 
}