package justineplayer1;

import battlecode.common.*;

public class TankFactory extends BaseBot {
    public TankFactory(RobotController rc) {
    	super(rc);
    }

    public void execute() throws GameActionException {
    	spawnAndBroadcast(RobotType.TANK);
    }
}