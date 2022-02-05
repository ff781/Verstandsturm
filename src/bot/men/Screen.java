package bot.men;

import java.io.File;
import java.util.*;

import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.lcd.*;

public class Screen {

  private static int x, y;
  public static Map<Integer, Boolean> wasPressed = new HashMap<>();
  static{
	  for(int i : new int[] {Button.ID_DOWN,Button.ID_LEFT,Button.ID_RIGHT,Button.ID_UP,Button.ID_ENTER,Button.ID_ESCAPE}) {
		wasPressed.put(i, false);
	  }
	  
	  Sound.setVolume(Sound.VOL_MAX);
  }
  
  public static void startButtonThread() {
	  Thread t = new Thread(new Runnable() {
		  public void run() {
			  while(true) {
				  int a = Button.waitForAnyPress();
				  for(int i : new int[] {Button.ID_DOWN,Button.ID_LEFT,Button.ID_RIGHT,Button.ID_UP,Button.ID_ENTER,Button.ID_ESCAPE}) {
					wasPressed.put(i, (a & i)!=0);
				  }
			  }
		  }
	  });
	  t.setDaemon(true);
	  t.start();
  }

  public static void print(String s, int x, int y) {
    LCD.drawString(s, x, y);
  }
  
  public static void print(String s) {
	LCD.drawString(s, x, y);
  }
  
  public static void prints(String s) {
	LCD.drawString(s, x, y++);
  }
  
  public static void sout_println(String s) {
	  System.out.println(s);
  }
  
  public static void sleep(long millis) {
	  try {
		  Thread.sleep(millis);
	  }catch(Exception e) {
		  throw new RuntimeException(e);
	  }
  }
  
  /*
   * Consumes the press, and checks whether the button was pressed since last check or consummation
   */
  public static boolean wasPressed(final int id) {
	 boolean r = wasPressed.get(id);
	 if(r) wasPressed.put(id,false);
	 return r;
  }
  
  public static void beep() {
	  Sound.playSample(new File("sonde.wav"));
	  //Sound.beep();
  }

  public static void clear(){
    LCD.clear();
    x = 0;
    y = 0;
  }
  
  public static void sout_clear() {
	  for(int i=0;i<8;i++)
		  System.out.println();
  }
}