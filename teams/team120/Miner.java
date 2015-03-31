package team120;

import battlecode.common.*;

import java.util.*;

public class Miner extends BaseBot {
	private MapLocation goAwayFromHere;

	public Miner(RobotController rc) {
		super(rc);
		state = State.MINING;
	}
	
	public MapLocation findNearbyLocWithMostOre() throws GameActionException {
		Set<MapLocation> locsInRange = this.getLocsInSensorRange(RobotType.MINER.sensorRadiusSquared);

		double maxOre = 0;
		MapLocation maxOreLocation = rc.getLocation();

		for (MapLocation loc : locsInRange) {
			double thisOre = rc.senseOre(loc);

			if (thisOre > maxOre && !rc.isLocationOccupied(loc)) {
				maxOre = thisOre;
				maxOreLocation = loc;
			}
		}

		if (maxOre == 0)
			return rc.getLocation();
		return maxOreLocation;
	}

	public void execute() throws GameActionException {
		RobotInfo[] nearbyEnemies = this.getEnemiesInAttackingRange();

		// Move unless ore > 4 and there are no miners nearby
/*		String oreAmt = String.valueOf(rc.senseOre(rc.getLocation()));
		rc.setIndicatorString(1, oreAmt);*/
		if (rc.getLocation().distanceSquaredTo(theirHQ) < rc.getLocation().distanceSquaredTo(myHQ)){
			state = State.TOO_FAR;
		}
		else if (rc.senseOre(rc.getLocation()) < 2){
			state = State.MOVING_TOWARD;
		}
		else{
			state = State.MINING;		
		}

		if (nearbyEnemies.length > 0) {
			if (shouldRetreat())
				state = State.RETREATING;
			else
				state = State.ATTACKING;
		}

		// do the thing
		if (state == State.RETREATING) {
			tryToMoveAway(getAverageEnemyLocation());
		} else if (state == State.ATTACKING) {
			this.attackLeastHealthEnemy(this.getEnemiesInAttackingRange());
		} else if (rc.isCoreReady()) {
			if (state == State.TOO_FAR) {
				moveTowardsLocation(myHQ);
				rc.setIndicatorString(2, "moving towards myHQ???");
			} else if (state == State.MINING && rc.canMine()) {
				rc.mine();
			} else if (state == State.MOVING_TOWARD) {
				MapLocation goHere = findNearbyLocWithMostOre();
				nav.moveTo(goHere, true);

/*				else {
					state = State.MOVING_AWAY;
					goAwayFromHere = rc.getLocation();
				}
			} else if (state == State.MOVING_AWAY) {
				if (rc.getLocation().distanceSquaredTo(goAwayFromHere) >= 6)
					state = State.MOVING_TOWARD;*/
			}
		}
	}


}