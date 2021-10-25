package bot;

import lejos.hardware.*;
import lejos.hardware.lcd.*;
import lejos.hardware.motor.*;
import lejos.hardware.port.*;
import lejos.robotics.navigation.DifferentialPilot;

public class Bot {

	public EV3LargeRegulatedMotor lMotor;
	public EV3LargeRegulatedMotor rMotor;
	public EV3MediumRegulatedMotor rotor;

	public SensorThread sensors;
	public EV3ColorSensor colorS;
	public EV3TouchSensor touchS;
	public EV3UltrasonicSensor ultraS;
	public EV3GyroSensor gyroS;

	public MainMenu mainMenu;

	public Bot() {
		this.lMotor = new EV3LargeRegulatedMotor(MotorPort.A);
		this.rMotor = new EV3LargeRegulatedMotor(MotorPort.B);
		this.rotor = new EV3MediumRegulatedMotor(MotorPort.C);

		colorS = new EV3ColorSensor(SensorPort.S1);
		touchS = new EV3TouchSensor(SensorPort.S2);
		ultraS = new EV3UltrasonicSensor(SensorPort.S3);
		gyroS = new EV3GyroSensor(SensorPort.S4)

		this.sensors = new SensorThread(colorS, touchS, ultraS, gyroS);
		this.mainMenu = new MainMenu();
		this.driver = new Driver(this);

		new Thread(this.sensors).start();
	}

}
