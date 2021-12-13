package bot.nav;

import bot.Bot;
import bot.sen.SensorThread;
import lejos.hardware.Button;
import lejos.hardware.Sound;

public class PushBox {

	private Bot bot;
	
	float conts = 5f;
	
	private float r, rBack;
	private float g, gBack;
	private float b, bBack;
	private float cornerAngle;
	
	Phase phase;
	
	public PushBox(Bot bot) {
		this.bot = bot;
		this.phase = Phase.FIRST;
	}
	
	public void start() {
		
		float[] rgbBack = this.bot.sensors.getRGB();
		rBack = rgbBack[0];
		gBack = rgbBack[1];
		bBack = rgbBack[2];
		
		this.bot.driver.forward();
		
		while(Button.ESCAPE.isUp()) {
			
			if (this.phase == Phase.THIRD) {
			Sound.twoBeeps();
			break;	
			}
			
			
			if (checkTouch(bot.sensors)) {
				if(this.phase == Phase.FIRST) {
					this.bot.driver.turn(-(90f), 2f);
					this.bot.driver.forward();
				}
				else if (this.phase == Phase.SECOND) {
					this.bot.driver.turn(90f, 2f);
					this.bot.driver.drive(8f, 2f, 1);
					this.bot.driver.turn(90f, 2f);
					this.bot.driver.forward();
				}
			}
			
			if (checkColor(bot.sensors)) {
				if(this.phase == Phase.FIRST) {
				this.bot.sensors.resetAngel();
				this.phase = Phase.SECOND;
				this.bot.driver.turn(180f, 2f);
				this.bot.driver.forward();
				Sound.beep();
				}
				else if (this.phase == Phase.SECOND){
					this.cornerAngle = this.bot.sensors.getAngel();
					Sound.buzz();
					this.phase = Phase.THIRD;
				}
			}
			
		}
		this.bot.driver.stop();
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
	
	enum Phase {
		FIRST,
		SECOND,
		THIRD
	}
	
}
