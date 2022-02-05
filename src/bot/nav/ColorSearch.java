package bot.nav;

import java.util.concurrent.TimeUnit;

import bot.Bot;
import bot.men.Screen;
import bot.sen.SensorThread;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.lcd.LCD;

public class ColorSearch {
	
	private Bot bot;
	
	private float r, rBack, rFirst;
	private float g, gBack, gFirst;
	private float b, bBack, bFirst;
	
	private boolean foundFirst;
	
	public ColorSearch(Bot bot) {
		this.bot = bot;
	}

	
	/**
	 * Searches a closed room for 2 different colored squares, produces a beep sound when it finds the first one and stops the robots when it finds the second one.
	 */
	public void search() {
		LCD.clear();
		
		int cnt = 0;
		
		
		float[] rgbBack = this.bot.sensors.getRGB();
		rBack = rgbBack[0];
		gBack = rgbBack[1];
		bBack = rgbBack[2];
		
		bot.driver.forward();
		
		while(Button.ESCAPE.isUp()) {
			
			//recognize different color
			if(checkColor(bot.sensors)) {
				if(!foundFirst) {
					//first color found
					Sound.beepSequenceUp();
					float[] rgb = this.bot.sensors.getRGB();
					rFirst = rgb[0];
					gFirst = rgb[1];
					bFirst = rgb[2];
					foundFirst = true;
					this.bot.driver.forward();
					try {
						TimeUnit.SECONDS.sleep(1);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else {
					//second color found
					this.bot.driver.stop();
					Screen.clear();
					Sound.beepSequence();
					break;
				}
			}
			
			//Robot touches wall
			if(checkTouch(bot.sensors)) {
				//on the first wall turn to the left
				if (cnt == 0) {
					this.bot.driver.drive(2f, 4f, -1);
					this.bot.driver.turnGyro(90f, 1f);
					this.bot.driver.drive(8f, 4f, -1);
					this.bot.driver.forward();
					cnt += 2;		
				}
				//then start checking inside the square
				else {
					if(cnt % 2 == 0) {
						this.bot.driver.drive(4f, 4f, -1);
						this.bot.driver.turnGyro(90f, 2f);
						this.bot.driver.drive(8f, 4f, 1);
						this.bot.driver.turnGyro(90f, 2f);
						this.bot.driver.drive(15f, 4f, -1);
						this.bot.driver.forward();
						cnt++;
					}
					else {
						this.bot.driver.drive(4f, 4f, -1);
						this.bot.driver.turnGyro(-90f, 2f);
						this.bot.driver.drive(8f, 4f, 1);
						this.bot.driver.turnGyro(-90f, 2f);
						this.bot.driver.drive(15f, 4f, -1);
						this.bot.driver.forward();
						cnt++;
					}
				}
			}
		}
		this.bot.driver.stop();
		Screen.clear();
		Screen.print("Both colors found!");
	}
	
	/**
	 * @param sensor sensor to get the values from.
	 * @return true if there was a color change detected that is not the first color found, false else.
	 */
	private boolean checkColor(SensorThread sensor) {
		float[] rgb = sensor.getRGB();
		r = rgb[0];
		g = rgb[1];
		b = rgb[2];
		
		if(Math.abs(r - rBack) + Math.abs(g- gBack) + Math.abs(b - bBack) > 0.05) {
			if(!foundFirst) {
				return true;
			}
			else {
				if(Math.abs(r - rFirst) + Math.abs(g- gFirst) + Math.abs(b - bFirst) > 0.45) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * @param sensor sensor to get values from.
	 * @return true if touch sensor is signaled, false else.
	 */
	public static boolean checkTouch(SensorThread sensor) {
		if(sensor.getTouch() == 1) {
			return true;
		}
		return false;
	}
	
	private boolean checkUs(SensorThread sensor) {
		if( sensor.getDistance() <= 0.1f) {
			return true;
		}
		return false;
	}
	
}