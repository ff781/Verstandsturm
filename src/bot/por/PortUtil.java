package bot.por;

import lejos.hardware.sensor.*;
import lejos.hardware.device.DeviceIdentifier;
import lejos.hardware.port.*;

public class PortUtil {
	
	private PortUtil() {}
	
	static public boolean portPlugged(Port port) {
		DeviceIdentifier devId = new DeviceIdentifier(port);
		boolean r = devId.getDeviceType()!=EV3SensorConstants.TYPE_NONE;
		devId.close();
		return r;
	}

}