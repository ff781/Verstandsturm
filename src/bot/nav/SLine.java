package bot.nav;

import bot.*;
import bot.sen.*;
import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;

public class SLine {

  //public static final float K_p = .268f;
  private int speed = 480;
  //private float[] w = {0.163f, 0.283f, 0.133f};			// target rgb values for white
  private float[] w = {0.17f, 0.29f, 0.133f};			// target rgb values for white
  private float K_p = 5000;								// constant for P-Regler
  

  public SLine() {

  }

  public void exec(Bot bot){
	  LCD.clear();			
	  System.out.println("Running line");
	  
	  //bot.driver.drive(distance, speed, direction, blocking);
	  bot.driver.forward(speed, speed);
	  
	  while (true) {
		  // Abbruchbedingungen
		  if (followLineTask(bot, w, K_p)) {
			  LCD.clear();
			  bot.driver.stop();
			  return;
		  }
		  
		  if (touchActivated(bot)) {
			  // TODO: Hindernis umfahren
			  LCD.clear();
			  bot.driver.stop();
			  return;
		  }
		  
		  if (Button.ESCAPE.isDown()) {
			  LCD.clear();
			  bot.driver.stop();
			  return;
		  }
	  }
  }
  
  private boolean isBlue(float[] rgb) {
	  return rgb[0] < 0.04f && rgb[1] < 0.11f && rgb[2] > 0.06f ;
  }
  
  private float getBrightness(float[] rgb) {
	  return (rgb[0] + rgb[1] + rgb[2]) / 3;
  }
  
  private float getMeanError(float[] r, float[] w) {
	  assert r.length == w.length;
	  float accumulator = 0;
	  for (int i = 0; i < r.length; i++) {
		  accumulator += Math.abs(r[i] - w[i]);
	  }
	  return accumulator / r.length;
  }
  
  private boolean motorsStopped(Bot bot) {
	  return !bot.lMotor.isMoving() && !bot.rMotor.isMoving();
  }
  
  private boolean followLineTask(Bot bot, float[] w, float K_p) {
	  float[] r = bot.sensors.getRGB();
	  
	  if (isBlue(r)) return true;
	  				// measured brightness
	  float x_d = getMeanError(r, w);						// error
	  int y = Math.round(x_d * K_p);						// adjustment
	  //System.out.println("rgb: " + rgb[0] + ", " + rgb[1] + ", " + rgb[2] + ". "
	  System.out.println("d " + String.format("%.3f", x_d) + " y " + y);
	  bot.driver.forward(speed + y, speed - y);
	  
	  return false;
  }

  private boolean touchActivated(Bot bot) {
	  return bot.sensors.getTouch() == 1.0f;
  }
}