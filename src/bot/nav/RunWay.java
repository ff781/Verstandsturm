package bot.nav;

import bot.Bot;
import bot.sen.SensorThread;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.lcd.LCD;

public class RunWay {
	
	private Bot bot;
	
	private float[] rgbStart;
	
	private float startOrientation;
	
	private int phase = 0;
	
	public RunWay(Bot bot) {
		this.bot = bot;
	}
	
	/**
	 * Drives up and down the ramp
	 */
	public void rampe() {

		startOrientation = this.bot.sensors.getAngel();
		rgbStart = this.bot.sensors.getRGB();
		
		LCD.clear();
		
		while (Button.ESCAPE.isUp()) {
			switch(phase) {
			case 0:
				System.out.println("T1: Find B");
				findBlueLine();
				phase = 1;
				break;
			case 1:
				System.out.println("T2: Drive U");
				driveUp();
				phase = 2;
				break;
			case 2:
				System.out.println("T3: Drive L");
				driveAcross();
				phase = 3;
				break;
			case 3:
				System.out.println("T4: Drive D");
				driveDown();
				phase = 4;
			case 4:
				System.out.println("Ramp Done");
				finish();
				phase = 5;
				break;
			default:
				System.out.println("Finito");
				return;
			}
		}
		
		this.bot.driver.stop();
	}
	
	
	private void findBlueLine() {
		bot.driver.forward();
		
		while (Button.ESCAPE.isUp()) {
			//recognize different color
			if (colorChange(bot.sensors) && checkBlue(bot.sensors)) {
				this.bot.driver.stop();
				Sound.beepSequence();
				break;
			}
			
			// check if Robot touches wall on left side
			if (checkTouch(bot.sensors)) {
				this.bot.driver.drive(4f, 2f, -1);
				this.bot.driver.turn(-90f, 2f, true);
				this.bot.driver.drive(13f, 2f, 1);
				this.bot.driver.turn(90f, 2f, true);
				this.bot.driver.forward();
			}
		}
	}
	
	private void driveUp() {
		// check direction
		if (checkGyro(bot.sensors)) {
			float error = bot.sensors.getAngel() - this.startOrientation;
			this.bot.driver.turn(-error, 2f, true);
		}
		
		bot.driver.forward();
		
		while (Button.ESCAPE.isUp()) {
			//recognize different color
			if (colorChange(bot.sensors) && checkTransition(bot.sensors)){
				this.bot.driver.stop();
				bot.driver.drive(18f, 2f, 1, false);
				Sound.beepSequence();
				break;
			}
			
			// check direction
			if (checkGyro(bot.sensors)) {
				float error = bot.sensors.getAngel() - this.startOrientation;
				System.out.println(error);
				//this.bot.driver.turn(-error, 2f, true);
			}
		}
	}
	
	private void driveAcross() {
		// check direction
		if (checkGyro(bot.sensors)) {
			float error = bot.sensors.getAngel() - (this.startOrientation - 90f);
			this.bot.driver.turn(-error, 2f, false);
		}
		
		bot.driver.turn(90f, 2f, true);
		bot.driver.drive(150f, 1.75f, 1, false);
		
		while (Button.ESCAPE.isUp()) {
			float[] rgb = bot.sensors.getRGB();
			float r = rgb[0];
			float g = rgb[1];
			float b = rgb[2];
			System.out.println(String.format("%.3f", r) + " " + String.format("%.3f", g) + " " + String.format("%.3f", b));
			
			// check direction
			if (checkGyro(bot.sensors)) {
				float error = bot.sensors.getAngel() - (this.startOrientation - 90f);
				System.out.println(error);
				//this.bot.driver.turn(-error, 2f, false);
			}
			
			// check for cliff
			if (colorChange(bot.sensors) && checkCliff(bot.sensors)) {
				this.bot.driver.stop();
				Sound.beepSequence();
				bot.driver.drive(7f, 2f, -1);
				bot.driver.turn(90f, 2f, true);
				break;
			}
		}
	}
	
	private void driveDown() {
		bot.driver.drive(150f, 2f, 1, false);
		
		while (Button.ESCAPE.isUp()) {
			// Check for transition (color)
			/*
			if (checkTransition(bot.sensors)){
				this.bot.driver.stop();
				Sound.beepSequence();
				break;
			}*/
			
			if (colorChange(bot.sensors) && checkBlue(bot.sensors)) {
				this.bot.driver.stop();
				Sound.beepSequence();
				break;
			}
			
			// check direction
			if (checkGyro(bot.sensors)) {
				float error = bot.sensors.getAngel() - (this.startOrientation - 180f);
				System.out.println(error);
				//this.bot.driver.turn(-error, 2f, false);
			}
		}
	}
	
	private void finish() {
		bot.driver.drive(2f, 2f, 1);
	}
	
	
	/**
	 * @param sensor to get the values from.
	 * @return true if there was a color change detected, false else.
	 */
	private boolean colorChange(SensorThread sensor) {
		float[] rgb = sensor.getRGB();
		float threshold = 0.03f;
		
		return rgbErrorSum(rgb, rgbStart) > threshold;
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
		float threshold = 0.07f;
		
		return rgb[0] + rgb[1] + rgb[2] < threshold;
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
