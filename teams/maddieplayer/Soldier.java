package maddieplayer;

import battlecode.common.*;

public class Soldier extends BaseBot {
	public Soldier(RobotController rc) {
		super(rc);
	}

	public void execute() throws GameActionException {
		// retreat?
		if (rc.isCoreReady() && shouldRetreat())
			tryToMoveAway(getAverageEnemyLocation());

		// attack?
		if (rc.isWeaponReady())
			this.attackLeastHealthEnemy(this.getEnemiesInAttackingRange());
		
		// go somewhere
		if (rc.isCoreReady()) {
			if (rc.senseNearbyRobots(rc.getType().sensorRadiusSquared, myTeam).length <= 20)
				this.tryToMoveToward(myAvgTowerLoc);
			else
				this.tryToMoveToward(theirAvgTowerLoc);
		}
	}
}