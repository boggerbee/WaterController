package no.kreutzer.water;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.pi4j.io.gpio.*;

public class LevelMeter {
    private static final Logger logger = LogManager.getLogger(LevelMeter.class);
	final GpioController gpio = GpioFactory.getInstance();
	
	/* Constructor */
	public LevelMeter () {
	}
	
	public int measure() {
	// measure gpio using ADC
		return 0;
	}
	
}
