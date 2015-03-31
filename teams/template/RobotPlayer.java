package template;

import battlecode.common.*;

public class RobotPlayer {
	public static void run(RobotController rc) {
        BaseBot myself;

        if (rc.getType() == 			RobotType.AEROSPACELAB) {
            myself = new AerospaceLab(rc);
        } else if (rc.getType() == 		RobotType.BARRACKS) {
            myself = new Barracks(rc);
        } else if (rc.getType() == 		RobotType.BASHER) {
            myself = new Basher(rc);
        } else if (rc.getType() == 		RobotType.BEAVER) {
            myself = new Beaver(rc);
        } else if (rc.getType() == 		RobotType.COMMANDER) {
            myself = new Commander(rc);
        } else if (rc.getType() ==		RobotType.COMPUTER) {
        	myself = new Computer(rc);
        } else if (rc.getType() ==		RobotType.DRONE) {
        	myself = new Drone(rc);
        } else if (rc.getType() ==		RobotType.HANDWASHSTATION) {
        	myself = new HandwashStation(rc);
        } else if (rc.getType() ==		RobotType.HELIPAD) {
        	myself = new Helipad(rc);
        } else if (rc.getType() == 		RobotType.HQ) {
        	myself = new HQ(rc);
        } else if (rc.getType() == 		RobotType.LAUNCHER) {
        	myself = new Launcher(rc);
        } else if (rc.getType() == 		RobotType.MINER) {
        	myself = new Miner(rc);
        } else if (rc.getType() == 		RobotType.MINERFACTORY) {
        	myself = new MinerFactory(rc);
        } else if (rc.getType() == 		RobotType.MISSILE) {
        	myself = new Missile(rc);
        } else if (rc.getType() == 		RobotType.SOLDIER) {
        	myself = new Soldier(rc);
        } else if (rc.getType() == 		RobotType.SUPPLYDEPOT) {
        	myself = new SupplyDepot(rc);
        } else if (rc.getType() == 		RobotType.TANK) {
        	myself = new Tank(rc);
        } else if (rc.getType() == 		RobotType.TANKFACTORY) {
        	myself = new TankFactory(rc);
        } else if (rc.getType() == 		RobotType.TECHNOLOGYINSTITUTE) {
        	myself = new TechInst(rc);
        } else if (rc.getType() == 		RobotType.TOWER) {
        	myself = new Tower(rc);
        } else if (rc.getType() == 		RobotType.TRAININGFIELD) {
        	myself = new TrainingField(rc);
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
}
