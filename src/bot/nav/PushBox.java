package bot.nav;

import bot.Bot;
import lejos.hardware.Button;

public class PushBox {
	
	private Bot bot;

	public PushBox(Bot bot) {
		this.bot = bot;
	}
	
	public void exec() {
		driveStraight();
	}
	
	private boolean driveStraight() {
		int distanceTraveled = 0;
		int goalDistance = 720;
		int startTacho;
		int distanceLower = 10;
		int distanceHigher = 30;
		int directionChangeStep = 40;
		
		while (Button.ESCAPE.isUp()) {
			bot.driver.turnUS(-90, 1f, true);
			startTacho = bot.lMotor.getTachoCount();
			bot.driver.forward();
			
			while (distanceTraveled < goalDistance) {
				distanceTraveled = bot.lMotor.getTachoCount() - startTacho;
				
				if (bot.sensors.getDistance() < distanceLower) {
					bot.driver.directionChange(directionChangeStep);
				}
				
				if (bot.sensors.getDistance() > distanceHigher) {
					bot.driver.directionChange(-directionChangeStep);
				}
			}
			
			return true;
		}
		
		return false;
	}
}
