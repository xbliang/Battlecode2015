package maddieplayer;

import battlecode.common.*;
import java.util.*;

public class Miner extends BaseBot {
	private boolean mineNow;
	private boolean goAway;
	private MapLocation goAwayFromHere;

	public Miner(RobotController rc) {
		super(rc);
		mineNow = false;
		goAway = false;
		goAwayFromHere = rc.getLocation();
	}

	public void execute() throws GameActionException {
		// retreat?
		if (rc.isCoreReady() && shouldRetreat())
			tryToMoveAway(getAverageEnemyLocation());
		
		// attack?
		if (rc.isWeaponReady())
			this.attackLeastHealthEnemy(this.getEnemiesInAttackingRange());
		
		if (rc.isCoreReady()) {
			// mine?
			if (rc.canMine() && mineNow) {
				rc.mine();
				mineNow = false;
			} else {
				// go away?
				if (goAway) {
					if (rc.getLocation().distanceSquaredTo(goAwayFromHere) >= 6)
						goAway = false;
					else						
						tryToMoveAway(goAwayFromHere);
				} else {
					// go toward more ore
					MapLocation goHere = findNearbyLocWithMostOre();
					Direction dir = getMoveDir(goHere);

					if (dir != null && rc.canMove(dir)) {
						rc.move(dir);
						mineNow = true;
					} else {
						// jk go away
						goAway = true;
						goAwayFromHere = rc.getLocation();

						tryToMoveAway(goHere);
					}
				}
			}
		}
	}

	public MapLocation findNearbyLocWithMostOre() {
		Set<MapLocation> locsInRange = this.getLocsInSensorRange(RobotType.MINER.sensorRadiusSquared);

		double maxOre = 0;
		MapLocation maxOreLocation = rc.getLocation();

		for (MapLocation loc : locsInRange) {
			double thisOre = rc.senseOre(loc);

			if (thisOre > maxOre) {
				maxOre = thisOre;
				maxOreLocation = loc;
			}
		}

		return maxOreLocation;
	}
}