//uses broadcasting channels 5-7: 5 for building structures, 6 is empty
//7 for keeping track of how many beavers we have

package justineplayer;

import battlecode.common.*;

import java.util.*;

public class RobotPlayer {
	
	static Direction facing = Direction.NORTH;
	static Random rand;
	
	public static void run(RobotController rc) {
        BaseBot myself;
        
        rand = new Random(rc.getID());
        if (rc.getType() == RobotType.HQ) {
            myself = new HQ(rc);
        } else if (rc.getType() == RobotType.BEAVER) {
            myself = new Beaver(rc);
        } else if (rc.getType() == RobotType.MINERFACTORY) {
            myself = new MinerFactory(rc);
        } else if (rc.getType() == RobotType.MINER) {
            myself = new Miner(rc);
        } else if (rc.getType() == RobotType.BARRACKS) {
            myself = new Barracks(rc);
        } else if (rc.getType() == RobotType.SOLDIER) {
            myself = new Soldier(rc);
        } else if (rc.getType() == RobotType.TOWER) {
            myself = new Tower(rc);
        } else if (rc.getType() == RobotType.BASHER) {
        	myself=new Basher(rc);
        } else if (rc.getType() == RobotType.TANKFACTORY){
        	myself = new TankFactory(rc);
        } else if (rc.getType() == RobotType.TANK){
        	myself = new Tank(rc);
        } else {
            myself = new BaseBot(rc);
        }

        while (true) {
            try {
                myself.go();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
	}

    public static class BaseBot {
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
        
        public void spawn(RobotType type) throws GameActionException {
        	if (rc.isCoreReady() && rc.canSpawn(Direction.NORTH, type)){
        		rc.spawn(Direction.NORTH, type);
        	}
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
        
        public void build(RobotType type) throws GameActionException{
        	Direction builddir = getBuildDirection(type);
    		if (rc.isCoreReady() && rc.canBuild(builddir, type)){
        		rc.build(builddir, type);
        	}
        }
        
        public void buildAndBroadcast(RobotType type, int channel, int data) throws GameActionException{
        	Direction builddir = getBuildDirection(type);
        	if (getBuildDirection(type) != null){
        		if (rc.isCoreReady() && rc.canBuild(builddir, type)){
            		rc.build(builddir, type);
            		rc.broadcast(channel, data);
            	}
        	}
    		return;
        }

        public RobotInfo[] getAllies() {
            RobotInfo[] allies = rc.senseNearbyRobots(Integer.MAX_VALUE, myTeam);
            return allies;
        }
        
        public void print(String message){
        	String input = message;
        	System.out.println(input);
        }

        public RobotInfo[] getEnemiesInAttackingRange() {
            RobotInfo[] enemies = rc.senseNearbyRobots(RobotType.SOLDIER.attackRadiusSquared, theirTeam);
            return enemies;
        }
        
        public RobotInfo[] getEnemiesInAnyAttackingRange(RobotType type) {
        	RobotInfo[] enemies = rc.senseNearbyRobots(type.attackRadiusSquared, theirTeam);
        	return enemies;
        }

        public void moveRandomly() throws GameActionException {
    		if (rand.nextDouble() < 0.3){
    			facing = facing.rotateRight();
    		}
    		else if (rand.nextDouble() >= 0.3 && rand.nextDouble() < 0.6){
    			facing = facing.rotateLeft();
    		}
    		if (rc.isCoreReady()&&rc.canMove(facing)){
    			rc.move(facing);
    		}
        }
        
        
        public void moveTowardsEnemyHQ() throws GameActionException {
        	if (getDirectionsToward(this.theirHQ).length > 0) {
        		Direction hqDir = getMoveDir(this.theirHQ);
        		if (rc.isCoreReady() && rc.canMove(hqDir)){
        			rc.move(hqDir);
        		}
    		}
        }
        
        public void moveTowardsCenter() throws GameActionException {
        	if (getMoveDir(this.theirHQ) != null) {
        		Direction centerMaybe = getMoveDir(this.myHQ.add(getMoveDir(this.theirHQ), 100));
        		if (rc.isCoreReady() && rc.canMove(centerMaybe)){
        			rc.move(centerMaybe);
        		}
    		}
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
            if (rc.isWeaponReady() && rc.canAttackLocation(toAttack)){
                rc.attackLocation(toAttack);
            }
        }

        public void beginningOfTurn() {
            if (rc.senseEnemyHQLocation() != null) {
                this.theirHQ = rc.senseEnemyHQLocation();
            }
        }

        public void endOfTurn() {
        }

        public void go() throws GameActionException {
            beginningOfTurn();
            execute();
            endOfTurn();
        }

        public void execute() throws GameActionException {
            rc.yield();
        }
    }

    public static class HQ extends BaseBot {
    	public static int xMin, xMax, yMin, yMax;
    	public static int xPos, yPos;
    	public static int totalNormal, totalVoid, totalProcessed;
    	public static int towerThreat;
    	
    	public static double ratio;
    	public static boolean isFinished;
    	
        public HQ(RobotController rc) {
            super(rc);
            
            xMin = Math.min(this.myHQ.x, this.theirHQ.x);
            yMin = Math.min(this.myHQ.y, this.theirHQ.y);
            xMax = Math.max(this.myHQ.x, this.theirHQ.x);
            yMax = Math.max(this.myHQ.y, this.theirHQ.y);
            
            xPos = xMin;
            yPos = yMin;
            
            totalNormal = totalVoid = totalProcessed = 0;
            isFinished = false;
        }
        
        public void analyzeMap(){
        	while (yPos < yMax + 1) {
        		TerrainTile t = rc.senseTerrainTile(new MapLocation(xPos, yPos));
        		if (t == TerrainTile.NORMAL){
        			totalNormal++ ;
        			totalProcessed++ ;
        		}
        		else if (t == TerrainTile.VOID){
        			totalVoid++ ;
        			totalProcessed++;
        		}
        		xPos++;
        		if (xPos == xMax +1){
        			xPos = xMin;
        			yPos++;
        		}
        		//keeps recalculating ratio until we have processed enough  
        		//make proportion of map size
        		if (totalProcessed < 800 && yPos >= yMax+1){
        			yPos = yMin;
        			xPos = xMin;
        			if (Clock.getRoundNum()%50 == 0){
        				System.out.println(totalProcessed);
        				System.out.println(totalNormal);
        				System.out.println((double)totalNormal/totalProcessed);
        			}
        			totalNormal = 0;
        			totalProcessed = 0;
        		}
        		if (Clock.getBytecodesLeft() < 500) {
        			return;
        		}
       		}        		
        	ratio = (double)totalNormal/totalProcessed;
        	System.out.println(ratio);
			isFinished = true;  
        }
        
        public void analyzeTowers(){
        	MapLocation[] towers = rc.senseEnemyTowerLocations();
        	towerThreat = 0;
        	for (int i=0; i<towers.length; ++i){
        		MapLocation towerLoc = towers[i];
        		if ((xMin <= towerLoc.x && towerLoc.x <= xMax && yMin <= towerLoc.y && 
        				towerLoc.y <= yMax) || 
        				towerLoc.distanceSquaredTo(this.theirHQ) <= 50){
        			for (int j=0; j<towers.length; ++j){
        				if (towers[j].distanceSquaredTo(towerLoc) <= 50) {
        					towerThreat++;
        				}
        			}
        		}
        	}
        }

        public void execute() throws GameActionException {
        	
        	if (getSpawnDirection(RobotType.BEAVER)!= null && rc.isCoreReady()){
        		// No more than ~20 Beavers
        		if (rc.readBroadcast(7) < 20){
    	        	rc.spawn(getSpawnDirection(RobotType.BEAVER), RobotType.BEAVER);
    	        	// Broadcasts when a new beaver is made
    	        	rc.broadcast(7, rc.readBroadcast(7)+1);
        		}
        	}
        	if (!isFinished) {
        		analyzeMap();
        		analyzeTowers();
        	}
        	rc.yield();
        }
    }

    public static class Beaver extends BaseBot {
        public Beaver(RobotController rc) {
            super(rc);
        }

        public void execute() throws GameActionException {
        	
        	// Broadcasts when about to die
        	if (rc.getHealth() < 10){
        		rc.broadcast(7, rc.readBroadcast(7)-1);
        	}
       	
        	// Broadcast channel 5 is 1 if Miner factory is built
        	if (getEnemiesInAnyAttackingRange(RobotType.BEAVER).length > 0) {
        		attackLeastHealthEnemy(getEnemiesInAnyAttackingRange(RobotType.BEAVER));
        	}
        	// Waits for channel 5 = 1, then builds 4 barracks
        	else if (rc.getTeamOre() > RobotType.BARRACKS.oreCost && rc.readBroadcast(5) > 0 &&
        			rc.readBroadcast(5) < 4) {
        		buildAndBroadcast(RobotType.BARRACKS, 5, rc.readBroadcast(5)+1);
        	}
        	else if (rc.getTeamOre() > RobotType.MINERFACTORY.oreCost && rc.readBroadcast(5) == 0){
        		buildAndBroadcast(RobotType.MINERFACTORY, 5, 1);
        	}
        	else if (rc.getTeamOre() > RobotType.TANKFACTORY.oreCost && rc.readBroadcast(5) >= 4){
        		buildAndBroadcast(RobotType.TANKFACTORY, 5, rc.readBroadcast(5));
        	}
        	if (rc.senseOre(rc.getLocation())>4 && rc.isCoreReady() && rc.canMine()) {
        		rc.mine();
        	} 
        	else{
        		if (rc.getID()%2 == 1){
        			moveTowardsCenter();
        		}
        		else{
        			moveRandomly();
        		}
        	}
            rc.yield();
        }
    }
    public static class MinerFactory extends BaseBot {
    	public MinerFactory(RobotController rc) {
    		super(rc);
    	}
    	
    	public void execute() throws GameActionException {
    		if (Clock.getRoundNum() < 600){
            	spawn(RobotType.MINER);	
    		}
    		rc.yield();
    	}
    }
    public static class Miner extends BaseBot {
        public Miner(RobotController rc) {
            super(rc);
        }
        public void execute() throws GameActionException {
        	if (rc.senseOre(rc.getLocation())>2 && rc.isCoreReady() && rc.canMine()) {
        		rc.mine();
        	}
        	else{
        		moveRandomly();
        		//moveTowardsEnemyHQ();
        	}
        	rc.yield();
        }
    }
    
    public static class Barracks extends BaseBot {
        public Barracks(RobotController rc) {
            super(rc);
        }

        public void execute() throws GameActionException {
        	if (rand.nextDouble()<0.5){
        		spawn(RobotType.SOLDIER);
        	}
        	else {
        		spawn(RobotType.BASHER);
        	}
            rc.yield();
        }
    }

    public static class Soldier extends BaseBot {
        public Soldier(RobotController rc) {
            super(rc);
        }
        public void execute() throws GameActionException {
        	if (Clock.getRoundNum()> 1600) {
        		moveTowardsEnemyHQ();
        	}
        	else {
        		moveRandomly();
        		//moveTowardsEnemyHQ();
        	}
        	attackLeastHealthEnemy(getEnemiesInAnyAttackingRange(RobotType.SOLDIER));
        	if (rc.canAttackLocation(this.theirHQ) && rc.isWeaponReady()){
        		rc.attackLocation(this.theirHQ);
        	}
        	rc.yield();
        }

    }
   
    public static class Tower extends BaseBot {
        public Tower(RobotController rc) {
            super(rc);
        }
        public void execute() throws GameActionException {
            RobotInfo[] nearbyEnemies = getEnemiesInAnyAttackingRange(RobotType.TOWER);
        	attackLeastHealthEnemy(nearbyEnemies);
            rc.yield();
        }
    }
    
    public static class Basher extends BaseBot {
        public Basher(RobotController rc) {
            super(rc);
        }
        public void execute() throws GameActionException {
        	if (Clock.getRoundNum()> 1600) {
        		moveTowardsEnemyHQ();
        	}
        	else {
        		moveRandomly();
        		//moveTowardsEnemyHQ();
        	}
        	attackLeastHealthEnemy(getEnemiesInAnyAttackingRange(RobotType.BASHER));
        	if (rc.canAttackLocation(this.theirHQ) && rc.isWeaponReady()){
        		rc.attackLocation(this.theirHQ);
        	}
            rc.yield();
        }
    }
    public static class TankFactory extends BaseBot {
        public TankFactory(RobotController rc) {
            super(rc);
        }
        public void execute() throws GameActionException {
        	spawn(RobotType.TANK);
            rc.yield();
        }
    }
    public static class Tank extends BaseBot {
        public Tank(RobotController rc) {
            super(rc);
        }
        public void execute() throws GameActionException {
        	moveRandomly();
            rc.yield();
        }
    }
}
