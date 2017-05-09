package no.kreutzer.water;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.IOException;
import com.pi4j.io.gpio.*;

public class LevelMeter {
    private static final Logger logger = LogManager.getLogger(LevelMeter.class);
	final GpioController gpio = GpioFactory.getInstance();
	private ADS1115 ADConverter;
	private int MAX_VALUE = 6500;
	private int MIN_VALUE = -4400;
	
	/* Constructor */
	public LevelMeter () {
		try {
			ADConverter = new ADS1115();
		} catch (Exception e) {
			logger.error("Error initializing ADS1115 "+e.getMessage());
		}
		logger.info("Initial level is :"+measurePercent());
	}
	
	public int measureRaw() {
		if (ADConverter == null) return 0;
		try {
			return ADConverter.read();
		} catch (IOException e) {
			logger.error("Error reading water level "+e.getMessage());
			return 0;
		}
	}
	public float measurePercent() {
		float part = measureRaw() - MIN_VALUE;
		float whole = MAX_VALUE - MIN_VALUE;
		return (part*100)/whole;
	}	
}
