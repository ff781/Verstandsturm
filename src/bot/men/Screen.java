package bot.men;

import java.util.*;

import lejos.hardware.Button;
import lejos.hardware.lcd.*;

public class Screen {

  private static int x, y;
  private static Map<Integer, Boolean> wasPressed = new HashMap<>();
  static{
	  for(int i : new int[] {Button.ID_DOWN,Button.ID_LEFT,Button.ID_RIGHT,Button.ID_UP,Button.ID_ENTER,Button.ID_ESCAPE}) {
		wasPressed.put(i, false);  
	  }
  }
  
  public static void startButtonThread() {
	  Thread t = new Thread(new Runnable() {
		  public void run() {
			  for(int i : new int[] {Button.ID_DOWN,Button.ID_LEFT,Button.ID_RIGHT,Button.ID_UP,Button.ID_ENTER,Button.ID_ESCAPE}) {
				int a = Button.waitForAnyPress();
				wasPressed.put(i, (a & i)!=0);
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
	  System.out.println(s);
	//LCD.drawString(s, x, y);
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

  public static void clear(){
    LCD.clear();
    x = 0;
    y = 0;
  }
}