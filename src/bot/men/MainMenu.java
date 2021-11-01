package bot.men;
import java.util.*;

import bot.Bot;
import lejos.hardware.*;
import lejos.hardware.lcd.*;
import lejos.utility.*;

public class MainMenu extends TextMenu {

  public MainMenu(){
    super(Arrays.stream(Task.values()).map(Task::getDesc).toArray(String[]::new),0,"what you gonna do rolling down in the deep");
  }

  public void open(Bot bot){
    Sound.beepSequenceUp();

		while (Button.ESCAPE.isUp()) {

			BrickScreen.clearScreen();

			switch (Task.values()[this.select()]) {
        case Task.LINE: {

        }
        break;
        case Task.PUSH: {


        }
        break;
        case Task.RUNWAY: {


        }
        break;
        case Task.LINE: {


        }
        break;
			}

			Thread.sleep(20);
		}
  }

  public static enum Task {
    LINE("Follow the line (not sus)"){},
    PUSH("Bully the box"){},
    RUNWAY("Walk the runway"){},
    COLOR("Find color speckles and purr"){},
    ;
    String desc;
    Task(String desc){
      this.desc = desc;
    }
    public String getDesc(){return desc;}
  }
}