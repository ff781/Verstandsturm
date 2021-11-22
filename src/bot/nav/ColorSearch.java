package bot.nav;

import bot.Bot;
import bot.sen.SensorThread;
import lejos.hardware.Button;
import lejos.hardware.Sound;

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
		
		float[] rgbBack = this.bot.sensors.getRGB();
		rBack = rgbBack[0];
		gBack = rgbBack[1];
		bBack = rgbBack[2];
		
		bot.driver.forward();;
		
		while(Button.LEFT.isUp()) {
	
			
			//recognize different color
			if(checkColor(bot.sensors)) {
				if(!foundFirst) {
				//first color found
				this.bot.driver.stop();
				Sound.beepSequenceUp();
				float[] rgb = this.bot.sensors.getRGB();
				rFirst = rgb[0];
				gFirst = rgb[1];
				bFirst = rgb[2];
				foundFirst = true;
				}
				else {
					//second color found
					this.bot.driver.stop();
					Sound.beepSequence();
					break;
				}
			}
			
			//Robot touches wall
			if(checkTouch(bot.sensors)) {
				this.bot.driver.turnRotor(90f, 1f);;
				this.bot.driver.forward();;
			}
		}
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
		
		if((r - rBack) + (g- gBack) + (b - bBack) > 15 ) {
			if(!foundFirst) {
				return true;
			}
			else {
				if((r - rFirst) + (g- gFirst) + (b - bFirst) > 15 ) {
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
	private boolean checkTouch(SensorThread sensor) {
		if(sensor.getTouch() == 1) {
			return true;
		}
		return false;
	}
}