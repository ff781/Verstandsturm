import java.util.*;

public class MainMenu extends TextMenu {

  public MainMenu(){
    super(Arrays.stream(Task.values()).map(Task::getDesc).toArray(String[]::new),0,"what you gonna do rolling down in the deep");
  }



  public static enum Task {
    LINE("Follow the line (not sus)"),
    PUSH("Bully the box"),
    RUNWAY("Walk the runway"),
    COLOR("Find color speckles and purr"),
    ;
    String desc;
    public Task(String desc){
      this.desc = desc;
    }
    public String getDesc(){return desc;}
  }
}