package bobplayer1;

import battlecode.common.*;

public class TankFactory extends BaseBot {
    public TankFactory(RobotController rc) {
        super(rc);
    }

    public void execute() throws GameActionException {
    	if (rc.isCoreReady() && rc.getTeamOre() > 200) {
            Direction newDir = getSpawnDirection(RobotType.TANK);
            if (newDir != null) {
                rc.spawn(newDir, RobotType.TANK);
            }
        }

    	rc.yield();
    }
}