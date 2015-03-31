package bobplayer1;

import battlecode.common.*;

public class Beaver extends BaseBot {
    public Beaver(RobotController rc) {
        super(rc);
    }

    public void execute() throws GameActionException {
    	if (rc.isCoreReady()) {
            if (rc.getTeamOre() < 500) {
                //mine
                if (rc.senseOre(rc.getLocation()) > 3) {
                	//System.out.println(rc.senseOre(rc.getLocation()));
                    rc.mine();
                }
                else {
                    Direction newDir = getMoveDir(this.theirHQ);

                    if (newDir != null) {
                        rc.move(newDir);
                    }
                }
            }
            else if (rc.readBroadcast(100) == 0) {                
                Direction newDir = getBuildDirection(RobotType.BARRACKS);
                if (newDir != null) {
                    rc.build(newDir, RobotType.BARRACKS);
                }
            }
            else {
            	Direction newDir = getBuildDirection(RobotType.TANKFACTORY);
            	if (newDir != null) {
            		rc.build(newDir, RobotType.TANKFACTORY);
            	}
            }
        }

        rc.yield();
    }
}