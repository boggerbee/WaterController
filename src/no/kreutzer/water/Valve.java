package no.kreutzer.water;

//import com.pi4j.io.gpio.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.wiringpi.Gpio;
import com.pi4j.wiringpi.GpioUtil;

public class Valve {
    private static final Logger logger = LogManager.getLogger(Valve.class);
    final GpioController gpio = GpioFactory.getInstance();
//    final GpioPinDigitalOutput relay = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_05,"Valve",PinState.LOW); 
	public enum State {OPEN,CLOSED};
	private State state = State.CLOSED;
	private final int WP_PIN = 5; //WiringPi Pin
	
	/* Constructor */
	public Valve () {
//		relay.setShutdownOptions(false,PinState.LOW);
        // test wiringPi
        GpioUtil.export(WP_PIN, GpioUtil.DIRECTION_OUT);
        Gpio.pinMode(WP_PIN, Gpio.OUTPUT);
        
        close();
	}
	
	public State getState() {return state;}
	
	public void setState(State s) {
		state = s;
		logger.info(s);
	}
	public void open() {
		setState(State.OPEN);
		//relay.high();
		
		Gpio.digitalWrite(WP_PIN,1);
	}
	public void close() {
		setState(State.CLOSED);
//		relay.low();
		
		Gpio.digitalWrite(WP_PIN,0);
	}
}
