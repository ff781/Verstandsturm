package bot.sen;

import lejos.hardware.Button;
import lejos.hardware.sensor.*;
import lejos.utility.*;
import bot.men.Screen;

public class SensorThread implements Runnable{

  public EV3GyroSensor gs;

  public SensorWrap csm;
  public SensorWrap tsm;
  public SensorWrap usm;
  public SensorWrap gsm;

  public SensorThread(EV3ColorSensor cs, EV3TouchSensor ts, EV3UltrasonicSensor us, EV3GyroSensor gs){
    this.gs = gs;

	this.csm = cs != null ? new SensorWrap(cs.getRGBMode()) : SensorWrap.empty();
	this.tsm = ts != null ? new SensorWrap(ts.getTouchMode()) : SensorWrap.empty();
    this.usm = us != null ? new SensorWrap(us.getDistanceMode()) : SensorWrap.empty();
    this.gsm = gs != null ? new SensorWrap(gs.getAngleAndRateMode()) : SensorWrap.empty();

    this.resetGs();
  }
  
  public void start() {
	  Thread t = new Thread(this);
	  t.setDaemon(true);
	  t.start();
  }

  @Override
  public void run(){
    try{
      while(true){
        for(SensorWrap sw : new SensorWrap[]{csm,tsm,usm,gsm}){
        	if(sw!=null)
        		sw.get();
        }
        Thread.sleep(25);
      }
    }catch(Exception e){
      e.printStackTrace();
    }
  }

  public float getTouch(){
	  this.assertTs();
    return this.tsm.getSample();
  }

  public float[] getRGB(){
	  this.assertCs();
	return this.csm.getSamples();
  }

  public float getDistance(){
	  this.assertUs();
    return this.usm.getSample();
  }

  public float getAngelV(){
	  this.assertGs();
    return this.gsm.getSample();
  }

  public void resetAngel(){
	this.assertGs();
    this.gs.reset();
  }

  public float getAngel(){
	this.assertGs();
    return this.gsm.getSamples()[0];
  }
  
  public void problem(String descr) {
	  Screen.clear();
	  System.out.println(descr);
	  while(Button.ENTER.isUp()) {
		  if(Button.ESCAPE.isDown()) {
			  System.exit(0);
		  }
	  }
  }
  
  public void assertCs() {
	  if(!this.csm.isActive() && !this.csm.isIgnorant()) {
		  this.problem("cs not available");
		}
  }
  public void assertTs() {
	  if(!this.tsm.isActive() && !this.tsm.isIgnorant()) {
		  this.problem("ts not available");
		}
  }
  public void assertGs() {
	  if(!this.gsm.isActive() && !this.gsm.isIgnorant()) {
		  this.problem("gs not available");
	}
  }
  public void assertUs() {
	if(!this.usm.isActive() && !this.usm.isIgnorant()) {
		this.problem("us not available");
	}
  }
  
  public void resetGs() {
	  if(gs!=null)
	  this.gs.reset();
  }
}