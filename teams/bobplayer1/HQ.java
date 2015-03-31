package bobplayer1;

import battlecode.common.*;

public class HQ extends BaseBot {
    public HQ(RobotController rc) {
        super(rc);
    }

    public void execute() throws GameActionException {
        int numSoldiers = 0;
		int numBashers = 0;
		int numBeavers = 0;
		int numBarracks = 0;
		int numTanks = 0;
		RobotInfo[] myRobots = getAllies();
		
		for (RobotInfo r : myRobots) {
			RobotType type = r.type;
			if (type == RobotType.SOLDIER) {
				numSoldiers++;
			} else if (type == RobotType.BASHER) {
				numBashers++;
			} else if (type == RobotType.BEAVER) {
				numBeavers++;
			} else if (type == RobotType.BARRACKS) {
				numBarracks++;
			} else if (type == RobotType.TANK) {
				numTanks++;
			}
			
		}
		rc.broadcast(0, numBeavers);
		rc.broadcast(1, numSoldiers);
		rc.broadcast(2, numBashers);
		rc.broadcast(100, numBarracks);
		
		if (rc.isWeaponReady()) {
			attackLeastHealthEnemy(getEnemiesInAttackingRange());
		}
		
        if (rc.isCoreReady() && rc.getTeamOre() > 100 && numBeavers < 10) {
            Direction newDir = getSpawnDirection(RobotType.BEAVER);
            if (newDir != null) {
                rc.spawn(newDir, RobotType.BEAVER);
                rc.broadcast(2, numBeavers + 1);
            }
        }
        
        MapLocation rallyPoint = new MapLocation(rc.readBroadcast(3), rc.readBroadcast(4));
        if ((numSoldiers +numTanks) < 15) {
            rallyPoint = new MapLocation( this.myHQ.x + (this.theirHQ.x - this.myHQ.x) / 4,
                                          this.myHQ.y + (this.theirHQ.y - this.myHQ.y) / 4);
        }
        else if ((numSoldiers+numTanks)>60){
            rallyPoint = this.theirHQ;
        }
        rc.broadcast(3, rallyPoint.x);
        rc.broadcast(4, rallyPoint.y);
        rc.yield();
    }
}