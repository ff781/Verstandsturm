package bot.men;

import static bot.nav.ParcourConstants.*;

import bot.*;
import bot.nav.*;
import bot.nav.line.Line;
import lejos.utility.*;
import lejos.hardware.*;

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

		while (true) {
			int select = this.select();			
			if(select >= 0 && select < Task.values.length)
			(Task.values[select]).exec(bot);			
			else if(select==-1) {
				break;
			}
		}
  }

  public static enum Task {
	ALL("EVERYTHING") {
		@Override
		void exec(Bot bot) {
			LINE.exec(bot);
			PUSH.exec(bot);
			RUNWAY.exec(bot);
			COLOR.exec(bot);
		}
	},
    LINE("Follow the line (not sus)"){

		@Override
		void exec(Bot bot) {
			Screen.clear();
			Line.exec(bot, true);
		}},
    PUSH("Bully the box"){

		@Override
		void exec(Bot bot) {
			new PushBox(bot).exec();
			
		}},
    RUNWAY("Walk the runway"){

		@Override
		void exec(Bot bot) {
			Bridge.exec(bot, false);
			
		}},
    COLOR("Find color speckles and purr"){

		@Override
		void exec(Bot bot) {
			Screen.clear();
			new ColorSearch(bot).search();
			
		}},
    SLINE("P-Regler simple line"){

		@Override
		void exec(Bot bot) {
			new SLine().exec(bot);
		}},
    DRIVE_TEST("Drive tests"){

		@Override
		void exec(Bot bot) {
			Screen.clear();
			Screen.prints("Testing, use the buttons to maneuver!");
			int mode = 0;
			float speedDefault = 2.2f;
			float[]speedFactors = {.25f,.5f,1,2,4};
			String[]modeDesc = {"drive&turn mode","rotor turn mode","change speed mode"};
			int i = 2;
			while(!Screen.wasPressed(Button.ID_ESCAPE)) {
				if (Button.ENTER.isDown()) {
					mode = (mode + 1)%2;
				}
				switch(mode) {
					case 0:
						if(Button.LEFT.isDown()) {
							bot.driver.drive_(90/Driver.DIST_TO_DEG, speedDefault * speedFactors[i], Driver.LEFT_DEGREES);
						}else if(Button.RIGHT.isDown()) {
							bot.driver.drive_(90/Driver.DIST_TO_DEG, speedDefault * speedFactors[i], Driver.RIGHT_DEGREES);
						}else if(Button.UP.isDown()) {
							bot.driver.drive_(10, speedDefault * speedFactors[i], Driver.FORWARD_DEGREES);
						}else if(Button.DOWN.isDown()) {
							bot.driver.drive_(10, speedDefault * speedFactors[i], Driver.BACKWARD_DEGREES);
						}
						break;
					case 1:
						if(Button.LEFT.isDown()) {
							bot.driver.turnUS(10, 1 * speedFactors[i]);
						}else if(Button.RIGHT.isDown()) {
							bot.driver.turnUS(-10, 1 * speedFactors[i]);
						}else if(Button.UP.isDown()) {
							bot.driver.turnUS(10, 1 * speedFactors[i]);
						}else if(Button.DOWN.isDown()) {
							bot.driver.turnUS(-10, 1 * speedFactors[i]);
						}
						break;
				}
				
			}
			
		}},
    SENSOR_TEST("Sensor tests"){

			@Override
			void exec(Bot bot) {
				Screen.clear();
				Screen.print("Testing, LRUP=CTGU!");
				int mode = -1;
				while(!Screen.wasPressed(Button.ID_ESCAPE)) {
					Screen.sleep(250);
					if(Button.LEFT.isDown()) {
						mode = 0;
					}else if(Button.RIGHT.isDown()) {
						mode = 1;
					}else if(Button.UP.isDown()) {
						mode = 2;
					}else if(Button.DOWN.isDown()) {
						mode = 3;
					}
					Screen.clear();
					switch(mode) {
						case 0:
							float[]rgb=bot.sensors.getRGB();
							Screen.prints("r:" + rgb[0]);
							Screen.prints("g:" + rgb[1]);
							Screen.prints("b:" + rgb[2]);
							//Screen.prints(colorClassify(rgb, LINE_WHITE, LINE_BROWN, LINE_BLUE) + "");
							Screen.prints(bot.sensors.getColorID()+"");
							Screen.prints(colorIDToString(bot.sensors.getColorID()));
							break;
						case 1:
							Screen.print(String.format("I am being touched %s%n", bot.sensors.getTouch()));
							break;
						case 2:
							Screen.print(String.format("Gyros%n current angle%s%n current angular speed%s%n", bot.sensors.getAngel(), bot.sensors.getAngelV()));
							break;
						case 3:
							Screen.print(String.format("Distance: %s%n", bot.sensors.getDistance()));
							break;
						case -1:
							Screen.print("No sensor selected");
					}
				}
			}},
    ;
	static Task[]values=values();
    String desc;
    Task(String desc){
      this.desc = desc;
    }
    public String getDesc(){return desc;}
    abstract void exec(Bot bot);
  }
}