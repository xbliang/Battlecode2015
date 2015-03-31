package template;

import battlecode.common.*;

public class Tower extends BaseBot {
    public Tower(RobotController rc) {
        super(rc);
    }

    public void execute() throws GameActionException {
    	if (rc.isWeaponReady()) {
        	this.attackLeastHealthEnemy(this.getEnemiesInAttackingRange());
        }
    }
}