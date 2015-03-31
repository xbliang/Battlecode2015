package bobplayer1;

import battlecode.common.*;
import java.util.*;

public class BaseBot {
    protected RobotController rc;
    protected MapLocation myHQ, theirHQ;
    protected Team myTeam, theirTeam;

    public BaseBot(RobotController rc) {
        this.rc = rc;
        this.myHQ = rc.senseHQLocation();
        this.theirHQ = rc.senseEnemyHQLocation();
        this.myTeam = rc.getTeam();
        this.theirTeam = this.myTeam.opponent();
    }

    public void go() throws GameActionException {
        beginningOfTurn();
        execute();
        endOfTurn();
    }
    
    public void beginningOfTurn() {
        if (rc.senseEnemyHQLocation() != null) {
            this.theirHQ = rc.senseEnemyHQLocation();
        }
    }
    
    public void execute() throws GameActionException {
        
    }

    public void endOfTurn() {
    	rc.yield();
    }
    
    //
    // 	HELPER FUNCTIONS
    //
    public Direction[] getDirectionsToward(MapLocation dest) {
        Direction toDest = rc.getLocation().directionTo(dest);
        Direction[] dirs = {toDest,
	    		toDest.rotateLeft(), toDest.rotateRight(),
			toDest.rotateLeft().rotateLeft(), toDest.rotateRight().rotateRight()};

        return dirs;
    }

    public Direction getMoveDir(MapLocation dest) {
        Direction[] dirs = getDirectionsToward(dest);
        for (Direction d : dirs) {
            if (rc.canMove(d)) {
                return d;
            }
        }
        return null;
    }

    public Direction getSpawnDirection(RobotType type) {
        Direction[] dirs = getDirectionsToward(this.theirHQ);
        for (Direction d : dirs) {
            if (rc.canSpawn(d, type)) {
                return d;
            }
        }
        return null;
    }

    public Direction getBuildDirection(RobotType type) {
        Direction[] dirs = getDirectionsToward(this.theirHQ);
        for (Direction d : dirs) {
            if (rc.canBuild(d, type)) {
                return d;
            }
        }
        return null;
    }

    public RobotInfo[] getAllies() {
        return rc.senseNearbyRobots(Integer.MAX_VALUE, myTeam);
    }

    public RobotInfo[] getEnemiesInAttackingRange() {
        return rc.senseNearbyRobots(RobotType.SOLDIER.attackRadiusSquared, theirTeam);
    }

    public void attackLeastHealthEnemy(RobotInfo[] enemies) throws GameActionException {
        if (enemies.length == 0) {
            return;
        }

        double minEnergon = Double.MAX_VALUE;
        MapLocation toAttack = null;
        for (RobotInfo info : enemies) {
            if (info.health < minEnergon) {
                toAttack = info.location;
                minEnergon = info.health;
            }
        }

        rc.attackLocation(toAttack);
    }
    
    public void attackOptimized(RobotInfo[] enemies) throws GameActionException {
    	if (enemies.length == 0) {
    		return;
    	}
    	MapLocation toAttack = null;
    	for (RobotInfo info : enemies) {
    		if (info.type == RobotType.HQ)
    			toAttack = info.location;
    		if (info.type == RobotType.TOWER && toAttack == null)
    			toAttack = info.location;
    	}
    	if (toAttack == null)
    		attackLeastHealthEnemy(enemies);
    	else
    		rc.attackLocation(toAttack);
    }
}