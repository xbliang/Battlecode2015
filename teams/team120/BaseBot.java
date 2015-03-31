package team120;

import battlecode.common.*;

import java.util.*;

public class BaseBot {
	protected RobotController rc;
	protected MapLocation myHQ, theirHQ;
	protected Team myTeam, theirTeam;
	protected static Random rand;
	protected State state;
	protected MapLocation[] enemyTowers;
	protected MapLocation[] towerAttackOrder;

	// navigation helper
	protected BugNav nav;
	
	public static final int swarm = 500;
	public static final int swarm2 = 1700;

	public static final int numMinerFactoriesChannel = 0;
	public static final int numBarracksChannel = 1;
	public static final int numTankFactoriesChannel = 2;
	public static final int numBeaversChannel = 3;
	public static final int numMinersChannel = 4;
	public static final int numSoldiersChannel = 5;
	public static final int numTanksChannel = 6;
	public static final int numHelipadsChannel = 7;
	public static final int numDronesChannel = 8;
	public static final int numSupplyDepotsChannel = 9;
	public static final int numBashersChannel = 10;
	public static final int numTechInstChannel = 11;
	public static final int numTrainingFieldChannel = 12;
	public static final int numCommanderChannel = 13;
	
	public static final int numTanksSwarm1 = 50;
	public static final int numTanksSwarm2 = 51;
	public static final int numTanksSwarm3 = 52;

	public static final int junkChannel = 99;

	public static final int rally1XChannel = 100;
	public static final int rally1YChannel = 101;
	public static final int rally2XChannel = 102;
	public static final int rally2YChannel = 103;
	public static final int minXChannel = 104;
	public static final int maxXChannel = 105;
	public static final int minYChannel = 106;
	public static final int maxYChannel = 107;

	public static final int stratChannel = 200;
	public static final int dronesReady = 201;
	
	//
	// MAX UNIT NUMBERS
	//
	public static final int maxMinerFactories = 1;
	public static final int maxBarracks = 2;
	public static final int maxTankFactories = 3;
	public static final int maxTechInst = 0;
	public static final int maxTrainingField = 0;
	public static final int maxHelipads = 0;
	public static final int maxSupplyDepots = 2;
	// public static final int maxHandWash = Integer.MAX_VALUE;
	
	public static final int maxMiners = 10;
	public static final int maxBeavers = 2;
	public static final int maxDrones = 0;
	public static final int maxSoldiers = Integer.MAX_VALUE;
	public static final int maxTanks = Integer.MAX_VALUE;

	public BaseBot(RobotController rc) {
		this.rc = rc;
		this.myHQ = rc.senseHQLocation();
		this.theirHQ = rc.senseEnemyHQLocation();
		this.myTeam = rc.getTeam();
		this.theirTeam = this.myTeam.opponent();
		rand = new Random(rc.getID());
		this.enemyTowers = rc.senseEnemyTowerLocations();
		
		if (this.isNotABuilding(rc.getType())) {
			this.nav = new BugNav(rc); // going around towers
			
			try {
				this.goToHQForSupply();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void go() throws GameActionException {
		beginningOfTurn();
		execute();
		endOfTurn();
	}

	public void beginningOfTurn() throws GameActionException {
		/**
        if (rc.senseEnemyHQLocation() != null) {
            this.theirHQ = rc.senseEnemyHQLocation();
        }
		 */

		transferSupplies();
	}

	public void execute() throws GameActionException {

	}

	public void endOfTurn() throws GameActionException {

		rc.yield();
	}

	//
	// 	SUPPLY
	//

	private void transferSupplies() throws GameActionException {
		RobotInfo[] nearbyAllies = rc.senseNearbyRobots(rc.getLocation(),GameConstants.SUPPLY_TRANSFER_RADIUS_SQUARED,rc.getTeam());
		double lowestSupply = rc.getSupplyLevel();
		double transferAmount = 0;
		MapLocation suppliesToThisLocation = null;
		for(RobotInfo ri:nearbyAllies){
			if(ri.supplyLevel<lowestSupply){
				lowestSupply = ri.supplyLevel;
				transferAmount = (rc.getSupplyLevel()-ri.supplyLevel)/2;
				suppliesToThisLocation = ri.location;
			}
		}
		if(suppliesToThisLocation!=null){
			rc.transferSupplies((int)transferAmount, suppliesToThisLocation);
		}
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

	public Direction[] getDirectionsAwayFrom(MapLocation dest) {
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
		Direction[] dirs = getDirectionsAwayFrom(dest);
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
		return rc.senseNearbyRobots(rc.getType().attackRadiusSquared, theirTeam);
	}

	public void attackLeastHealthEnemy(RobotInfo[] enemies) throws GameActionException {
		if (enemies.length == 0) {
			return;
		}
		if (rc.isWeaponReady() && rc.isCoreReady()){
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

	public void moveRandomly() throws GameActionException {
		Direction facing;
		if (rand.nextDouble() < 0.25){
			facing = Direction.NORTH;
		}
		else if (rand.nextDouble() >= 0.25 && rand.nextDouble() < 0.5){
			facing = Direction.EAST;
		}
		else if (rand.nextDouble() >= 0.5 && rand.nextDouble() < 0.75 ){
			facing = Direction.SOUTH;
		}
		else {
			facing = Direction.WEST;
		}
		int i = 0;
		while (!rc.canMove(facing) && i < 8){
			facing = facing.rotateLeft();
			i++;
		}
		if (rc.isCoreReady()&&rc.canMove(facing)){
			rc.move(facing);
		}
	}

	// Use buildStructureAndBroadcast under beaver.java or spawnAndBroadcast pls
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

	// For now barracks only spawn soldiers...
	public void spawnAndBroadcast(RobotType type) throws GameActionException{
		RobotType spawnType = type;
		int channel = junkChannel;

		if (type == RobotType.SOLDIER) {
			channel = numSoldiersChannel;
		}
		else if (type == RobotType.BASHER){
			channel = numBashersChannel;
		}
		else if (type == RobotType.MINER) {
			channel = numMinersChannel;
		}
		else if (type == RobotType.TANK) {
			channel = numTanksChannel;
		}
		else if (type == RobotType.DRONE){
			channel = numDronesChannel;
		}
		else if (type == RobotType.BEAVER) {
			channel = numBeaversChannel;
		} else if (type == RobotType.COMMANDER) {
			channel = numCommanderChannel;
		}
		//System.out.println (rc.isCoreReady() + " " + (rc.getTeamOre()>=spawnType.oreCost));
		if (rc.isCoreReady() && rc.getTeamOre()>=spawnType.oreCost) {
			Direction newDir = getSpawnDirection(spawnType);
			if (newDir != null) {
				rc.spawn(newDir, spawnType);
				incrementBroadcast(channel);
			}
		}
	}

	public void print(String message){
		String input = message;
		System.out.println(input);
	}

	public void moveTowardsEnemyHQ() throws GameActionException {
		if (getDirectionsToward(this.theirHQ).length > 0) {
			Direction hqDir = getMoveDir(this.theirHQ);
			if (rc.isCoreReady() && rc.canMove(hqDir)){
				rc.move(hqDir);
			}
		}
	}

	public void moveTowardsLocation(MapLocation dest) throws GameActionException {
		if (getDirectionsToward(this.theirHQ).length > 0) {
			Direction locDir = getMoveDir(dest);
			if (rc.isCoreReady() && rc.canMove(locDir)){
				rc.move(locDir);
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

	public void attackMove() throws GameActionException {
		if (getEnemiesInAttackingRange().length > 0) {
			if (rc.isWeaponReady()){
				attackOptimized(getEnemiesInAttackingRange());
			}
		}
		else if (rc.isCoreReady()) {
			int rallyX = rc.readBroadcast(100);
			int rallyY = rc.readBroadcast(101);
			MapLocation rallyPoint = new MapLocation(rallyX, rallyY);

			Direction newDir = getMoveDir(rallyPoint);

			if (newDir != null) {
				rc.move(newDir);
			}
		}
	}

	public void attackWhileMovingTo(MapLocation dest) throws GameActionException {
		if (getEnemiesInAttackingRange().length > 0) {
			if (rc.isWeaponReady()){
				attackOptimized(getEnemiesInAttackingRange());
			}
		}
		nav.moveTo(dest, true);
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
		if (nearbyEnemies.length > 0) {
			for (int i = 0; i < nearbyEnemies.length; i++) {
				locs[i] = nearbyEnemies[i].location;
			}
		}
		return getAverageLocation(locs);
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

	public void incrementBroadcast(int channel) throws GameActionException {
		rc.broadcast(channel, rc.readBroadcast(channel)+1);
	}

	public Direction getMoveDirAvoidTowers(MapLocation loc) {
		MapLocation myLoc = rc.getLocation();

		Direction[] dirs = getDirectionsToward(loc);
		for (Direction d : dirs) {
			boolean safe = true;
			if (rc.canMove(d)) {            	
				for (int i = 0; i < this.enemyTowers.length + 1; i++) {
					MapLocation enemy = i == enemyTowers.length ? theirHQ : enemyTowers[i];

					if (myLoc.add(d).distanceSquaredTo(enemy) <= 24)
						safe = false;
				}
				if (safe)
					return d;
			}
		}

		return null;
	}

	public void goToHQForSupply() throws GameActionException {
		while (rc.getSupplyLevel() == 0 && rc.getLocation().distanceSquaredTo(myHQ) > 15) {
			this.tryToMoveToward(myHQ);
		}
	}

	public boolean isNotABuilding(RobotType type) {
		return type == RobotType.BASHER || type == RobotType.BEAVER
				|| type == RobotType.COMMANDER || type == RobotType.DRONE
				|| type == RobotType.MINER || type == RobotType.SOLDIER || type == RobotType.TANK;
	}

	public void chooseTowerAttackOrder() {
		this.towerAttackOrder = new MapLocation[this.enemyTowers.length + 1];
		int indexToFill = 0;
		
		List<MapLocation> towerLocs = new ArrayList<MapLocation>(Arrays.asList(this.enemyTowers));
		
		while(indexToFill < this.towerAttackOrder.length) {
			int bestMetric = Integer.MIN_VALUE;
			MapLocation bestLoc = null;
			
			for (MapLocation loc : towerLocs) {
				int thisMetric = 0;
				
				for (MapLocation loc2 : this.enemyTowers) {
					thisMetric += loc.distanceSquaredTo(loc2);
				}
				
				thisMetric += loc.distanceSquaredTo(theirHQ);
				thisMetric -= loc.distanceSquaredTo(myHQ);
				
				if (thisMetric >= bestMetric) {
					bestMetric = thisMetric;
					bestLoc = loc;
				}
			}
			
			towerLocs.remove(bestLoc);
			this.towerAttackOrder[indexToFill] = bestLoc;
			indexToFill++;
		}
		
		this.towerAttackOrder[this.towerAttackOrder.length - 1] = theirHQ;
	}
	
}