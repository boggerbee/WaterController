package no.kreutzer.water;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.pi4j.io.gpio.*;

public class Tank {
    private static final Logger logger = LogManager.getLogger(Tank.class);
	final GpioController gpio = GpioFactory.getInstance();
	public static final int UPPER_THRESHOLD = 66;
	public static final int LOWER_THRESHOLD = 42;
	
	private int level = 0;
	
	public enum State {FILLING,FULL};
	private State state = State.FULL;
	
	/* Constructor */
	public Tank () {
		// init level
		updateLevel();
	}
	
	public int updateLevel() {
		// TODO: invoke the proper GPIO
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
