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
			bot.driver.setUSPosition(95, 1, true);
			startTacho = bot.lMotor.getTachoCount();
			bot.driver.drive(10, 2, 1, true);
			
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
			bot.driver.drive_(2.1f, speed, 90, true);
			
			// look ahead
			bot.driver.setUSPosition(0, 1, true);
			if (bot.sensors.getDistance() < 0.3) {
				bot.driver.setUSPosition(-90, speed, true);
			} else {
				bot.driver.setUSPosition(targetAngle1 * 1.4f, 1, true);
			}
			
			startTacho = bot.lMotor.getTachoCount();
			
			usMeasured = bot.sensors.getDistance();
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
				//bot.driver.drive_(2f, speed, -0, true);
				
				// try to turn exactly to target angle
				bot.driver.drive_(8f, speed, -80, true);
			} else {
				// drive a bit forward
				bot.driver.drive_(2.7f, speed, -10f, true);
				
				// try to turn right to target Angle
				bot.driver.drive_(7.3f, speed, -100, true);
				
				// drive forward ...
				boolean touched = false;
				bot.driver.forward(speed / 2, speed / 2);
				startTacho = bot.lMotor.getTachoCount();
				distanceTraveled = (bot.lMotor.getTachoCount() - startTacho) / 360;
				while (distanceTraveled < 5f && Button.ESCAPE.isUp()) {
					distanceTraveled = (bot.lMotor.getTachoCount() - startTacho) / 360;
					
					// ... until box is touched
					if (bot.sensors.isTouched()) {
						touched = true;
						break;
					}
				}
				
				if (touched = false) {
					Screen.print("LAST TRY");
					bot.driver.drive_(5f, speed, -180, true);
					bot.driver.drive_(5f, speed, 100, true);
					pushBox();
				}
				
				bot.driver.stop();
				
				// turn smoothly left
				bot.driver.drive_(3f, speed, 60, true);
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
		
		stop();
		return false;
	}
	
	private boolean reposition() {
		int speed = 720;
		
		Screen.clear();
		Screen.print("Reposition");
		
		// push box a bit more
		bot.driver.drive_(15, speed, -90, true);
		bot.driver.drive_(10, speed, 90, true);
		
		Screen.print("DONE with pushing");
		bot.driver.stop();
		
		// drive back
		bot.driver.forward(-speed, -speed);
		
		float startTacho = bot.lMotor.getTachoCount();
		float distanceTraveled = (bot.lMotor.getTachoCount() - startTacho) / 360;
		while (distanceTraveled > -1f && Button.ESCAPE.isUp()) {
			distanceTraveled = (bot.lMotor.getTachoCount() - startTacho) / 360;
		}
		
		// turn right in place
		bot.driver.forward(-speed, speed);
		
		startTacho = bot.lMotor.getTachoCount();
		distanceTraveled = (bot.lMotor.getTachoCount() - startTacho) / 360;
		while (distanceTraveled > -0.77f && Button.ESCAPE.isUp()) { // TODO adjust
			distanceTraveled = (bot.lMotor.getTachoCount() - startTacho) / 360;
		}
		
		bot.driver.stop();
		
		boolean touchedWall = false;
		
		// forward towards wall
		bot.driver.drive_(20, speed, 0, false);
		while (Button.ESCAPE.isUp() && !touchedWall) {
			if (bot.sensors.isTouched()) {
				touchedWall = true;
				break;
			}
		}
		bot.driver.stop();
		
		// drive back
		bot.driver.forward(-speed, -speed);
		startTacho = bot.lMotor.getTachoCount();
		distanceTraveled = (bot.lMotor.getTachoCount() - startTacho) / 360;
		while (distanceTraveled > -1.6f && Button.ESCAPE.isUp()) {
			distanceTraveled = (bot.lMotor.getTachoCount() - startTacho) / 360;
		}
		
		// turn right
		bot.driver.forward(-speed, speed);
		
		startTacho = bot.lMotor.getTachoCount();
		distanceTraveled = (bot.lMotor.getTachoCount() - startTacho) / 360;
		while (distanceTraveled > -1.43f && Button.ESCAPE.isUp()) { // TODO adjust
			distanceTraveled = (bot.lMotor.getTachoCount() - startTacho) / 360;
		}
		
		bot.driver.stop();
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
		bot.driver.setUSPosition(90, 1, true);
		
		// drive forward
		bot.driver.forward(speed, speed);
		
		while (Button.ESCAPE.isUp() && distanceTraveled < 10f) {
			distanceTraveled = (bot.lMotor.getTachoCount() - startTacho) / 360;
			Screen.print(distanceTraveled + "");
			if (bot.sensors.getDistance() > 0.5f) {
				Screen.print("50");
				bot.driver.stop();
				
				bot.driver.forward(-speed, -speed);
				startTacho = bot.lMotor.getTachoCount();
				distanceTraveled = (bot.lMotor.getTachoCount() - startTacho) / 360;
				while (distanceTraveled > -0.8f && Button.ESCAPE.isUp()) {
					distanceTraveled = (bot.lMotor.getTachoCount() - startTacho) / 360;
				}
				stop();
				//Screen.beep();
				return true;
			}
			
			if ((ParcourConstants.colorClassify(bot.sensors.getRGB(), ParcourConstants.LINE_BLUE, ParcourConstants.LINE_BROWN, ParcourConstants.LINE_WHITE, ParcourConstants.LINE_FAKE_BLUE) == 0 && distanceTraveled > 8)) {
				stop();
				Screen.print("COLOR");
				//Screen.beep();
				return true;
			}
			
			if (bot.sensors.isTouched()) {
				touched = true;
				bot.driver.stop();
				break;
			}
		}
		
		if (touched) {
			// drive back
			bot.driver.forward(-speed, -speed);
			
			startTacho = bot.lMotor.getTachoCount();
			distanceTraveled = (bot.lMotor.getTachoCount() - startTacho) / 360;
			while (distanceTraveled > -1.5f && Button.ESCAPE.isUp()) {
				distanceTraveled = (bot.lMotor.getTachoCount() - startTacho) / 360;
			}
			
			// turn right
			bot.driver.forward(-speed, speed);
			
			startTacho = bot.lMotor.getTachoCount();
			distanceTraveled = (bot.lMotor.getTachoCount() - startTacho) / 360;
			while (distanceTraveled > -1.7f && Button.ESCAPE.isUp()) {
				distanceTraveled = (bot.lMotor.getTachoCount() - startTacho) / 360;
			}
			
			// drive back to orient
			bot.driver.forward(-speed, -speed);
			
			startTacho = bot.lMotor.getTachoCount();
			distanceTraveled = (bot.lMotor.getTachoCount() - startTacho) / 360;
			while (distanceTraveled > -2.5f && Button.ESCAPE.isUp()) {
				distanceTraveled = (bot.lMotor.getTachoCount() - startTacho) / 360;
			}
			
			// drive forward
			bot.driver.forward(speed, speed);
			while (bot.sensors.getDistance() < 0.3f && Button.ESCAPE.isUp()) {} 
			
			// turn left
			bot.driver.forward(speed, -speed);
			
			startTacho = bot.rMotor.getTachoCount();
			distanceTraveled = (bot.rMotor.getTachoCount() - startTacho) / 360;
			while (distanceTraveled > -1.7f && Button.ESCAPE.isUp()) {
				distanceTraveled = (bot.rMotor.getTachoCount() - startTacho) / 360;
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
	
	public static float brownOrWhite(float[] rgb) {
		  float[] brown = {0.02f, 0.0365f, 0.0039f};
		  float[] white = {0.163f, 0.283f, 0.1313f};
		  float threshold = 0.5f;
		  
		  float result = 0f;
		  
		  for (int i = 0; i < brown.length; i++) {
			  result += (rgb[i] - brown[i]) / (white[i] - brown[i]);
		  }
		  
		  if (result > 1.0f) result = 1.0f;
		  if (result < 0.0f) result = 0.0f;
		  
		  return 1 - result;
		  //return (result > threshold) ? 0.0f : 1.0f;
	  }
}