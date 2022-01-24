package bot.nav;

import java.util.LinkedList;

import bot.Bot;
import bot.men.Screen;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.lcd.LCD;

public class PushBox {
	
	private Bot bot;

	public PushBox(Bot bot) {
		this.bot = bot;
	}
	
	public void exec() {
		boolean success1 = driveStraight();
		if (!success1) return;
		boolean success2 = pushBox();
		if (!success2) return;
		boolean success3 = reposition();
		return;
	}
	
	private boolean driveStraight() {
		int distanceTraveled = 0;
		int goalDistance = 9 * 360 + 180;
		int startTacho;
		float distanceOptimal = 0.26f;
		float distanceTolerance = 0.03f;
		float distanceLower = distanceOptimal - distanceTolerance;
		float distanceHigher = distanceOptimal + distanceTolerance;
		float distancePrevious = 0.0f;
		float distanceDirection;
		float dDThreshold = 0.005f;
		int speed = 720;
		int correction = speed / 4;
		float whiteThreshold = 0.5f;
		
		Screen.clear();
		
		while (Button.ESCAPE.isUp()) {
			bot.driver.setUSPosition(95, 1, true);
			startTacho = bot.lMotor.getTachoCount();
			bot.driver.drive(10, 2, 1, true);
			
			float dist = distanceOptimal;
			
			while (distanceTraveled < goalDistance) {
				Screen.clear();
				distanceTraveled = bot.lMotor.getTachoCount() - startTacho;
				
				if (bot.sensors.getTouch() == 1) {
					stop();
					Screen.print("Touch");
					return true;
				}
				/*
				if (SLine.brownOrWhite(bot.sensors.getRGB()) < whiteThreshold) {
					stop();
					Screen.print("Color");
					return true;
				}*/
				
				//Screen.prints("Active: " + bot.sensors.usm.isActive());
				//Screen.prints("ignore: " + bot.sensors.usm.isIgnorant());
				
				dist = bot.sensors.getDistance();
				Screen.print(dist + "");
				
				boolean closeToTarget = distanceTraveled > goalDistance - 480;
				
				if (dist < distanceLower) {
					int correction2 = correction;
					if (closeToTarget) {
						//correction2 = correction / 4;
						bot.driver.forward(speed, speed);
					} else {
						bot.driver.forward(speed - correction2, speed + correction2);
					}
					Screen.print("RIGHT");
				} else if (dist > distanceHigher) {
					int correction2 = correction;
					if (closeToTarget) {
						//correction2 = correction / 4;
						bot.driver.forward(speed, speed);
					} else {
						bot.driver.forward(speed + correction2, speed - correction2);
					}
					Screen.print("LEFT");
				} else {
					bot.driver.forward(speed, speed);
					Screen.print("STRAIGT");
					Screen.print("GYRO " + bot.sensors.getAngel());
					bot.sensors.resetAngel();
					Screen.sleep(1000);
				}
			}
			
			stop();
			return true;
		}
		
		stop();
		return false;
	}
	
	private boolean pushBox() {
		int distanceTraveled = 0;
		int goalDistance = 7 * 360;
		float usMeasured = bot.sensors.getDistance();
		float usGoal = 0.45f;
		int startTacho;
		int speed = 720;
		int correction = speed / 4;
		float whiteThreshold = 0.5f;
		float angleThreshold = 3.5f;
		float targetAngle1 = -45f;
		float closeToBoxDist = 0.2f;
		int numWhiteSeen = 0;
		int numTouched = 0;
		boolean stoppedForTouch = false;
		
		while (Button.ESCAPE.isUp()) {
			Screen.clear();
			
			// look right
			bot.driver.setUSPosition(targetAngle1, 1, true);
			
			// correct orientation
			bot.driver.drive_(2.1f, speed, 90, true);
			
			startTacho = bot.lMotor.getTachoCount();
			
			if (usMeasured > usGoal) {
				// drive forward ...
				bot.driver.forward(speed / 2, speed / 2);
				
				// ... until box is seen
				while (usMeasured > usGoal && !bot.sensors.isTouched()) {
					usMeasured = bot.sensors.getDistance();
				}
			}
			
			if (bot.sensors.isTouched()) stoppedForTouch = true;
			bot.driver.stop();
			
			Screen.print("I C U");
			
			if (stoppedForTouch) {
				// stop and drive forward a bit more
				bot.driver.drive_(2f, speed, -0, true);
				
				// try to turn exactly to target angle
				bot.driver.drive_(6f, speed, -90, true);
			} else {
				// try to turn to target Angle
				bot.driver.drive_(7f, speed, -80, true);
				
				// drive forward ...
				bot.driver.forward(speed / 2, speed / 2);
				
				// ... until box is touched
				while (!bot.sensors.isTouched()) {}
				
				bot.driver.stop();
				
				// turn smoothly
				bot.driver.drive_(8f, speed, 45, true);
			}
			
			// drive until box against wall
			startTacho = bot.lMotor.getTachoCount();
			distanceTraveled = (bot.lMotor.getTachoCount() - startTacho) / 360;
			bot.driver.setUSPosition(-90, 1, false);
			bot.driver.forward(speed, speed);
			
			while (distanceTraveled < goalDistance) {
				Screen.clear();
				distanceTraveled = bot.lMotor.getTachoCount() - startTacho;
				
				if (SLine.brownOrWhite(bot.sensors.getRGB()) < whiteThreshold) {
					//stop();
					numWhiteSeen++;
					Screen.print("COLOR " + numWhiteSeen);
					Screen.sleep(2000);
					//return true;
				}
				
				if (bot.sensors.isTouched()) {
					numTouched++;
					Screen.print("TOUCH");
					Screen.sleep(1000);
				}
				/*
				if (numWhiteSeen >= 1 && numTouched == 0 && bot.sensors.getDistance() > 0.35f) {
					stop();
					Screen.print("FAILED");
					Screen.sleep(2000);
					return false;
				}
				*/
				if (bot.sensors.getDistance() < closeToBoxDist) {
					this.bot.driver.stop();
					Screen.print("DIST");
					Screen.sleep(2000);
					return true;
				}
			}
			
			Screen.print("MOTORS");
			
			this.bot.driver.stop();
			return true;
		}
		
		this.bot.driver.stop();
		return false;
	}
	
	private boolean reposition() {
		Screen.clear();
		
		int speed = 720;
		
		// push box a bit more
		bot.driver.drive_(10, speed, -80);
		bot.driver.drive_(10, speed, 0);
		bot.driver.drive_(15, speed, 80);
		bot.driver.drive_(10, speed, 0);
		
		// look right for wall
		bot.driver.setUSPosition(-90, 1, false);
		
		// drive back
		bot.driver.drive_(5, speed, -180);
		
		// turn right in place
		bot.driver.drive_(9, speed, -90);
		
		bot.driver.drive_(15, speed, 30, false);
		
		// drive straight into wall
		while(Button.ESCAPE.isUp()) {
			if (bot.sensors.isTouched()) {
				this.bot.driver.stop();
				break;
			}
		}
		
		// straigthen out
		bot.driver.drive_(5, speed, -45);
		bot.driver.drive_(5, speed, 45);
		bot.driver.drive_(5, speed, 0);
		
		// back
		bot.driver.drive_(10, speed, -180, false);
		
		while(bot.sensors.getDistance() < 0.65) {
			
		}
		
		bot.driver.stop();
		
		bot.driver.setUSPosition(0, speed, true);
		
		// turn 90 deg right
		bot.driver.drive_(14, speed, -90);
		
		// drive (straight)
		bot.driver.drive_(60, speed, 0, false);
		
		while(bot.sensors.getDistance() < 0.4) {
			
		}
		
		stop();
		return false;
	}
	
	private void stop() {
		this.bot.driver.stop();
		Screen.clear();
		this.bot.driver.setUSPosition(0, 1f, true);
	}
	
	private float getAdjustedAngle() {
		float angle = this.bot.sensors.getAngel();
		
		if (angle < -180) {
			angle = angle + 360;
		}
		if (angle > 180) {
			angle = angle - 360;
		}
		
		return angle;
	}
	
	private void turnToAngle(float targetAngle, float angleThreshold, int speed) {
		float angle = this.getAdjustedAngle();
		
		while (Math.abs(angle - targetAngle) > angleThreshold && Button.ESCAPE.isUp()) {
			if (Math.abs(180f - angle) < angleThreshold * 3) {
				this.stop();
				return;
			}
			
			if (angle > targetAngle) {
				Screen.print("L " + (angle - targetAngle));
				Screen.sleep(500);
				this.bot.driver.forward(-speed, speed);
			} else {
				Screen.print("R " + (angle - targetAngle));
				Screen.sleep(500);
				this.bot.driver.forward(speed, -speed);
			}
			Screen.sleep(500);
			
			angle = this.getAdjustedAngle();
		}
	}
}
