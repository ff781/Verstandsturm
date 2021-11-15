package bot.men;
import java.util.*;

import bot.Bot;
import lejos.hardware.*;
import lejos.utility.*;

public class MainMenu extends TextMenu {

  public MainMenu(){
    super(superParam1(),1,"what you gonna do rolling down in the deep");
  }

  public static String[] superParam1 () {
	  Task[]v = Task.values();
	  String[] r = new String[v.length];
	  for(int i=0;i<v.length;i++) {
		r[i] = v[i].getDesc();
	  }
	  return r;
  }
  

  public void open(Bot bot){
    Sound.beepSequenceUp();

		while (Button.ESCAPE.isUp()) {

			Screen.clear();

			switch (Task.values()[this.select()]) {
		        case LINE: {

		        }
		        break;
		        case PUSH: {


		        }
		        break;
		        case RUNWAY: {


		        }
		        break;
		        case COLOR: {


		        }
		        break;
			}
			try {
				Thread.sleep(20);
			}catch(Exception e) {
				throw new RuntimeException(e);
			}
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