package bot.nav;

import bot.Bot;
import bot.sen.SensorThread;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.lcd.LCD;

public class RunWay {
	
	private Bot bot;
	
	private float rStart;
	private float gStart;
	private float bStart;
	
	private float startOrientation;
	
	public RunWay(Bot bot) {
		this.bot = bot;
	}
	
	/**
	 * Drives up and down the ramp
	 */
	public void rampe() {

		startOrientation = this.bot.sensors.getAngel();
		float[] rgbBack = this.bot.sensors.getRGB();
		rStart = rgbBack[0];
		gStart = rgbBack[1];
		bStart = rgbBack[2];
		
		LCD.clear();
		
		System.out.println("T1: Find B");
		findBlueLine();
		System.out.println("T2: Drive U");
		driveUp();
		System.out.println("T3: Drive L");
		driveAcross();
		System.out.println("T4: Drive D");
		driveDown();
		System.out.println("Ramp Done");
		
		this.bot.driver.stop();
	}
	
	
	private void findBlueLine() {
		bot.driver.forward();
		
		while (Button.ESCAPE.isUp()) {
			//recognize different color
			if (checkColor(bot.sensors)) {
				if (checkBlue(bot.sensors)) {
					this.bot.driver.stop();
					Sound.beepSequence();
					break;
				}
			}
			
			// check if Robot touches wall on left side
			if (checkTouch(bot.sensors)) {
				this.bot.driver.drive(1f, 2f, -1);
				this.bot.driver.turn(90f, 2f, false);
				this.bot.driver.drive(1f, 2f, -1);
				this.bot.driver.turn(-90f, 2f, false);
				this.bot.driver.forward();
			}
		}
	}
	
	private void driveUp() {
		bot.driver.forward();
		
		while (Button.ESCAPE.isUp()) {
			//recognize different color
			if (checkColor(bot.sensors)) {
				if (checkTransition(bot.sensors)){
					this.bot.driver.stop();
					Sound.beepSequence();
					break;
				}
			}
			
			// check direction
			if (checkGyro(bot.sensors)) {
				float error = bot.sensors.getAngel() - this.startOrientation;
				this.bot.driver.turn(-error, 1f, false);
			}
		}
	}
	
	private void driveAcross() {
		bot.driver.drive(1f, 2f, 1);
		bot.driver.turn(90f, 2f, false);
		bot.driver.forward();
		
		while (Button.ESCAPE.isUp()) {
			//recognize different color
			if (checkCliff(bot.sensors)) {
				this.bot.driver.stop();
				Sound.beepSequence();
				break;
			}
			
			// check direction
			if (checkGyro(bot.sensors)) {
				float error = bot.sensors.getAngel() - (this.startOrientation - 90f);
				this.bot.driver.turn(-error, 1f, false);
			}
		}
	}
	
	private void driveDown() {
		bot.driver.drive(1f, 2f, -1);
		bot.driver.turn(90f, 2f, false);
		bot.driver.forward();
		
		while (Button.ESCAPE.isUp()) {
			// Check for transition (color)
			/*
			if (checkTransition(bot.sensors)){
				this.bot.driver.stop();
				Sound.beepSequence();
				break;
			}*/
			
			if (checkBlue(bot.sensors)) {
				bot.driver.drive(2f, 2f, 1);
				this.bot.driver.stop();
				Sound.beepSequence();
				break;
			}
			
			// check direction
			if (checkGyro(bot.sensors)) {
				float error = bot.sensors.getAngel() - (this.startOrientation - 180f);
				this.bot.driver.turn(-error, 1f, false);
			}
		}
	}
	
	
	/**
	 * @param sensor to get the values from.
	 * @return true if there was a color change detected, false else.
	 */
	private boolean checkColor(SensorThread sensor) {
		float[] rgb = sensor.getRGB();
		float r = rgb[0];
		float g = rgb[1];
		float b = rgb[2];
		
		if ((r - rStart) + (g - gStart) + (b - bStart) > 0.05 ) {
			return true;
		}
		return false;
	}
	
	private boolean checkBlue(SensorThread sensor) {
		float[] rgb = sensor.getRGB();
		float[] goal = {0.0255f, 0.102f, 0.075f};
		float tolerance = 0.1f;
		
		return rgbErrorSum(rgb, goal) < tolerance;
	}
	
	private boolean checkTransition(SensorThread sensor) {
		float[] rgb = sensor.getRGB();
		float[] goal = {0.042f, 0.051f, 0.015f};
		float tolerance = 0.1f;
		
		return rgbErrorSum(rgb, goal) < tolerance;
	}
	
	private boolean checkCliff(SensorThread sensor) {
		float[] rgb = sensor.getRGB();
		float threshold = 2f;
		
		return rgb[0] + rgb[1] + rgb[2] > threshold;
	}
	
	private float rgbErrorSum(float measured[], float goal[]) {
		assert measured.length == 3;
		assert measured.length == goal.length;
		
		return Math.abs(measured[0] - goal[0]) + Math.abs(measured[1] - goal[1]) + Math.abs(measured[2] - goal[2]);
	}
	
	private boolean checkGyro(SensorThread sensor) {
		float threshold = 5f;
		
		return Math.abs(sensor.getAngel() - this.startOrientation) > threshold;
	}
	
	
	/**
	 * @param sensor sensor to get values from.
	 * @return true if touch sensor is signaled, false else.
	 */
	private boolean checkTouch(SensorThread sensor) {
		if(sensor.getTouch() == 1) {
			return true;
		}
		return false;
	}
}
