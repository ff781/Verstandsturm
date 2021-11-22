package bot;

import lejos.hardware.*;

import lejos.hardware.lcd.*;
import lejos.hardware.motor.*;
import lejos.hardware.port.*;
import lejos.hardware.sensor.*;
import lejos.hardware.device.*;
import lejos.robotics.navigation.DifferentialPilot;

import java.util.*;

import bot.men.*;
import bot.sen.SensorThread;
import bot.por.PortUtil;

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
	public Map<Port,Boolean> plugged = new HashMap<>();

	public MainMenu mainMenu;

	public Driver driver;

	public Bot() {
		this.lMotor = new EV3LargeRegulatedMotor(MotorPort.A);
		this.rMotor = new EV3LargeRegulatedMotor(MotorPort.B);
		this.rotor = new EV3MediumRegulatedMotor(MotorPort.C);
		
//		for(Port p : new Port[] {SensorPort.S1, SensorPort.S2, SensorPort.S3, SensorPort.S4}) {
//			this.plugged.put(p, PortUtil.portPlugged(p));
//		}

//		if(this.plugged.get(SensorPort.S1))
//			this.colorS = new EV3ColorSensor(SensorPort.S1);
//		if(this.plugged.get(SensorPort.S2))
//			this.touchS = new EV3TouchSensor(SensorPort.S2);
//		if(this.plugged.get(SensorPort.S3))
//			this.gyroS = new EV3GyroSensor(SensorPort.S3);
//		if(this.plugged.get(SensorPort.S4))
//			this.ultraS = new EV3UltrasonicSensor(SensorPort.S4);
		try {
			this.colorS = new EV3ColorSensor(SensorPort.S1);
		}catch (Exception e){}
		try {
			this.touchS = new EV3TouchSensor(SensorPort.S2);
		}catch (Exception e){}
		try {
			this.gyroS = new EV3GyroSensor(SensorPort.S3);
		}catch (Exception e){}
		try {
			this.ultraS = new EV3UltrasonicSensor(SensorPort.S4);
		}catch (Exception e){}

		this.sensors = new SensorThread(colorS, touchS, ultraS, gyroS);

		this.mainMenu = new MainMenu();
		this.driver = new Driver(this);

		this.sensors.start();
		
		Screen.startButtonThread();
	}

}
