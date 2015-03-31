package bobplayer1;

import battlecode.common.*;

public class Soldier extends BaseBot {
    public Soldier(RobotController rc) {
        super(rc);
    }

    public void execute() throws GameActionException {
    	if (getEnemiesInAttackingRange().length > 0) {
    		if (rc.isWeaponReady()){
    			attackOptimized(getEnemiesInAttackingRange());
    		}
    	}
    	else if (rc.isCoreReady()) {
            int rallyX = rc.readBroadcast(3);
            int rallyY = rc.readBroadcast(4);
            MapLocation rallyPoint = new MapLocation(rallyX, rallyY);

            Direction newDir = getMoveDir(rallyPoint);

            if (newDir != null) {
                rc.move(newDir);
            }
        }
        rc.yield();
    }
}