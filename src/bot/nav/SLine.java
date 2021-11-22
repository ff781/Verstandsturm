package bot.nav;

import bot.*;
import bot.sen.*;
import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;

public class SLine {

  public static final float K_p = .268f;
  

  public SLine() {

  }

  public void exec(Bot bot){
	  LCD.clear();			
	  System.out.println("Running simple line subroutine");
	  float w = 0.5f;								// target brightness
	  float K_p = 0.3f;								// constant for P-Regler
	  
	  float distance = 100;							// in cm
	  float speed = 2;								// cm per second
	  int direction = 1;							// 1: forward
	  
	  bot.driver.drive(distance, speed, direction);
	  
	  while (true) {
		  if (followLineTask(bot, w, K_p)) return;
		  //if (Button.ESCAPE.isDown()) return;
	  }
  }
  
  private float getBrightness(float[] rgb) {
	  return (rgb[0] + rgb[1] + rgb[2]) / 3;
  }
  
  private boolean isBlue(float[] rgb) {
	  return rgb[0] < 0.04f && rgb[1] < 0.11f && rgb[2] > 0.06f ;
  }
  
  private boolean followLineTask(Bot bot, float w, float K_p) {
	  float[] rgb = bot.sensors.getRGB();
	  
	  if (isBlue(rgb)) return true;
	  
	  float r = getBrightness(rgb);					// measured brightness
	  float x_d = r - w;						// error
	  float y = x_d * K_p;						// adjustment
	  //System.out.println("rgb: " + rgb[0] + ", " + rgb[1] + ", " + rgb[2] + ". "
	  System.out.println("x_d: " + x_d + ", y: " + y);
	  bot.driver.directionChange(y);
	  
	  return false;
  }

}