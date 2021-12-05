package bot;

import lejos.hardware.motor.*;
import util.meth.Meth;

/*
 * degrees are in degrees, positive is counterclockwise="left"
 * linear speed is in 3cm/s
 * distance is in 1cm
 * error is on average ~.07, worst case ~.15
 */
public class Driver {
	
	public static final float BACKWARD_DEGREES = 180;
	public static final float FORWARD_DEGREES = 0;
	
	

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
	 * @param speed: 45 degrees per second
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
		this.drive_(distance, speed, (direction - 1) * (-90), blocking);
	}
	//drive with more complex direction parameter
	//named drive_ for backwards compatibility
	/*
	 * drives forward or backward
	 * @param distance: distance to drive
	 * @param speed: centimeter per second of the faster track
	 * @param rotation: 0 deg forward, 180 deg backward, other degree values interpolate in between
	 * @param blocking: method blocks
	 */
	public void drive_(float distance, float speed, float rotation) {
		this.drive_(distance, speed, rotation, BLOCKING_DEFAULT);
	}
	public void drive_(float distance, float speed, float rotation, boolean blocking) {
		distance *= driveFactor;
		//offset to actual 1,1 position for motor 
		rotation += 45;
		float rad = Meth.degToRad(rotation);
		float lscalar = Meth.sin(rad);
		float rscalar = Meth.cos(rad);
		Thread threadL = new RotateThread(this.bot.lMotor, lscalar*distance*turnDegFactorL, lscalar*speed*turnSpeedFactorL);
		Thread threadR = new RotateThread(this.bot.rMotor, rscalar*turnDegFactorR, rscalar*speed*turnSpeedFactorR);
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
	 * turns ultrasonic(US) sensor
	 * @param deg: degrees
	 * @param speed: 45 degrees per second
	 * @param blocking: method blocks
	 */
	public void turnUS(float deg, float speed) {
		this.turnUS(deg, speed, BLOCKING_DEFAULT);
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
	public void USStop() {
		this.bot.rotor.stop();
	}
	public boolean isUSMoving() {
		return this.bot.rotor.isMoving();
	}
	
	public void driveForever(float speed) {
		this.bot.lMotor.setSpeed(speed * turnSpeedFactorL);
		this.bot.rMotor.setSpeed(speed * turnSpeedFactorR);
		this.bot.lMotor.forward();
		this.bot.rMotor.forward();
	}
	public void driveStop() {
		this.bot.lMotor.stop();
		this.bot.rMotor.stop();
	}
	public boolean isMoving() {
		return this.bot.lMotor.isMoving() || this.bot.rMotor.isMoving();
	
	public void forward() {
		bot.lMotor.setSpeed(720);
		bot.rMotor.setSpeed(720);
		bot.lMotor.forward();
		bot.rMotor.forward();
	}
	
	public void stop() {
		bot.lMotor.stop();
		bot.rMotor.stop();
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
