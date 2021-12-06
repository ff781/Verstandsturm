package bot.nav;

import bot.*;
import bot.sen.*;
import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;

public class SLine {

  //public static final float K_p = .268f;
  private int initialSpeed = 240;
  private int currentSpeedL = initialSpeed;
  private int currentSpeedR = initialSpeed;
  private int maxSpeed = 600;
  //private float[] w = {0.163f, 0.283f, 0.133f};			// target rgb values for white
  private float[] w = {0.17f, 0.29f, 0.133f};			// target rgb values for white
  private float K_p = 120;								// constant for P-Regler
  

  public SLine() {

  }

  public void exec(Bot bot){
	  LCD.clear();			
	  System.out.println("Running line");
	  
	  //bot.driver.drive(distance, speed, direction, blocking);
	  bot.driver.forward(initialSpeed, initialSpeed);
	  
	  while (true) {
		  // Abbruchbedingungen
		  if (followLineTask(bot, K_p)) {
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
  
  /**
   * 
   * 
   * @param rgb
   * @return 0.0 if on white, 1.0 if on brown
   */
  private float brownOrWhite(float[] rgb) {
	  float[] brown = {0.02f, 0.0365f, 0.0039f};
	  float[] white = {0.163f, 0.283f, 0.1313f};
	  float threshold = 0.5f;
	  
	  float result = 0f;
	  
	  for (int i = 0; i < brown.length; i++) {
		  result += (rgb[i] - brown[i]) / (white[i] - brown[i]);
	  }
	  
	  if (result > 1.0f) return 1.0f;
	  if (result < 0.0f) return 0.0f;
	  
	  return result;
	  //return (result > threshold) ? 0.0f : 1.0f;
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
  
  private boolean followLineTask(Bot bot, float K_p) {
	  float[] r = bot.sensors.getRGB();
	  
	  if (isBlue(r)) return true;
	  				// measured brightness
	  float x_d = brownOrWhite(r);							// error
	  /*
	  if (x_d < 0.1f) {
		  // white
		  currentSpeedL = initialSpeed;
		  currentSpeedR = initialSpeed;
	  } else {
		  // brown
		  int y = Math.round(x_d * K_p);						// adjustment
		  if (Math.abs(currentSpeedL + y) < maxSpeed) currentSpeedL += y;
		  if (Math.abs(currentSpeedR - y) < maxSpeed) currentSpeedR -= y;
	  }
	  */
	  x_d = 2 * x_d - 1;
	  int y = Math.round(x_d * K_p);						// adjustment
	  if (Math.abs(currentSpeedL + y) <= maxSpeed && Math.abs(currentSpeedR - y) <= maxSpeed) {
		  currentSpeedL += y;
		  currentSpeedR -= y;
	  }
	  //System.out.println("rgb: " + rgb[0] + ", " + rgb[1] + ", " + rgb[2] + ". "
	  System.out.println(String.format("%.0f", x_d) + " " + currentSpeedL + " " + currentSpeedR);
	  bot.driver.forward(currentSpeedL, currentSpeedR);
	  
	  return false;
  }

  private boolean touchActivated(Bot bot) {
	  return bot.sensors.getTouch() == 1.0f;
  }
}