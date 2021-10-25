public class SensorThread implements Runnable{

  public Sensor gs;

  public SensorWrap csw;
  public SensorWrap tsw;
  public SensorWrap usw;
  public SensorWrap gsw;

  public SensorThread(EV3ColorSensor cs, EV3TouchSensor ts, EV3UltrasonicSensor us, EV3GyroSensor gs){
    this.gs = gs;

    this.csm = new SensorWrap(cs.getRGBMode());
    this.tsm = new SensorWrap(ts.getTouchMode());
    this.usm = new SensorWrap(us.getDistanceMode());
    this.gsm = new SensorWrap(gs.getAngleAndRateMode());

    this.gs.reset();
  }

  @Override
  public void run(){
    try{
      while(true){
        for(SensorWrap sw : new SensorWrap[]{csm,tsm,usm,gsm}){
          sw.get();
        }
        Thread.sleep(25);
      }
    }catch(Exception e){
      e.printStackTrace();
    }
  }

  public float getTouch(){
    return this.tsw.getSample();
  }

  public float[] getRGB(){
    return this.csw.getSamples();
  }

  public float getDistance(){
    return this.usm.getSample();
  }

  public float getAngelV(){
    return this.gsm.getSample();
  }

  public void resetAngel(){
    this.gs.reset();
  }

  public float getAngel(){
    return this.gsm.getSamples()[1];
  }
}