package bot.nav;

import bot.Bot;
import bot.sen.SensorThread;
import lejos.hardware.Button;
import lejos.hardware.Sound;

public class PushBox {

	Bot bot;
	
	float r, rBack;
	float g, gBack;
	float b, bBack;
	
	public PushBox(Bot bot) {
		this.bot = bot;
	}
	
	public void start() {
		
		float[] rgbBack = this.bot.sensors.getRGB();
		rBack = rgbBack[0];
		gBack = rgbBack[1];
		bBack = rgbBack[2];
		
		this.bot.driver.turnRotor(90f, 2f);
		this.bot.driver.forward();
		
		while(Button.ESCAPE.isUp()) {
			if (checkTouch(bot.sensors)) {
				this.bot.driver.turnRotor(-90f, 2f);
				this.bot.driver.forward();
			}
			
			if (checkColor(bot.sensors)) {
				Sound.beep();
				bot.driver.stop();
			}
		}
	}
	
	private boolean checkColor(SensorThread sensor) {
		float[] rgb = sensor.getRGB();
		r = rgb[0];
		g = rgb[1];
		b = rgb[2];
		
		if((r - rBack) + (g- gBack) + (b - bBack) > 0.05 ) {
			return true;
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
