package maddieplayer;

import java.util.Random;

import battlecode.common.*;

public class Beaver extends BaseBot {
	private boolean builtSomething;
	private RobotType buildThis;
	private int stepsTaken = 0;
	
	Beaver(RobotController rc) {
		super(rc);
		builtSomething = false;

		double fate = rand.nextDouble();

		if (fate <= 0.3)
			buildThis = RobotType.MINERFACTORY;
		else
			buildThis = RobotType.BARRACKS;
	}

	public void execute() throws GameActionException {
		// attack?
		if (rc.isWeaponReady())
			this.attackLeastHealthEnemy(this.getEnemiesInAttackingRange());

		if (rc.isCoreReady()) {
			if (builtSomething)
				// mine
				if (rc.canMine())
					rc.mine();
				// move away
				else
					tryToMoveToward(this.theirHQ);
			else if (stepsTaken >= 10) {
				Direction buildDir = getBuildDirection(buildThis);

				// build?
				if (buildDir != null && rc.canBuild(buildDir, buildThis)) {
					rc.build(buildDir, buildThis);
					builtSomething = true;
				}
				// mine?
				else {
					if (rc.canMine()) {
						rc.mine();
						System.out.println("mine");
					}
					// go away from HQ
					else
						tryToMoveAway(this.myHQ);
				}
			}
			// go away from HQ
			else {
				tryToMoveInRandomDirection();
				stepsTaken++;
			}
		}
	}
}