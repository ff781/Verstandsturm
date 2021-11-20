package bot.men;

import java.util.*;

import bot.Bot;
import bot.nav.SLine;
import lejos.utility.*;
import lejos.hardware.*;
import lejos.hardware.lcd.*;

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
			
			//LCD.clear();

			int select = this.select();
			
			if(select >= 0 && select < Task.values.length)
			(Task.values[select]).exec(bot);
			try {
				Thread.sleep(20);
			}catch(Exception e) {
				throw new RuntimeException(e);
			}
		}
  }

  public static enum Task {
    LINE("Follow the line (not sus)"){

		@Override
		void exec(Bot bot) {
			// TODO Auto-generated method stub
			
		}},
    PUSH("Bully the box"){

		@Override
		void exec(Bot bot) {
			// TODO Auto-generated method stub
			
		}},
    RUNWAY("Walk the runway"){

		@Override
		void exec(Bot bot) {
			// TODO Auto-generated method stub
			
		}},
    COLOR("Find color speckles and purr"){

		@Override
		void exec(Bot bot) {
			// TODO Auto-generated method stub
			
		}},
    SLINE("P-Regler simple line"){

		@Override
		void exec(Bot bot) {
			new SLine().exec(bot);
		}},
    DRIVE_TEST("Drive tests"){

		@Override
		void exec(Bot bot) {
			LCD.clear();
			System.out.println("Testing, use the buttons to maneuver!");
			int mode = 0;
			while(Button.ESCAPE.isUp()) {
				if (Button.ENTER.isDown()) {
					mode = (mode + 1)%2;
				}
				switch(mode) {
					case 0:
						if(Button.LEFT.isDown()) {
							bot.driver.turn(90, 1);
						}else if(Button.RIGHT.isDown()) {
							bot.driver.turn(-90, 1);
						}else if(Button.UP.isDown()) {
							bot.driver.drive(1, 1, 1);
						}else if(Button.DOWN.isDown()) {
							bot.driver.drive(1, 1, -1);
						}
						break;
					case 1:
						if(Button.LEFT.isDown()) {
							bot.driver.turnRotor(90, 90);
						}else if(Button.RIGHT.isDown()) {
							bot.driver.turnRotor(-90, 90);
						}else if(Button.UP.isDown()) {
						}else if(Button.DOWN.isDown()) {
						}
						break;
				}
				
			}
			
		}},
    SENSOR_TEST("Sensor tests"){

			@Override
			void exec(Bot bot) {
				LCD.clear();
				System.out.println("Testing, LRUP=CTGU!");
				int mode = -1;
				while(Button.ESCAPE.isUp()) {
					if(Button.ENTER.isDown()) mode = -1;
					if(Button.LEFT.isDown()) {
						mode = 0;
					}else if(Button.RIGHT.isDown()) {
						mode = 1;
					}else if(Button.UP.isDown()) {
						mode = 2;
					}else if(Button.DOWN.isDown()) {
						mode = 3;
					}
					LCD.clear();
					switch(mode) {
						case 0:
							
							System.out.printf("RGB: %s %n",Arrays.toString(bot.sensors.getRGB()));
							break;
						case 1:
							
							System.out.printf("I am being touched %s%n", bot.sensors.getTouch());
							break;
						case 2:
							System.out.printf("Gyros%n current angle%s%n current angular speed%s%n", bot.sensors.getAngel(), bot.sensors.getAngelV());
							break;
						case 3:
							System.out.printf("Distance: %s%n", bot.sensors.getDistance());
							break;
						case -1:
							System.out.println("No sensor selected");
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