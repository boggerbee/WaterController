package no.kreutzer.water;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Tank {
    private static final Logger logger = LogManager.getLogger(Tank.class);
	public static final int UPPER_THRESHOLD = 66;
	public static final int LOWER_THRESHOLD = 42;
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
		level = levelMeter.measure();
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
