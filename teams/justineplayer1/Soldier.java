package justineplayer1;

import battlecode.common.*;

public class Soldier extends BaseBot {
    public Soldier(RobotController rc) {
        super(rc);
    }

    public void execute() throws GameActionException {
    	// Should move towards enemy hQ to see more of map
    	attackWhileMovingTo(theirHQ);
    	
    	
    }
}