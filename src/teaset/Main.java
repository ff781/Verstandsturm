package teaset;

import lejos.hardware.Button;

import bot.*;
import bot.men.*;

public class Main {
	public static void main(String[]a) {

		Bot bot = new Bot();

		MainMenu mainMenu = new MainMenu();
		
		mainMenu.open(bot);
	}
}
