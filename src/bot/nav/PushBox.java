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
		boolean success2 = findFirstField();
		if (!success2) return;
		//success = pushBox();
	}
	
	private boolean driveStraight() {
		int distanceTraveled = 0;
		int goalDistance = 9 * 360;
		int startTacho;
		float distanceOptimal = 0.23f;
		float distanceTolerance = 0.04f;
		float distanceLower = distanceOptimal - distanceTolerance;
		float distanceHigher = distanceOptimal + distanceTolerance;
		float distancePrevious = 0.0f;
		float distanceDirection;
		float dDThreshold = 0.005f;
		int speed = 720;
		int correction = speed / 4;
		
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
					return true;
				}
				
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
	
	private boolean findFirstField() {
		int distanceTraveled = 0;
		int goalDistance = 6 * 360;
		float usMeasured = bot.sensors.getDistance();
		float usGoal = 0.5f;
		int startTacho;
		int speed = 720;
		int correction = speed / 4;
		float whiteThreshold = 0.5f;
		float angleThreshold = 3.5f;
		float targetAngle = -45f;
		float closeToBoxDist = 0.05f;
		int numWhiteSeen = 0;
		
		while (Button.ESCAPE.isUp()) {
			Screen.clear();
			
			bot.driver.setUSPosition(targetAngle, 1, true);
			this.turnToAngle(0f, angleThreshold, speed / 4);
			
			while (usMeasured > usGoal) {
				bot.driver.forward(speed, speed);
				usMeasured = bot.sensors.getDistance();
			}
			
			Screen.print("I C U");
			
			this.stop();
			
			/*
			this.turnToAngle(targetAngle, angleThreshold, speed / 4);
			
			Screen.print("50 turned");
			
			startTacho = bot.lMotor.getTachoCount();
			bot.driver.forward(speed, speed);
			
			while (distanceTraveled < goalDistance) {
				Screen.clear();
				distanceTraveled = bot.lMotor.getTachoCount() - startTacho;
				
				if (SLine.brownOrWhite(bot.sensors.getRGB()) < whiteThreshold) {
					//stop();
					Screen.print("COLOR");
					numWhiteSeen++;
					return true;
				}
				
				if (bot.sensors.getTouch() == 1) {
					stop();
					Screen.print("TOUCH");
					Screen.sleep(3000);
					return true;
				}
				
				if (bot.sensors.getDistance() < closeToBoxDist) {
					stop();
					Screen.print("DIST");
					Screen.sleep(3000);
					return true;
				}
			}
			
			Screen.print("MOTORS");
			
			*/
			this.stop();
			return false;
		}
		
		stop();
		return false;
	}
	
	private boolean pushBox() {
		int distanceTraveled = 0;
		int goalDistance = 5 * 360;
		int startTacho;
		int speed = 720;
		int correction = speed / 4;
		float whiteThreshold = 0.5f;
		int whiteFound = 0;
		int whiteGoal = 2;
		boolean onWhite = false;
		
		while (Button.ESCAPE.isUp()) {
			Screen.clear();
			
			startTacho = bot.lMotor.getTachoCount();
			bot.driver.forward(speed, speed);
			
			while (bot.sensors.isTouched() && whiteFound < whiteGoal) {
				Screen.clear();
				distanceTraveled = bot.lMotor.getTachoCount() - startTacho;
				
				if (!onWhite && SLine.brownOrWhite(bot.sensors.getRGB()) < whiteThreshold) {
					Sound.beep();
					onWhite = true;
					whiteFound++;
				}
				
				if (onWhite && SLine.brownOrWhite(bot.sensors.getRGB()) > whiteThreshold) {
					onWhite = false;
				}
			}
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
			Screen.print(angle + "");
			
			if (Math.abs(180f - angle) < angleThreshold * 3) {
				this.stop();
				return;
			}
			
			if (angle > targetAngle) {
				this.bot.driver.forward(-speed, speed);
			} else {
				this.bot.driver.forward(speed, -speed);
			}
			
			angle = this.getAdjustedAngle();
		}
	}
}
