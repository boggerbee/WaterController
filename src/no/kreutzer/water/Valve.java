package no.kreutzer.water;

import javax.enterprise.context.ApplicationScoped;

//import com.pi4j.io.gpio.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

@ApplicationScoped
public class Valve {
    private static final Logger logger = LogManager.getLogger(Valve.class);
    final GpioController gpio = GpioFactory.getInstance();
    final GpioPinDigitalOutput relay = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_05,"Valve",PinState.LOW); 
	public enum State {OPEN,CLOSED};
	private State state = State.CLOSED;
	
	/* Constructor */
	public Valve () {
		relay.setShutdownOptions(false,PinState.LOW);
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
