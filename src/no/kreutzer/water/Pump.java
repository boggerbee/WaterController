package no.kreutzer.water;

import com.pi4j.io.gpio.*;

import javax.enterprise.context.ApplicationScoped;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@ApplicationScoped
public class Pump {
    private static final Logger logger = LogManager.getLogger(Pump.class);
    final GpioController gpio = GpioFactory.getInstance();
    final GpioPinDigitalOutput relay = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_12);
	public enum State {ON,OFF};
	private State state = State.OFF;
	
	/* Constructor */
	public Pump () {
		relay.setShutdownOptions(false,PinState.LOW);
		off();
	}
	
	public State getState() {return state;}
	
	public void setState(State s) {
		state = s;
		logger.info(s);
	}
	public void on() {
		setState(State.ON);
		relay.high();
	}
	public void off() {
		setState(State.OFF);
		relay.low();
	}
}
