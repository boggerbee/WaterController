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
import com.pi4j.io.gpio.event.*;
import com.pi4j.io.gpio.trigger.*;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

public class FlowMeter {
    private static final Logger logger = LogManager.getLogger(FlowMeter.class);
	final GpioController gpio = GpioFactory.getInstance();
	final GpioPinDigitalInput flowPin = gpio.provisionDigitalInputPin(RaspiPin.GPIO_07,"Flow sensor",PinPullResistance.PULL_DOWN);
	
	private AtomicInteger pulseIncrement = new AtomicInteger();
	private AtomicInteger pulseTotal = new AtomicInteger();
	
	private float lastFlow = 0;
	
	// Number of pulses per litre
	private static int NUM_PULSES_PER_LITRE = 666;
	
	/* Constructor */
	public FlowMeter () {
        flowPin.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                if (event.getState().isHigh()) {
					//logger.info(event.getPin() + " = " + event.getState() + " cnt:"+pulseIncrement);  
					pulseIncrement.incrementAndGet();
				}
            }
        });
	}
	
	// TODO: calculate real flow
	public float getFlow() {
		return pulseIncrement.get();
	}
	public int getTotalCount() {
		return pulseTotal.get();
	}
	
	// to be called every second
	public void reset() {
		int currentCount = pulseIncrement.getAndSet(0);
		pulseTotal.addAndGet(currentCount);
		lastFlow = currentCount/NUM_PULSES_PER_LITRE;
	}
}
