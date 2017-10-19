package no.kreutzer.water;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.IOException;
import com.pi4j.io.gpio.*;

import no.kreutzer.water.Tank.State;

public class LevelMeter implements FullSensor {
    private static final Logger logger = LogManager.getLogger(LevelMeter.class);
	final GpioController gpio = GpioFactory.getInstance();
	private ADS1115 adc;
	private int MAX_VALUE = 6500;
	private int MIN_VALUE = -4400;
	
	private float level = 0;
	
	/* Constructor */
	public LevelMeter () {
		try {
			adc = new ADS1115();
		} catch (Exception e) {
			logger.error("Error initializing ADS1115 "+e.getMessage());
		}
		logger.info("Initial level is :"+measurePercent());
		level = measurePercent();
	}
	
	protected int measureRaw() {
		if (adc == null) return 0;
		try {
			return adc.read();
		} catch (IOException e) {
			logger.error("Error reading water level "+e.getMessage());
			return 0;
		}
	}
	protected float measurePercent() {
		float part = measureRaw() - MIN_VALUE;
		float whole = MAX_VALUE - MIN_VALUE;
		return (part*100)/whole;
	}
	
	public float getLevel() {
		level = measurePercent();
		return level;
	}

	/*
	 * Don't report "not full" until below the lower limit to avoid frequent on/off situations 
	 */
	@Override
	public boolean isFull(State state) {
		getLevel();
		switch(state) {
		case FILLING:
			return (level >= Tank.UPPER_THRESHOLD);
		case FULL:
			return (level < Tank.LOWER_THRESHOLD);
		default:
			return false;
		}
	}	
}
