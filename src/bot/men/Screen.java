package bot.men;

import lejos.hardware.lcd.*;

public class Screen {

  private static int x, y;

  public static void drawString(String s, int x, int y) {
    LCD.drawString(s, x, y);
  }

  public static void clear(){
    LCD.clear();
    x = 0;
    y = 0;
  }
}