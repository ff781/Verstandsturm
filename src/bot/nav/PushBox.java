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
		boolean success = driveStraight();
		if (!success) return;
		success = findFirstField();
		if (!success) return;
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
			bot.driver.setUSPosition(95, 1, false);
			startTacho = bot.lMotor.getTachoCount();
			bot.driver.drive(10, 2, 1, true);
			
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
				
				if (distancePrevious != 0.0f) {
					distanceDirection = dist - distancePrevious;
					
					if (distanceDirection < dDThreshold) {
						Screen.print("GYRO " + bot.sensors.getAngel());
						bot.sensors.resetAngel();
						Screen.sleep(1000);
					}
				}
				
				distancePrevious = dist;
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
	
	private boolean findFirstField() {
		int distanceTraveled = 0;
		int goalDistance = 4 * 360;
		float usMeasured = bot.sensors.getDistance();
		float usGoal = 0.65f;
		int startTacho;
		int speed = 720;
		int correction = speed / 4;
		float whiteThreshold = 0.5f;
		float angleThreshold = 20f;
		
		while (Button.ESCAPE.isUp()) {
			Screen.clear();
			
			float angle = bot.sensors.getAngel();
			while (Math.abs(angle % 360) > angleThreshold && Button.ESCAPE.isUp()) {
				Screen.print(angle + "");
				if (angle > 0) {
					bot.driver.forward(speed / 2, -speed / 2);
				} else {
					bot.driver.forward(-speed / 2, speed / 2);
				}
				
				angle = bot.sensors.getAngel();
			}
			
			stop();
			return true;
			
			/*
			while (usMeasured > usGoal) {
				bot.driver.forward(speed, speed);
				usMeasured = bot.sensors.getDistance();
			}
			
			bot.driver.turn(-45, 2);
			startTacho = bot.lMotor.getTachoCount();
			bot.driver.forward(speed, speed);
			
			while (distanceTraveled < goalDistance) {
				Screen.clear();
				distanceTraveled = bot.lMotor.getTachoCount() - startTacho;
				
				if (SLine.brownOrWhite(bot.sensors.getRGB()) < whiteThreshold) {
					stop();
					Screen.print("COLOR");
					Screen.sleep(1000);
					return true;
				}
				
				if (bot.sensors.getTouch() == 1) {
					stop();
					Screen.print("TOUCH");
					Screen.sleep(1000);
					return true;
				}
			}*/
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
			
			while (ColorSearch.checkTouch(this.bot.sensors) && whiteFound < whiteGoal) {
				Screen.clear();
				distanceTraveled = bot.lMotor.getTachoCount() - startTacho;
				
				if (!onWhite && PushBox.brownOrWhite(bot.sensors.getRGB()) < whiteThreshold) {
					Sound.beep();
					onWhite = true;
					whiteFound++;
				}
				
				if (onWhite && PushBox.brownOrWhite(bot.sensors.getRGB()) > whiteThreshold) {
					onWhite = false;
				}
			}
		}
		
		stop();
		return false;
	}
	
	private void stop() {
		bot.driver.stop();
		Screen.clear();
		bot.driver.setUSPosition(0, 1f, true);
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