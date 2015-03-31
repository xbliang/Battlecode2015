package team120;

import battlecode.common.*;

public class Barracks extends BaseBot {
    public Barracks(RobotController rc) {
        super(rc);
    }

    public void execute() throws GameActionException {
    	if (rc.readBroadcast(numSoldiersChannel) < this.maxSoldiers){
    		spawnAndBroadcast(RobotType.SOLDIER);
    	}    	
    	
    	
    	/**
    	if (rc.readBroadcast(numSoldiersChannel) < 10){
    		spawnAndBroadcast(RobotType.SOLDIER);
    	}
    	
    	
    	// Should spawn tanks and not soldiers
    	if (rc.readBroadcast(stratChannel) == Strategy.TANKS.value){
    		
    	}
    	
    	// Probably not going to use soldiers strategy
    	else if (rc.readBroadcast(stratChannel) == Strategy.SOLDIERS.value) {
    		spawnAndBroadcast(RobotType.SOLDIER);
    	}
    	*/
    	
    	// Should we ever spawn bashers?

    }
}