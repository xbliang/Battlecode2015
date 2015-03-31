package team120;

import battlecode.common.*;

public class TrainingField extends BaseBot {
    public TrainingField(RobotController rc) {
        super(rc);
    }

    public void execute() throws GameActionException {
    	if (rc.readBroadcast(numCommanderChannel) < 1) {
    		this.spawnAndBroadcast(RobotType.COMMANDER);
    	}
    }
}