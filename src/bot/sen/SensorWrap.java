
public class SensorWrap {

  public SensorMode mode;
  public float[] samples;

  public SensorWrap(SensorMode mode){
    this.mode = mode;
    this.samples = new float[this.mode.sampleSize()];
  }

  public <MODE_TYPE> SensorWrap(BaseSensor sensor, MODE_TYPE mode){
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