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
	  while(Button.ESCAPE.isUp()) {
		  float[]rgb = bot.sensors.getRGB();
		  
	  }
  }

}