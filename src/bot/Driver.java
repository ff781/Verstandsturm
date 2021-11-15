package bot;

import lejos.hardware.motor.*;

public class Driver {

	public static final boolean BLOCKING_DEFAULT = true;

    public static final float turnFactor = 1;
	public static final float driveFactor = 1;

	Bot bot;

	public Driver(Bot bot) {
		this.bot = bot;
	}

	public void turn(float rad, float speed){
		this.turn(rad, speed, BLOCKING_DEFAULT);
	}

	public void turn(float rad, float speed, boolean blocking){
		Thread threadL = new RotateThread(this.bot.lMotor, rad, speed);
		Thread threadR = new RotateThread(this.bot.rMotor, -rad, speed);;
		Thread[]threads = new Thread[]{
				threadL, threadR,
		};
		for(Thread t:threads)t.start();
		if(blocking) {
			try{
				for(Thread t:threads)t.join();
			}catch(Exception e){
				throw new RuntimeException(e);
			};
		}
	}

	public void driveForward(float distance, float speed, int direction){
	  this.driveForward(distance, speed, direction, BLOCKING_DEFAULT);
	}

	public void driveForward(float distance, float speed, int direction, boolean blocking){

	}

	static class RotateThread extends Thread {

		BaseRegulatedMotor motor;
		float rad;
		float speed;

		public RotateThread(BaseRegulatedMotor motor, float rad, float speed) {
			this.motor = motor;
			this.rad = rad;
			this.speed = speed;
		}
		public void run(){
			motor.setSpeed(speed);
			motor.setSpeed(speed);
			motor.rotate((int)rad);
			motor.rotate(-(int)rad);
		}
	}

}
