package team120;

import battlecode.common.*;

public class Beaver extends BaseBot {
    public Beaver(RobotController rc) {
        super(rc);
    }
    
    public void buildStructureAndBroadcast(RobotType type) throws GameActionException{
    	if (rc.isCoreReady() && rc.getTeamOre() > type.oreCost){
    		Direction buildDir = getBuildDirection(type);
    		if (buildDir != null && rc.canBuild(buildDir, type) && this.locOkForCheckerboard(rc.getLocation().add(buildDir))){
    			rc.setIndicatorString(1, "Building");
    			rc.build(buildDir, type);
    			if (type == RobotType.MINERFACTORY){
        			incrementBroadcast(numMinerFactoriesChannel);
    			}
    			else if (type == RobotType.BARRACKS){
    				incrementBroadcast(numBarracksChannel);
    			}
    			else if (type == RobotType.TANKFACTORY){
    				incrementBroadcast(numTankFactoriesChannel);
    			}
    			else if (type == RobotType.HELIPAD){
    				incrementBroadcast(numHelipadsChannel);
    			} else if (type == RobotType.TECHNOLOGYINSTITUTE) {
    				incrementBroadcast(numTechInstChannel);
    			} else if (type == RobotType.TRAININGFIELD) {
    				incrementBroadcast(numTrainingFieldChannel);
    			}
    		}
    	}
    }
    
    // Builds structures close to HQ, better defense + supply transfer
    
    public void buildAndMoveTowardsHQ(RobotType type) throws GameActionException{

    	if (rc.getLocation().distanceSquaredTo(myHQ) < 20) {
    		buildStructureAndBroadcast(type);
    		return;
    	}
    	else {
        	nav.moveTo(myHQ, true);
    	}
    }

    public void execute() throws GameActionException {
    	
    	// Whether the first, second, third... steps are done for beaver
    	boolean firstCondition = false;
    	boolean secondCondition = false;
    	boolean thirdCondition = false;
    	boolean fourthCondition = false;
    	
    	if (rc.readBroadcast(numMinerFactoriesChannel) >= this.maxMinerFactories){
    		firstCondition = true;
    	} if (rc.readBroadcast(numHelipadsChannel) >= this.maxHelipads) {
    		secondCondition = true;
    	} if (rc.readBroadcast(numBarracksChannel) >= this.maxBarracks) {
    		thirdCondition = true;
    	} if (rc.readBroadcast(numTrainingFieldChannel) >= this.maxTrainingField) {
    		fourthCondition = true;
    	}
    	
    	boolean mostConditions = firstCondition && secondCondition && thirdCondition;
    	boolean allConditions = mostConditions && fourthCondition;
    	
		rc.setIndicatorString(0, String.valueOf(firstCondition));
		rc.setIndicatorString(1, String.valueOf(secondCondition));
		rc.setIndicatorString(2, String.valueOf(thirdCondition));
		rc.setIndicatorString(3, String.valueOf(fourthCondition));
    	
    	// make sure we have miners
    	if (rc.readBroadcast(numMinerFactoriesChannel) < this.maxMinerFactories) {
    		buildAndMoveTowardsHQ(RobotType.MINERFACTORY);
		} else if (firstCondition && rc.readBroadcast(numHelipadsChannel) < this.maxMinerFactories){
    		buildAndMoveTowardsHQ(RobotType.HELIPAD);
    	} else if (firstCondition && rc.readBroadcast(numBarracksChannel) < this.maxBarracks){
			buildAndMoveTowardsHQ(RobotType.BARRACKS);
		} else if (firstCondition && thirdCondition && rc.readBroadcast(numTankFactoriesChannel) < this.maxTankFactories) {
			buildAndMoveTowardsHQ(RobotType.TANKFACTORY);
		} else if (mostConditions && rc.readBroadcast(numTechInstChannel) < this.maxTechInst) {
			buildAndMoveTowardsHQ(RobotType.TECHNOLOGYINSTITUTE);
		} else if (mostConditions && rc.readBroadcast(numTrainingFieldChannel) < this.maxTrainingField) {
			buildAndMoveTowardsHQ(RobotType.TRAININGFIELD);
		} else if (allConditions && rc.readBroadcast(numSupplyDepotsChannel) < this.maxSupplyDepots) {
			buildAndMoveTowardsHQ(RobotType.SUPPLYDEPOT);
		}
    	
    	if (Clock.getRoundNum() % 20 == 0)
    		moveRandomly();
    	
/*    	// Initial value, haven't decided on strategy yet
    	if (rc.readBroadcast(stratChannel)== Strategy.NONE.value) {
    		
    		if (rc.readBroadcast(numBarracksChannel) < 1) {
    			rc.setIndicatorString(1, "building");
    			buildStructureAndBroadcast(RobotType.BARRACKS);
    		}
    		
    		// Build drones and start rushing
    		else if (rc.readBroadcast(numHelipadsChannel) < 2) {
    			buildStructureAndBroadcast(RobotType.HELIPAD);
    		}
    		
    		// Mine and move if not enough ore
    		if (rc.senseOre(rc.getLocation()) > 3 && rc.getLocation().distanceSquaredTo(this.myHQ)>5 && rc.isCoreReady()){
    			rc.setIndicatorString(1, "mining");
    			rc.mine();
    		}
    		else{
    			rc.setIndicatorString(1, "moving");
    			moveRandomly();
    		}   		
    	}
    	// Should build tanks
    	else if (rc.readBroadcast(stratChannel) == Strategy.TANKS.value) {
    		
    		if (rc.readBroadcast(numBarracksChannel) == 0) {
    			buildStructureAndBroadcast(RobotType.BARRACKS);
    		}
    		else if (rc.readBroadcast(numTankFactoriesChannel) < 4) {
    			buildStructureAndBroadcast(RobotType.TANKFACTORY);
    		}
    		moveRandomly();
    	}
    	
    	// Mixed strategy
    	*//**
    	else if (rc.readBroadcast(stratChannel) == Strategy.TANKS_DRONES.value){
    		buildStructureAndBroadcast(RobotType.BARRACKS);
    		moveRandomly();
        }
        *//*
    	
    	// Should build drones
    	else if (rc.readBroadcast(stratChannel) == Strategy.DRONES.value) {
    		if (rc.readBroadcast(numTankFactoriesChannel) < 4)
    			buildStructureAndBroadcast(RobotType.HELIPAD);
    		moveRandomly();
    	}
        rc.yield();*/
    }
    
    private boolean locOkForCheckerboard(MapLocation loc) {
    	return (loc.x + loc.y) % 2 == 0;
    }
}