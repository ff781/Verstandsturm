package bot;

import lejos.hardware.*;

import lejos.hardware.lcd.*;
import lejos.hardware.motor.*;
import lejos.hardware.port.*;
import lejos.hardware.sensor.*;
import lejos.hardware.device.*;

import lejos.robotics.chassis.*;
import lejos.robotics.navigation.*;

import java.util.*;

import bot.men.*;
import bot.sen.SensorThread;
import bot.por.PortUtil;

public class Bot {

	public static final float SAGITTAL_LENGTH = 1;
	public static final float CORONAL_LENGTH = 1;
	public static final float AXIAL_LENGTH = 1;
	
	public static final float TRACK_WIDTH = 11.5f; 
	public static final float WHEEL_DIAMETER = 3.5f;
	
	public static final int SENSOR_CONNECT_MAX_RETRIES = 4;

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
	
	//public MovePilot pilot;
	public DifferentialPilot pilot;
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
			for(int i=0;i<SENSOR_CONNECT_MAX_RETRIES&&this.colorS==null;i++)
				this.colorS = new EV3ColorSensor(SensorPort.S1);
		}catch (Exception e){}
		try {
			for(int i=0;i<SENSOR_CONNECT_MAX_RETRIES&&this.touchS==null;i++)
				this.touchS = new EV3TouchSensor(SensorPort.S2);
		}catch (Exception e){}
		try {
			for(int i=0;i<SENSOR_CONNECT_MAX_RETRIES&&this.gyroS==null;i++)
				this.gyroS = new EV3GyroSensor(SensorPort.S3);
		}catch (Exception e){}
		try {
			for(int i=0;i<SENSOR_CONNECT_MAX_RETRIES&&this.ultraS==null;i++)
				this.ultraS = new EV3UltrasonicSensor(SensorPort.S4);
		}catch (Exception e){}

		this.sensors = new SensorThread(colorS, touchS, ultraS, gyroS);

		this.mainMenu = new MainMenu();
	
		//robot steering mechanisms
		this.driver = new Driver(this);
//		Wheel lWheel = WheeledChassis.modelWheel(this.lMotor, WHEEL_DIAMETER).offset(- TRACK_WIDTH * .5);
//		Wheel rWheel = WheeledChassis.modelWheel(this.rMotor, WHEEL_DIAMETER).offset(TRACK_WIDTH * .5);
//		Wheel[]wheels = new Wheel[] {lWheel, rWheel,};
//		WheeledChassis chassis = new WheeledChassis(wheels, WheeledChassis.TYPE_DIFFERENTIAL);
//		this.pilot = new MovePilot(chassis);
		//this.pilot = new DifferentialPilot(WHEEL_DIAMETER, TRACK_WIDTH, this.lMotor, this.rMotor);

		//starts sensor thread
		this.sensors.start();
		
		Screen.startButtonThread();
	}

}
