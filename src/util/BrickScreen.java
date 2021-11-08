package util;

import lejos.hardware.lcd.LCD;

public class BrickScreen {

	private static int xPos = 0;
	private static int yPos = 0;
	
	public static void clearScreen() {
		LCD.clear();
		xPos = 0;
		yPos = 0;
	}
	
	public static void showStringOnScreen(String str, int xPos, int yPos) {
		LCD.drawString(str, xPos, yPos);
	}
	
	public static void showIntOnScreen(int i, int xPos, int yPos) {
		LCD.drawInt(i, xPos, yPos);
	}
	
	public static void show(String str) {
		LCD.drawString(str, xPos, yPos++);
	}
	
}
