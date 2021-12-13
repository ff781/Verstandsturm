package bot.nav;

import bot.Bot;
import bot.men.Screen;
import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;

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
		int goalDistance = 30 * 360;
		int startTacho;
		float distanceOptimal = 0.23f;
		float distanceTolerance = 0.04f;
		float distanceLower = distanceOptimal - distanceTolerance;
		float distanceHigher = distanceOptimal + distanceTolerance;
		int speed = 360;
		int correction = 80;
		
		while (Button.ESCAPE.isUp()) {
			Screen.clear();
			bot.driver.setUSPosition(95, 1, true);
			startTacho = bot.lMotor.getTachoCount();
			
			while (distanceTraveled < goalDistance) {
				Screen.clear();
				distanceTraveled = bot.lMotor.getTachoCount() - startTacho;
				
				if (bot.sensors.getTouch() == 1) {
					stop();
					return true;
				}
				
				//Screen.prints("Active: " + bot.sensors.usm.isActive());
				//Screen.prints("ignore: " + bot.sensors.usm.isIgnorant());
				float dist = bot.sensors.getDistance();
				Screen.print(dist + "");
				
				if (dist < distanceLower) {
					bot.driver.forward(speed - correction, speed + correction);
					Screen.print("RIGHT");
				} else if (dist > distanceHigher) {
					bot.driver.forward(speed + correction, speed - correction);
					Screen.print("LEFT");
				} else {
					bot.driver.forward(speed, speed);
					Screen.print("STRAIGT");
				}
			}
			
			stop();
			return true;
		}
		
		stop();
		return false;
	}
	
	private void stop() {
		Screen.clear();
		bot.driver.setUSPosition(0, 1f, true);
		bot.driver.stop();
	}
}
