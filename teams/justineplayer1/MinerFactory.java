package justineplayer1;

import battlecode.common.*;

public class MinerFactory extends BaseBot {
	
    public MinerFactory(RobotController rc) {
        super(rc);
        state = State.SPAWNING;
    }

    public void execute() throws GameActionException {
    	RobotInfo[] nearbyEnemies = this.getEnemiesInAttackingRange();
    	state = nearbyEnemies.length > 0 ? State.ATTACKING : State.SPAWNING;
   	
    	if (rc.readBroadcast(numMinersChannel) < 25) {
    		spawnAndBroadcast(RobotType.MINER);
    	}
    }
}