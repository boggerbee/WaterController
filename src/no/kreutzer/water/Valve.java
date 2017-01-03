package no.kreutzer.water;

import com.pi4j.io.gpio.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Valve {
    private static final Logger logger = LogManager.getLogger(Valve.class);
    final GpioController gpio = GpioFactory.getInstance();
    final GpioPinDigitalOutput relay = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01);
	public enum State {OPEN,CLOSED};
	private State state = State.CLOSED;
	
	/* Constructor */
	public Valve () {
		close();
	}
	
	public State getState() {return state;}
	
	public void setState(State s) {
		state = s;
		logger.info(s);
	}
	public void open() {
		setState(State.OPEN);
		relay.high();
	}
	public void close() {
		setState(State.CLOSED);
		relay.low();
	}
}
