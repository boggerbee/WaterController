package no.kreutzer.water;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import no.kreutzer.water.FullSwitch.State;

public class Tank {
    private static final Logger logger = LogManager.getLogger(Tank.class);
	// Thresholds for upper(full) and lower limit to start fill in percent
	public static final int UPPER_THRESHOLD = 90;
	public static final int LOWER_THRESHOLD = 80;
	
	private LevelMeter levelMeter;
	private FullSwitch fullSwitch;
	
	private FullSensor fullSensor;
	
	public enum State {FILLING,FULL};
	public enum FullMode {SWITCH,LEVEL};
	
	private State state = State.FULL;
	private FullMode mode = FullMode.SWITCH;
	
	/* Constructor */
	public Tank () {
		levelMeter = new LevelMeter();
		fullSwitch = new FullSwitch();
		
		fullSensor = fullSwitch;
	}
	

	public float getLevel() {
		return levelMeter.getLevel();
	}
	public State getState() {
		return state;
	}
	public void setState(State st) {
		state = st;
	}
	public FullSwitch getFullSwitch() {
		return fullSwitch;
	}

	public boolean isFull() {
		return fullSensor.isFull(state);
	}

	public boolean isFilling() {
		return (state == Tank.State.FILLING);
	}
	
	public void setMode(FullMode m) {
		switch (m) {
		case SWITCH:
			mode = m;
			fullSensor = fullSwitch;
			break;
		case LEVEL:
			mode = m;
			fullSensor = levelMeter;
			break;
		}		
	}
	
	public FullMode getMode() {
		return mode;
	}
}
