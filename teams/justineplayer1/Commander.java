package justineplayer1;

import battlecode.common.*;
import java.util.*;

public class Commander extends BaseBot {
    public Commander(RobotController rc) {
        super(rc);
    }

    public void execute() throws GameActionException {
    	if (this.shouldRetreat() && rc.getFlashCooldown() == 0 && rc.isCoreReady()) {
    		rc.castFlash(this.pickBestFlashLoc());
    	} else if (getEnemiesInAttackingRange().length > 0) {
    		if (rc.isWeaponReady()){
    			attackOptimized(getEnemiesInAttackingRange());
    		}
    	}
    	
    	int rallyX, rallyY;
    	
        if (Clock.getRoundNum() < swarm2) {
        	rallyX = rc.readBroadcast(100);
        	rallyY = rc.readBroadcast(101);
        } else {
        	rallyX = rc.readBroadcast(102);
        	rallyY = rc.readBroadcast(103);
        }
        
        MapLocation rallyPoint = new MapLocation(rallyX, rallyY);

        Direction newDir = getMoveDir(rallyPoint);

        if (newDir != null && rc.isCoreReady()) {
            rc.move(newDir);
        }
    }
    
    private Set<MapLocation> getAllValidFlashLocs() throws GameActionException {
    	Set<MapLocation> validLocs = new HashSet<MapLocation>();
    	
    	for (MapLocation loc : this.getLocsInSensorRange(10)) {
    		if (rc.senseTerrainTile(loc) == TerrainTile.NORMAL && !rc.isLocationOccupied(loc)) {
    			validLocs.add(loc);
    		}
    	}
    	
    	return validLocs;
    }
    
    private MapLocation pickBestFlashLoc() throws GameActionException {
    	MapLocation bestLoc = null;
    	int metric = 0;
    	
    	for (MapLocation loc : this.getAllValidFlashLocs()) {
    		int dist = loc.distanceSquaredTo(theirHQ);
    		
    		if (dist > metric) {
    			bestLoc = loc;
    			metric = dist;
    		}
    	}
    	
    	return bestLoc;
    }
}