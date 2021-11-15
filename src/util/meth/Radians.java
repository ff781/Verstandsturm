package util.meth;

//probably too verbose

public class Radians {

  public final float rad;

  public Radians(float rad) {
    this.rad = rad;
  }

  public float degrees(){
    return (float)((this.rad / Math.PI)*720);
  }

}