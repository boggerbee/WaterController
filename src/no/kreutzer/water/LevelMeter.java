package no.kreutzer.water;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.kreutzer.utils.ConfigService;
import no.kreutzer.water.Tank.State;

@ApplicationScoped
public class LevelMeter implements FullSensor {
    private static final Logger logger = LogManager.getLogger(LevelMeter.class);
	private ADConverter adc;
	private int MAX_VALUE = 6500;
	private int MIN_VALUE = -4400;
	
	private float level = 0;
    private @Inject	ConfigService conf;
	
	public LevelMeter () {
	}
	
	@PostConstruct
	public void init() {
		adc = conf.getADCImpl();
		if (adc == null)
			logger.error("No ADC found!!");
		else
			logger.info("Initial level is :"+measurePercent());
		level = measurePercent();
	}	
	
	protected int measureRaw() {
		if (adc == null) return 0;
		try {
			return adc.read();
		} catch (Exception e) {
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
			return (level > Tank.LOWER_THRESHOLD);
		default:
			return false;
		}
	}	
}
