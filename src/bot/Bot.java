package bot;

import lejos.hardware.*;

import lejos.hardware.lcd.*;
import lejos.hardware.motor.*;
import lejos.hardware.port.*;
import lejos.hardware.sensor.*;
import lejos.robotics.navigation.DifferentialPilot;

import bot.men.MainMenu;
import bot.sen.SensorThread;

public class Bot {

	public static final float SAGITTAL_LENGTH = 1;
	public static final float CORONAL_LENGTH = 1;
	public static final float AXIAL_LENGTH = 1;

	public static final float WHEEL_DIAMETER = 1;

	public EV3LargeRegulatedMotor lMotor;
	public EV3LargeRegulatedMotor rMotor;
	public EV3MediumRegulatedMotor rotor;

	public SensorThread sensors;
	public EV3ColorSensor colorS;
	public EV3TouchSensor touchS;
	public EV3UltrasonicSensor ultraS;
	public EV3GyroSensor gyroS;

	public MainMenu mainMenu;

	public Driver driver;

	public Bot() {
		this.lMotor = new EV3LargeRegulatedMotor(MotorPort.A);
		this.rMotor = new EV3LargeRegulatedMotor(MotorPort.B);
		this.rotor = new EV3MediumRegulatedMotor(MotorPort.C);

		try{
			colorS = new EV3ColorSensor(SensorPort.S1);
			touchS = new EV3TouchSensor(SensorPort.S2);
			gyroS = new EV3GyroSensor(SensorPort.S3);
			ultraS = new EV3UltrasonicSensor(SensorPort.S4);

			this.sensors = new SensorThread(colorS, touchS, ultraS, gyroS);
		}catch(Exception e){
			//
		}

		this.mainMenu = new MainMenu();
		this.driver = new Driver(this);

		new Thread(this.sensors).start();
	}

}
