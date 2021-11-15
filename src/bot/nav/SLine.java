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
	  float w = 0.5;								// target brightness
	  float K_p = 0.3;								// constant for P-Regler
	  
	  while (Button.ESCAPE.isUp()) {
		  float[] rgb = bot.sensors.getRGB();
		  r = getBrightness(rgb);					// measured brightness
		  float x_d = r - w;						// error
		  float y = x_d * K_p;						// adjustment
		  System.out.println("rgb: " + rgb[0] + ", " + rgb[1] + ", " + rgb[2] + ". x_d: " + x_d + ", y: " + y);
		  bot.driver.directionChange(y);
	  }
  }
  
  private float getBrightness(float[] rgb) {
	  return (rgb[0] + rgb[1] + rgb[2]) / 3;
  }

}