package maddieplayer;

import battlecode.common.*;

import java.util.*;

public class BaseBot {
    protected RobotController rc;
    protected MapLocation myHQ, theirHQ;
    protected Team myTeam, theirTeam;
    protected MapLocation myAvgTowerLoc, theirAvgTowerLoc;
    protected Random rand;

    public BaseBot(RobotController rc) {
        this.rc = rc;
        this.myHQ = rc.senseHQLocation();
        this.theirHQ = rc.senseEnemyHQLocation();
        this.myTeam = rc.getTeam();
        this.theirTeam = this.myTeam.opponent();
        this.rand = new Random(rc.getID());
        
        try {
        	readLocsFromBroadcast();
        } catch(Exception e) {
        	e.printStackTrace();
        }
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
    
    public Direction[] getOppDirectionsToward(MapLocation dest) {
        Direction toDest = rc.getLocation().directionTo(dest).opposite();
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
    
    public Direction getOppMoveDir(MapLocation dest) {
        Direction[] dirs = getOppDirectionsToward(dest);
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

        if (rc.canAttackLocation(toAttack))
        	rc.attackLocation(toAttack);
    }
    
    public boolean isLocInSensorRange(int sensorRadSq, MapLocation otherLoc) {
    	MapLocation myLoc = rc.getLocation();
    	return Math.pow(myLoc.x - otherLoc.x, 2) + Math.pow(myLoc.y - otherLoc.y, 2) <= sensorRadSq;
    }
    
    public Set<MapLocation> getLocsInSensorRange(int sensorRadSq) {
    	int sensorRadFloor = (int)Math.sqrt(sensorRadSq);
    	
    	Set<MapLocation> locs = new HashSet<MapLocation>();
    	
    	MapLocation myLoc = rc.getLocation();
    	
    	for(int i = -sensorRadFloor; i <= sensorRadFloor; i++) {
    		for (int j = -sensorRadFloor; j <= sensorRadFloor; j++) {
    			MapLocation thisLoc = new MapLocation(myLoc.x + i, myLoc.y + j);
    			
    			if (isLocInSensorRange(sensorRadSq, thisLoc))
    				locs.add(thisLoc);
    		}
    	}
    	
    	return locs;
    }
    
    public Direction[] getAllDirs() {
    	Direction[] dirs = new Direction[8];
    	Direction currDir = Direction.NORTH;
    	    	
    	for (int i = 0; i < 8; i++) {
    		dirs[0] = currDir;
    		currDir = currDir.rotateLeft();
    	}
    	
    	return dirs;
    }
    
    public void tryToMoveAway(MapLocation badLoc) throws GameActionException {
		Direction oppDir = getOppMoveDir(badLoc);

		if (oppDir != null && rc.canMove(oppDir) && rc.isCoreReady())
			rc.move(oppDir);
	}
    
    public void tryToMoveToward(MapLocation goHere) throws GameActionException {
    	Direction toDir = getMoveDir(goHere);
    	
    	if (toDir != null && rc.canMove(toDir) && rc.isCoreReady())
    		rc.move(toDir);
    }
    
    public boolean shouldRetreat() {
    	RobotInfo[] nearbyFriends = rc.senseNearbyRobots(rc.getType().sensorRadiusSquared, this.myTeam);
    	RobotInfo[] nearbyEnemies = rc.senseNearbyRobots(rc.getType().sensorRadiusSquared, this.theirTeam);
    	
    	double alliedHealth = 0;
    	double enemyHealth = 0;
    	
    	for (int i = 0; i < nearbyFriends.length; i++) {
    		alliedHealth += nearbyFriends[i].health;
    	}
    	
    	for (int j = 0; j < nearbyEnemies.length; j++) {
    		enemyHealth += nearbyEnemies[j].health;
    	}
    	
    	return alliedHealth < enemyHealth;
    }
    
    public MapLocation getAverageLocation(MapLocation[] locs) {
    	int sumX = 0;
    	int sumY = 0;
    	
    	for (MapLocation loc : locs) {
    		sumX += loc.x;
    		sumY += loc.y;
    	}
    	
    	int aveX = Math.round(sumX/locs.length);
    	int aveY = Math.round(sumY/locs.length);
    	
    	return new MapLocation(aveX, aveY);
    }
    
    public MapLocation getAverageEnemyLocation() {
    	RobotInfo[] nearbyEnemies = rc.senseNearbyRobots(rc.getType().sensorRadiusSquared, this.theirTeam);
    	
    	MapLocation[] locs = new MapLocation[nearbyEnemies.length];
    	
    	for (int i = 0; i < nearbyEnemies.length; i++) {
    		locs[i] = nearbyEnemies[i].location;
    	}
    	
    	return getAverageLocation(locs);
    }
    
    public MapLocation getAverageTowerLocation(Team team) {
    	MapLocation[] towerLocs;
    	
    	if (team.equals(theirTeam))
    		towerLocs = rc.senseEnemyTowerLocations();
    	else
    		towerLocs = rc.senseTowerLocations();
    	
    	return getAverageLocation(towerLocs);
    }
    
    public MapLocation getAverageLocationOfTowersAndHQ() {    	
    	MapLocation[] towerLocs = rc.senseTowerLocations();
    	
    	MapLocation[] locs = new MapLocation[towerLocs.length + 1];
    	
    	for (int i = 0; i < towerLocs.length; i++) {
    		locs[i] = towerLocs[i];
    	}
    	
    	locs[locs.length - 1] = myHQ;
    	
    	return getAverageLocation(locs);
    }
    
    public void readLocsFromBroadcast() throws GameActionException {
    	int myTowerX = rc.readBroadcast(0);
    	int myTowerY = rc.readBroadcast(1);
    	int theirTowerX = rc.readBroadcast(2);
    	int theirTowerY = rc.readBroadcast(3);
    	
    	myAvgTowerLoc = new MapLocation(myTowerX, myTowerY);
    	theirAvgTowerLoc = new MapLocation(theirTowerX, theirTowerY);
    }
    
    public void tryToMoveInRandomDirection() throws GameActionException {
    	List<Direction> dirs = new ArrayList<Direction>(Arrays.asList(Direction.values()));
    	
    	while (dirs.size() > 0) {
    		int index = rand.nextInt(dirs.size());
    		Direction dir = dirs.get(index);
    		
    		if (dir.equals(Direction.NONE) || dir.equals(Direction.OMNI) || !rc.canMove(dir))
    			dirs.remove(index);
    		else {
    			rc.move(dir);
    			break;
    		}
    	}    	
    }
}