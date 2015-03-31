package bobplayer1;

import battlecode.common.*;

public class Barracks extends BaseBot {
    public Barracks(RobotController rc) {
        super(rc);
    }

    public void execute() throws GameActionException {
    	if (rc.isCoreReady() && rc.getTeamOre() > 200) {
            Direction newDir = getSpawnDirection(RobotType.SOLDIER);
            if (newDir != null) {
                rc.spawn(newDir, RobotType.SOLDIER);
            }
        }

    	rc.yield();
    }
}