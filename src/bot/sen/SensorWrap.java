package bot.sen;

import lejos.robotics.*;
import lejos.hardware.sensor.*;

public class SensorWrap {

  public SampleProvider mode;
  public float[] samples;

  public <T> SensorWrap(SampleProvider mode){
    this.mode = mode;
    this.samples = new float[this.mode.sampleSize()];
  }

  public SensorWrap(BaseSensor sensor, String mode){
    this(sensor.getMode(mode));
  }

  public void get(){
    this.mode.fetchSample(samples,0);
  }

  public float getSample(){
    return samples[0];
  }

  public float[] getSamples(){
    return samples;
  }

}