package bot.nav;

import java.util.LinkedList;

import bot.Bot;
import bot.men.Screen;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.lcd.LCD;

public class PushBox {
	public static float BAT = 0.8f;
	
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
		if (!success3) return;
		boolean success4 = lastStraight();
		return;
	}
	
	private boolean driveStraight() {
		int distanceTraveled = 0;
		int goalDistance = 8 * 360;
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
			bot.driverOld.setUSPosition(95, 1, true);
			startTacho = bot.lMotor.getTachoCount();
			bot.driverOld.drive(10, 2, 1, true);
			
			float dist = distanceOptimal;
			
			while (distanceTraveled * BAT < goalDistance) {
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
				
				boolean closeToTarget = distanceTraveled * BAT > goalDistance - 480;
				
				if (dist < distanceLower) {
					int correction2 = correction;
					if (closeToTarget) {
						//correction2 = correction / 4;
						bot.driverOld.forward(speed, speed);
					} else {
						bot.driverOld.forward(speed - correction2, speed + correction2);
					}
					Screen.print("RIGHT");
				} else if (dist > distanceHigher) {
					int correction2 = correction;
					if (closeToTarget) {
						//correction2 = correction / 4;
						bot.driverOld.forward(speed, speed);
					} else {
						bot.driverOld.forward(speed + correction2, speed - correction2);
					}
					Screen.print("LEFT");
				} else {
					bot.driverOld.forward(speed, speed);
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
		float usGoal = 0.35f;
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
			
			// correct orientation
			bot.driverOld.drive_(2.1f, speed, 90, true);
			
			// look ahead
			bot.driverOld.setUSPosition(0, 1, true);
			if (bot.sensors.getDistance() < 0.3) {
				bot.driverOld.setUSPosition(-90, speed, true);
			} else {
				bot.driverOld.setUSPosition(targetAngle1 * 1.4f, 1, true);
			}
			
			startTacho = bot.lMotor.getTachoCount();
			
			usMeasured = bot.sensors.getDistance();
			if (usMeasured > usGoal) {
				// drive forward ...
				bot.driverOld.forward(speed / 2, speed / 2);
				
				// ... until box is seen
				while (usMeasured > usGoal && !bot.sensors.isTouched()) {
					usMeasured = bot.sensors.getDistance();
				}
			}
			
			if (bot.sensors.isTouched()) stoppedForTouch = true;
			bot.driverOld.stop();
			
			Screen.print("I C U");
			
			if (stoppedForTouch) {
				// stop and drive forward a bit more
				//bot.driverOld.drive_(2f, speed, -0, true);
				
				// try to turn exactly to target angle
				bot.driverOld.drive_(8f, speed, -80, true);
			} else {
				// drive a bit forward
				bot.driverOld.drive_(2.7f, speed, -10f, true);
				
				// try to turn right to target Angle
				bot.driverOld.drive_(7.3f, speed, -100, true);
				
				// drive forward ...
				boolean touched = false;
				bot.driverOld.forward(speed / 2, speed / 2);
				startTacho = bot.lMotor.getTachoCount();
				distanceTraveled = (bot.lMotor.getTachoCount() - startTacho) / 360;
				while (distanceTraveled * BAT < 5f && Button.ESCAPE.isUp()) {
					distanceTraveled = (bot.lMotor.getTachoCount() - startTacho) / 360;
					
					// ... until box is touched
					if (bot.sensors.isTouched()) {
						touched = true;
						break;
					}
				}
				
				if (touched = false) {
					Screen.print("LAST TRY");
					bot.driverOld.drive_(5f, speed, -180, true);
					bot.driverOld.drive_(5f, speed, 100, true);
					pushBox();
				}
				
				bot.driverOld.stop();
				
				// turn smoothly left
				bot.driverOld.drive_(3f, speed, 60, true);
			}
			
			// drive until box against wall
			startTacho = bot.lMotor.getTachoCount();
			distanceTraveled = (bot.lMotor.getTachoCount() - startTacho) / 360;
			bot.driverOld.setUSPosition(-90, 1, false);
			bot.driverOld.forward(speed, speed);
			
			while (distanceTraveled * BAT < goalDistance) {
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
					this.bot.driverOld.stop();
					Screen.print("DIST");
					Screen.sleep(2000);
					return true;
				}
			}
			
			Screen.print("MOTORS");
			
			this.bot.driverOld.stop();
			return true;
		}
		
		stop();
		return false;
	}
	
	private boolean reposition() {
		int speed = 720;
		
		Screen.clear();
		Screen.print("Reposition");
		
		// push box a bit more
		bot.driverOld.drive_(15, speed, -90, true);
		bot.driverOld.drive_(10, speed, 90, true);
		
		Screen.print("DONE with pushing");
		bot.driverOld.stop();
		
		// drive back
		bot.driverOld.forward(-speed, -speed);
		
		float startTacho = bot.lMotor.getTachoCount();
		float distanceTraveled = (bot.lMotor.getTachoCount() - startTacho) / 360;
		while (distanceTraveled * BAT > -1f && Button.ESCAPE.isUp()) {
			distanceTraveled = (bot.lMotor.getTachoCount() - startTacho) / 360;
		}
		
		// turn right in place
		bot.driverOld.forward(-speed, speed);
		
		startTacho = bot.lMotor.getTachoCount();
		distanceTraveled = (bot.lMotor.getTachoCount() - startTacho) / 360;
		while (distanceTraveled * BAT > -0.77f && Button.ESCAPE.isUp()) { // TODO adjust
			distanceTraveled = (bot.lMotor.getTachoCount() - startTacho) / 360;
		}
		
		bot.driverOld.stop();
		
		boolean touchedWall = false;
		
		// forward towards wall
		bot.driverOld.forward(speed, speed);
		startTacho = bot.lMotor.getTachoCount();
		distanceTraveled = (bot.lMotor.getTachoCount() - startTacho) / 360;
		while (Button.ESCAPE.isUp() && !touchedWall && distanceTraveled * BAT < 5f) {
			distanceTraveled = (bot.lMotor.getTachoCount() - startTacho) / 360;
			
			if (bot.sensors.isTouched()) {
				touchedWall = true;
				break;
			}
		}
		Screen.print("TOUCHED WALL");
		bot.driverOld.stop();
		
		// drive back
		bot.driverOld.forward(-speed, -speed);
		startTacho = bot.lMotor.getTachoCount();
		distanceTraveled = (bot.lMotor.getTachoCount() - startTacho) / 360;
		while (distanceTraveled * BAT > -1.6f && Button.ESCAPE.isUp()) {
			distanceTraveled = (bot.lMotor.getTachoCount() - startTacho) / 360;
		}
		
		// turn right
		bot.driverOld.forward(-speed, speed);
		
		startTacho = bot.lMotor.getTachoCount();
		distanceTraveled = (bot.lMotor.getTachoCount() - startTacho) / 360;
		while (distanceTraveled * BAT > -1.43f && Button.ESCAPE.isUp()) { // TODO adjust
			distanceTraveled = (bot.lMotor.getTachoCount() - startTacho) / 360;
		}
		
		bot.driverOld.stop();
		return true;
	}
	
	private boolean lastStraight() {
		float goalDistance = 5 * 360;
		float distanceTraveled = 0f;
		float distanceOptimal = 0.15f;
		float distanceTolerance = 0.03f;
		float distanceLower = distanceOptimal - distanceTolerance;
		float distanceHigher = distanceOptimal + distanceTolerance;
		int speed = 720 / 2;
		int correction = speed / 3;
		boolean touched = false;
		
		Screen.clear();
		Screen.print("Last straight");
		
		// drive straight
		float dist = distanceOptimal;
		float startTacho = bot.lMotor.getTachoCount();
		
		// look left
		bot.driverOld.setUSPosition(90, 1, true);
		
		// drive forward
		bot.driverOld.forward(speed, speed);
		
		while (Button.ESCAPE.isUp() && distanceTraveled * BAT < 10f) {
			distanceTraveled = (bot.lMotor.getTachoCount() - startTacho) / 360;
			Screen.print(distanceTraveled + "");
			if (bot.sensors.getDistance() > 0.5f) {
				Screen.print("50");
				bot.driverOld.stop();
				
				bot.driverOld.forward(-speed, -speed);
				startTacho = bot.lMotor.getTachoCount();
				distanceTraveled = (bot.lMotor.getTachoCount() - startTacho) / 360;
				while (distanceTraveled * BAT > -0.8f && Button.ESCAPE.isUp()) {
					distanceTraveled = (bot.lMotor.getTachoCount() - startTacho) / 360;
				}
				stop();
				//Screen.beep();
				return true;
			}
			
			if ((ParcourConstants.colorClassify(bot.sensors.getRGB(), ParcourConstants.LINE_BLUE, ParcourConstants.LINE_BROWN, ParcourConstants.LINE_WHITE, ParcourConstants.LINE_FAKE_BLUE) == 0 && distanceTraveled * BAT > 8)) {
				stop();
				Screen.print("COLOR");
				//Screen.beep();
				return true;
			}
			
			if (bot.sensors.isTouched()) {
				touched = true;
				bot.driverOld.stop();
				break;
			}
		}
		
		if (touched) {
			// drive back
			bot.driverOld.forward(-speed, -speed);
			
			startTacho = bot.lMotor.getTachoCount();
			distanceTraveled = (bot.lMotor.getTachoCount() - startTacho) / 360;
			while (distanceTraveled * BAT > -1.5f && Button.ESCAPE.isUp()) {
				distanceTraveled = (bot.lMotor.getTachoCount() - startTacho) / 360;
			}
			
			// turn right
			bot.driverOld.forward(-speed, speed);
			
			startTacho = bot.lMotor.getTachoCount();
			distanceTraveled = (bot.lMotor.getTachoCount() - startTacho) / 360;
			while (distanceTraveled * BAT > -1.7f && Button.ESCAPE.isUp()) {
				distanceTraveled = (bot.lMotor.getTachoCount() - startTacho) / 360;
			}
			
			// drive back to orient
			bot.driverOld.forward(-speed, -speed);
			
			startTacho = bot.lMotor.getTachoCount();
			distanceTraveled = (bot.lMotor.getTachoCount() - startTacho) / 360;
			while (distanceTraveled * BAT > -2.5f && Button.ESCAPE.isUp()) {
				distanceTraveled = (bot.lMotor.getTachoCount() - startTacho) / 360;
			}
			
			// drive forward
			bot.driverOld.forward(speed, speed);
			while (bot.sensors.getDistance() < 0.3f && Button.ESCAPE.isUp()) {} 
			
			// turn left
			bot.driverOld.forward(speed, -speed);
			
			startTacho = bot.rMotor.getTachoCount();
			distanceTraveled = (bot.rMotor.getTachoCount() - startTacho) / 360;
			while (distanceTraveled * BAT > -1.7f && Button.ESCAPE.isUp()) {
				distanceTraveled = (bot.rMotor.getTachoCount() - startTacho) / 360;
			}
						
		}
		
		stop();
		return false;
	}
	
	private void stop() {
		this.bot.driverOld.stop();
		Screen.clear();
		this.bot.driverOld.setUSPosition(0, 1f, true);
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
				this.bot.driverOld.forward(-speed, speed);
			} else {
				Screen.print("R " + (angle - targetAngle));
				Screen.sleep(500);
				this.bot.driverOld.forward(speed, -speed);
			}
			Screen.sleep(500);
			
			angle = this.getAdjustedAngle();
		}
	}
}
