package no.kreutzer.water;
/*
 * FlowMeter measures the flow of water 
 * 
 * The sensor sends pulses with a frequency equal to the flow rate.
 * Needs to set up listening to changes and measure freq.
 * */
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.trigger.*;
import java.util.concurrent.Callable;

public class FlowMeter {
    private static final Logger logger = LogManager.getLogger(FlowMeter.class);
	final GpioController gpio = GpioFactory.getInstance();
	final GpioPinDigitalInput flowPin = gpio.provisionDigitalInputPin(RaspiPin.GPIO_02,PinPullResistance.PULL_DOWN);
	
	// Measure flow in litres per minute
	private int flow;
	// Frequency in pulses per minute (?)
	private int freq;
	// Number of pulses per litre
	private static int NUM_PULSES_PER_LITRE = 666;
	
	/* Constructor */
	public FlowMeter () {
		// TODO: register listener on flow pin
		flowPin.addTrigger(new GpioCallbackTrigger(new Callable<Void>() {
			public Void call() throws Exception {
				logger.trace(" --> GPIO TRIGGER CALLBACK RECEIVED ");
				return null;
			}
		})); 
	}
	
	/* Return flow in litres per minute */
	public int getFlow() {
		return freq/NUM_PULSES_PER_LITRE;
	}
}
