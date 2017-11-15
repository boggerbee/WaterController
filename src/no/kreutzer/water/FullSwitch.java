package no.kreutzer.water;

import javax.enterprise.context.ApplicationScoped;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

@ApplicationScoped
public class FullSwitch implements FullSensor {
    private static final Logger logger = LogManager.getLogger(FullSwitch.class);
    private final GpioController gpio = GpioFactory.getInstance();
    private GpioPinDigitalInput sensor = gpio.provisionDigitalInputPin(RaspiPin.GPIO_00,"Full Sensor", PinPullResistance.PULL_DOWN); // GPIO_00 = BCM 17
    public enum State {TRIGGERED,OPEN};
    private State state;
    private FullEventHandler fullEvent;
   
    public void setFullEventHandler(FullEventHandler fullEvent) {
		this.fullEvent = fullEvent;
	}

	public FullSwitch() {
        // set shutdown state for this input pin
        sensor.setShutdownOptions(true);
        
        setState(getCurrentState());
        logger.info("Current state: "+state);

        // create and register gpio pin listener
        sensor.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                // display pin state on console
                setState(stateMap(event.getState()));
                logger.info(" --> Fullstate: " + event.getPin() + " = " + event.getState()+ " -> "+state);
                if (fullEvent != null) fullEvent.onChange(state);
            }
        });
    	
    }

	private State stateMap(PinState st) {
		switch (st) {
		case LOW:
			return State.OPEN;
		case HIGH:
			return State.TRIGGERED;
		default:
			logger.error("Sensor did not return HIGH/LOW!!");
			return null;
		}
	}
	
    private State getCurrentState() {
		return stateMap(sensor.getState());
	}

	public State getState() {
		return state;
	}

	private void setState(State state) {
		this.state = state;
	}

	@Override
	public boolean isFull(Tank.State s) {
		return (state == State.TRIGGERED);
	}
}
