// Uses broadcasting channels 0, 1, 2, 3, 4, 100

package team120;

import battlecode.common.*;

public class HQ extends BaseBot {
	public static int xMin, xMax, yMin, yMax;
	public static int xPos, yPos;
	public static int totalNormal, totalVoid, totalProcessed;
	public static int towerThreat;
	public static boolean hasBroadcastedMapCoords;

	// percent non-void terrain
	public static double ratio;
	// map analysis finished?
	public static boolean isFinished;

	public static int numSoldiers, numMinerFactories, numBeavers, numBarracks, numTanks;
	public static int numTankFactories, numMiners, numHelipads, numDrones;
	public static int numSupplyDepots, numBashers, numTechInst, numTrainingField, numCommander;

	public HQ(RobotController rc){
		super(rc);     
		xMin = Math.min(this.myHQ.x, this.theirHQ.x);
		yMin = Math.min(this.myHQ.y, this.theirHQ.y);
		xMax = Math.max(this.myHQ.x, this.theirHQ.x);
		yMax = Math.max(this.myHQ.y, this.theirHQ.y);

		xPos = xMin;
		yPos = yMin;

		totalNormal = totalVoid = totalProcessed = 0;
		isFinished = false;
		
		// populate BaseBot.towerAttackOrder with enemy towers
		this.chooseTowerAttackOrder();
		for (int i = 0; i < this.towerAttackOrder.length; i++) {
			System.out.println(this.towerAttackOrder[i]);
		}
		hasBroadcastedMapCoords = false;
		
	}

	/////////////////////////////////////// HELPER FUNCITONS //////////////////////////////////////////

	// analyzeMap on rectangle bounded by both HQ
	// continue until a certain number of squares have been processed
	// or a percentage have been processed
	// (takes the minimum of the two)
	// or we have reached round roundNum
	public void analyzeMap(int procSquares, double procPercent, int roundNum){
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
			if ((totalProcessed < Math.min(procSquares, (xMax-xMin)*(yMax-yMin)*procPercent) && 
					Clock.getRoundNum() < roundNum) && yPos >= yMax+1){
				yPos = yMin;
				xPos = xMin;
				totalNormal = 0;
				totalProcessed = 0;
			}
			if (Clock.getBytecodesLeft() < 500) {
				return;
			}
		}        		
		ratio = (double)totalNormal/totalProcessed;
		isFinished = true;  
	}

	/*    public void analyzeTowers(){
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
    }*/

	public void decideAndBroadcastStrategy() throws GameActionException {
		int strategy;
		if (ratio > 0.85) {
			strategy = Strategy.TANKS.value;
		}
		/**
		else if (ratio > 0.3) {
			strategy = Strategy.TANKS_DRONES.value;
		}
		 */
		else{
			//strategy = Strategy.DRONES.value;
			strategy = Strategy.TANKS.value;
		}
		rc.broadcast(stratChannel, strategy);
		// System.out.println(rc.readBroadcast(stratChannel));
	}

	public void broadcastAllyCount() throws GameActionException {
		numSoldiers = 0;
		numMinerFactories = 0;
		numBeavers = 0;
		numBarracks = 0;
		numTanks = 0;
		numTankFactories = 0;
		numMiners = 0;
		numHelipads = 0;
		numDrones = 0;
		numSupplyDepots = 0;
		numBashers = 0;
		numTechInst = 0;
		numTrainingField = 0;
		numCommander = 0;
		RobotInfo[] myRobots = getAllies();

		for (RobotInfo r : myRobots) {
			RobotType type = r.type;
			if (type == RobotType.MINERFACTORY) {
				numMinerFactories++;
			} else if (type == RobotType.BARRACKS) {
				numBarracks++;
			} else if (type == RobotType.TANKFACTORY) {
				numTankFactories++;
			} else if (type == RobotType.BEAVER) {
				numBeavers++;
			} else if (type == RobotType.MINER) {
				numMiners++;
			} else if (type == RobotType.SOLDIER) {
				numSoldiers++;
			} else if (type == RobotType.TANK) {
				numTanks++;
			} else if (type == RobotType.HELIPAD) {
				numHelipads++;
			} else if (type == RobotType.DRONE){
				numDrones++;
			} else if (type == RobotType.SUPPLYDEPOT){
				numSupplyDepots ++;
			} else if (type == RobotType.BASHER){
				numBashers ++;
			} else if (type == RobotType.TECHNOLOGYINSTITUTE) {
				numTechInst++;
			} else if (type == RobotType.TRAININGFIELD) {
				numTrainingField++;
			} else if (type == RobotType.COMMANDER) {
				numCommander++;
			}

		}	
		rc.broadcast(numMinerFactoriesChannel, numMinerFactories);
		rc.broadcast(numBarracksChannel, numBarracks);
		rc.broadcast(numTankFactoriesChannel, numTankFactories);
		rc.broadcast(numBeaversChannel, numBeavers);
		rc.broadcast(numMinersChannel, numMiners);
		rc.broadcast(numSoldiersChannel, numSoldiers);
		rc.broadcast(numTanksChannel, numTanks);
		rc.broadcast(numHelipadsChannel, numHelipads);
		rc.broadcast(numDronesChannel, numDrones);
		rc.broadcast(numSupplyDepotsChannel, numSupplyDepots);
		rc.broadcast(numBashersChannel, numBashers);
		rc.broadcast(numTechInstChannel, numTechInst);
		rc.broadcast(numTrainingFieldChannel, numTrainingField);
		rc.broadcast(numCommanderChannel, numCommander);
	}

	public void decideAndBroadcastRallyPoint() throws GameActionException {
		//        MapLocation rallyPoint = new MapLocation(rc.readBroadcast(rally1XChannel), rc.readBroadcast(rally1YChannel));
		//        if ((numSoldiers +numTanks) < 5) {
		//            rallyPoint = new MapLocation( this.myHQ.x + (this.theirHQ.x - this.myHQ.x) / 4,
		//                                          this.myHQ.y + (this.theirHQ.y - this.myHQ.y) / 4);
		//        }
		//        else if ((numSoldiers+numTanks) > 20){
		//            rallyPoint = this.theirHQ;
		//        }
		MapLocation rallyPoint1 = null;
		//MapLocation rallyPoint2 = null;

		if (Clock.getRoundNum() < swarm) {
			rallyPoint1 = new MapLocation( this.myHQ.x + (this.theirHQ.x - this.myHQ.x) / 4,
					this.myHQ.y + (this.theirHQ.y - this.myHQ.y) / 4);
		//	rallyPoint2 = new MapLocation( this.myHQ.x + (this.theirHQ.x - this.myHQ.x) / 4,
		//			this.myHQ.y + (this.theirHQ.y - this.myHQ.y) / 4);
		//} else if (Clock.getRoundNum() < swarm2) {
		//	rallyPoint1 = this.theirHQ;
		//	rallyPoint2 = new MapLocation( this.myHQ.x + (this.theirHQ.x - this.myHQ.x) / 4,
		//			this.myHQ.y + (this.theirHQ.y - this.myHQ.y) / 4);
		} else {
			int remainingTowers = rc.senseEnemyTowerLocations().length;
			
			rallyPoint1 = this.towerAttackOrder[this.towerAttackOrder.length - remainingTowers - 1];
			//rallyPoint2 = this.theirHQ;
		}

		rc.broadcast(rally1XChannel, rallyPoint1.x);
		rc.broadcast(rally1YChannel, rallyPoint1.y);

		//rc.broadcast(rally2XChannel, rallyPoint2.x);
		//rc.broadcast(rally2YChannel, rallyPoint2.y);
	}




	///////////////////////////////////////// EXECUTE ////////////////////////////////////////////   

	public void execute() throws GameActionException {
		if (!hasBroadcastedMapCoords){
			rc.broadcast(minXChannel, Math.min(myHQ.x, theirHQ.x));
			rc.broadcast(minYChannel, Math.min(myHQ.y, theirHQ.y));
			rc.broadcast(maxXChannel, Math.max(myHQ.x, theirHQ.x));
			rc.broadcast(maxYChannel, Math.max(myHQ.y, theirHQ.y));
			hasBroadcastedMapCoords = true;
		}
		
		// Broadcast every 5 rounds to save on byte cost
		if (Clock.getRoundNum() % 5 == 0){
			broadcastAllyCount();
		}

		// Attacks enemies
		attackLeastHealthEnemy(getEnemiesInAttackingRange());

		// Builds beaver(s)
		if (numBeavers < this.maxBeavers) {
			spawnAndBroadcast(RobotType.BEAVER);
		}

		decideAndBroadcastRallyPoint();

		// Analyze map and then broadcast strategy
/*		if (!isFinished) {
			analyzeMap(400, 0.40, 300);
			    		analyzeTowers();
		}
		else{
			decideAndBroadcastStrategy();
		}*/
	}
}