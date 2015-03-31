package team120;

import battlecode.common.*;

public class TankFactory extends BaseBot {
    public TankFactory(RobotController rc) {
    	super(rc);
    }

    public void execute() throws GameActionException {
    	if (rc.readBroadcast(this.numTanksChannel) < this.maxTanks)
    		spawnAndBroadcast(RobotType.TANK);
    }
}