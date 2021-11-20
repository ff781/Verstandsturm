package bot;

import lejos.hardware.motor.*;

public class Driver {

	public static final boolean BLOCKING_DEFAULT = true;

	//scaling constants for turning degree and turning speed
	public static final float turnDegFactorL = 5.88f;
    public static final float turnSpeedFactorL = 150;
	public static final float turnDegFactorR = 5.88f;
    public static final float turnSpeedFactorR = 150;
	public static final float turnDegFactorUSM = 5;
    public static final float turnSpeedFactorUSM = 100;
    
    //scaling constant for drive distance
    public static final float driveFactor = 36 / turnDegFactorL;

	Bot bot;

	public Driver(Bot bot) {
		this.bot = bot;
	}

	/*
	 * turns the robot
	 * @param deg: degrees
	 * @param speed: full rotation per second
	 * @param blocking: method blocks
	 */
	public void turn(float deg, float speed){
		this.turn(deg, speed, BLOCKING_DEFAULT);
	}
	public void turn(float deg, float speed, boolean blocking){
		Thread threadL = new RotateThread(this.bot.lMotor, deg*turnDegFactorL, speed*turnSpeedFactorL);
		Thread threadR = new RotateThread(this.bot.rMotor, -deg*turnDegFactorR, speed*turnSpeedFactorR);
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
	
	public void directionChange(float y) {
		int lSpeed = this.bot.lMotor.getSpeed();
		int rSpeed = this.bot.rMotor.getSpeed();
		
		this.bot.lMotor.setSpeed(lSpeed + y);
		this.bot.rMotor.setSpeed(rSpeed - y);
	}

	/*
	 * drives forward or backward in a straight line
	 * @param distance: distance to drive
	 * @param speed: centimeter per second
	 * @param direction: +1 forward, -1 backward
	 * @param blocking: method blocks
	 */
	public void drive(float distance, float speed, int direction){
	  this.drive(distance, speed, direction, BLOCKING_DEFAULT);
	}
	public void drive(float distance, float speed, int direction, boolean blocking){
		distance *= driveFactor * direction;
		Thread threadL = new RotateThread(this.bot.lMotor, distance*turnDegFactorL, speed*turnSpeedFactorL);
		Thread threadR = new RotateThread(this.bot.rMotor, distance*turnDegFactorR, speed*turnSpeedFactorR);
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
	
	/*
	 * turns rotor
	 * @param deg: degrees
	 * @param speed: full rotation per second
	 * @param blocking: method blocks
	 */
	public void turnRotor(float deg, float speed){
		this.turn(deg, speed, BLOCKING_DEFAULT);
	}
	public void turnUS(float deg, float speed, boolean blocking){
		Thread[]threads = new Thread[]{
				new RotateThread(this.bot.rotor, deg*turnDegFactorUSM, speed*turnSpeedFactorUSM),
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
			motor.rotate((int)rad);
		}
	}

}
