package template;

import battlecode.common.*;

public class HQ extends BaseBot {
    public HQ(RobotController rc) {
        super(rc);
    }

    public void execute() throws GameActionException {
        if (rc.isWeaponReady()) {
        	this.attackLeastHealthEnemy(this.getEnemiesInAttackingRange());
        }
    }
}