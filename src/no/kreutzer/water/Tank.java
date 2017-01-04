package no.kreutzer.water;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Tank {
    private static final Logger logger = LogManager.getLogger(Tank.class);
	public static final int UPPER_THRESHOLD = 90;
	public static final int LOWER_THRESHOLD = 80;
	private LevelMeter levelMeter;
	
	private int level = 0;
	
	public enum State {FILLING,FULL};
	private State state = State.FULL;
	
	/* Constructor */
	public Tank () {
		// init level
		levelMeter = new LevelMeter();
		updateLevel();
	}
	
	public int updateLevel() {
		level = levelMeter.measurePercent();
		return level;
	}
	public int getLevel() {
		return level;
	}
	public State getState() {
		return state;
	}
	public void setState(State st) {
		state = st;
	}
	
}
