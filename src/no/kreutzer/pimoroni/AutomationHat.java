package no.kreutzer.pimoroni;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

import no.kreutzer.water.ADS1115;

/**
    
 * @author anders
 *
 */
public class AutomationHat {
    private static final Logger logger = LogManager.getLogger(AutomationHat.class);
    private ADS1115 ads;	// Analog/digital converter
    private SN3218 sn;		// PWM led driver
    private boolean ledDirty = true;
    private byte[] ledStates = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
    public static final int WARN = 0;
    public static final int COMMS = 1;
    public static final int POWER = 2;
    public static final int OUTPUT_1 = 0;
    public static final int OUTPUT_2 = 1;
    public static final int OUTPUT_3 = 2;
    private final GpioController gpio = GpioFactory.getInstance();
    private SNLight[] lights = {
    		new SNLight(15),	
    		new SNLight(16),	
    		new SNLight(17)};
    private Output[] outputs = {
    		new Output(3,RaspiPin.GPIO_21),	
    		new Output(4,RaspiPin.GPIO_26),	
    		new Output(5,RaspiPin.GPIO_22)};
    
    public class SNLight {
    	private int index;
    	private float maxBrightness = 100;//128;
    	
    	public SNLight(int idx) {
    		this.index = idx;
    	}
    	
    	public void on() {
    		try {
				write(1);
			} catch (IOException e) {
				logger.error("Error setting led on, "+e.getMessage());
			}
    	}
    	
    	public void off() {
    		try {
				write(0);
			} catch (IOException e) {
				logger.error("Error setting led off, "+e.getMessage());
			}
    	}
    	
    	public void toggle() {
    		try {
				write(1-read());
			} catch (IOException e) {
				logger.error("Error toggling led, "+e.getMessage());
			}
    	}
    	
    	public byte read() {
    		return (byte)(ledStates[index]/maxBrightness);
    	}
    	
    	public void write(float val) throws IOException {
            if ((val >= 0) && (val <= 1)) {
            	ledStates[index] = (byte)(maxBrightness * val);
            	ledDirty = true;
            } else {
                throw new IOException("Value must be between 0.0 and 1.0");
            }
    	}
    }
    
    public class Output {
    	private boolean autoLights = true;
    	private SNLight light;
        private GpioPinDigitalOutput pin;
    	
    	public Output(int led, Pin p) {
    		light = new SNLight(led);
    		pin = gpio.provisionDigitalOutputPin(p,PinState.LOW);
    	}
    	
    	public void high() {
			pin.high();
			updateLights();
		}
    	public void low() {
			pin.low();
			updateLights();
			
		}
    	private void updateLights() {
			if (autoLights)
				if (pin.isHigh()) light.on();
				else light.off();    		
    	}
    	public void setAutoLights(boolean auto) {
    		autoLights = auto;
    	}
    }
	
	public AutomationHat() {
		try {
			//ads = new ADS1115();
			sn = new SN3218();
			sn.enable();
			sn.enableLeds(0b111111111111111111); // enable all 18 channels
			logger.info("HAT initialized!");
		} catch (Exception e) {
			logger.error("Error initializing, no AutomationHat? "+e.getMessage());
		}
        Executors.newScheduledThreadPool(2).scheduleAtFixedRate(new Runnable() {
	    		@Override
	    		public void run() {
	    			if (ledDirty) {
	    				try {
							ledDirty = false;
							sn.output(ledStates);
							logger.info("Updated leds");
						} catch (IOException e) {
							logger.error("Error writing leds, "+e.getMessage());
						}
	    			}
	    		}
	    	}, 100,50, TimeUnit.MILLISECONDS);	
		logger.info("Timer started!");
	}
	
	public SNLight getLight(int index) {
		if (index < 3) {
			return lights[index];
		} else {
			return null;
		}
	}

	public Output getOutput(int index) {
		if (index < 3) {
			return outputs[index];
		} else {
			return null;
		}
	}
}
