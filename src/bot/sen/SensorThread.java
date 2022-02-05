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
  public SensorWrap csmid;
  
  public int lastTouch;

  public SensorThread(EV3ColorSensor cs, EV3TouchSensor ts, EV3UltrasonicSensor us, EV3GyroSensor gs){
    this.gs = gs;

    this.csmid = cs != null ? new SensorWrap(cs.getColorIDMode()) : SensorWrap.empty();
	this.csm = cs != null ? new SensorWrap(cs.getRGBMode()) : SensorWrap.empty();
	this.tsm = ts != null ? new SensorWrap(ts.getTouchMode()) : SensorWrap.empty();
    this.usm = us != null ? new SensorWrap(us.getDistanceMode()) : SensorWrap.empty();
    this.gsm = gs != null ? new SensorWrap(gs.getAngleAndRateMode()) : SensorWrap.empty();
  }
  
  private SensorWrap[]g(){
	  //return new SensorWrap[]{csm,tsm,usm,gsm,};
	  return new SensorWrap[]{csm,tsm,usm,gsm,csmid,};
  }
  
  public void start() {
	  Thread t = new Thread(this);
	  t.setDaemon(true);
	  t.start();
  }

  @Override
  public void run(){
	  long millisDelay = 1;
	  long c = System.currentTimeMillis();
    try{
      while(true){
    	long n = System.currentTimeMillis();
    	if(n - c > millisDelay) {
	        for(SensorWrap sw : g()){
	        	if(sw!=null)
	        		sw.get();
	        }
	        c = n;
    	}
      }
    }catch(Exception e){
      e.printStackTrace();
    }
  }

  public float getTouch(){
	this.assertTs();
    return this.tsm.getSample();
  }
  
  public boolean touchee() {
	  int cur = (int)this.getTouch();
	  int diff = cur - lastTouch;
	  lastTouch = cur;
	  return diff==1;
  }
  
  public boolean isTouched() {
	  return this.getTouch() == 1;
  }

  public float[] getRGB(){
	  this.assertCs();
	return this.csm.getSamples();
  }
  
  public int getColorID() {
	this.assertCs();
	return (int)this.csmid.getSample();
  }

  public float getDistance(){
	this.assertUs();
    return this.usm.getSample();
  }

  public float getAngelV(){
	this.assertGs();
    return this.gsm.getSamples()[1];
  }

  public float getAngel(){
	this.assertGs();
    return this.gsm.getSamples()[0];
  }

  public void resetAngel(){
	this.assertGs();
    this.gs.reset();
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
  
}