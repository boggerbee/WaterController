package no.kreutzer.pimoroni;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

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
    public static final int INPUT_1 = 0;
    public static final int INPUT_2 = 1;
    public static final int INPUT_3 = 2;
    public static final int RELAY_1 = 0;
    public static final int RELAY_2 = 1;
    public static final int RELAY_3 = 2;
    
    private final GpioController gpio = GpioFactory.getInstance();
    private SNLight[] lights = {
    		new SNLight(15),					// WARN
    		new SNLight(16),					// COMMS
    		new SNLight(17)};					// POWER
    private Output[] outputs = {
    		new Output(3,RaspiPin.GPIO_21),		// OUTPUT_1
    		new Output(4,RaspiPin.GPIO_26),		// OUTPUT_2
    		new Output(5,RaspiPin.GPIO_22)};	// OUTPUT_3
    private Input[] inputs = {
    		new Input(14,RaspiPin.GPIO_25),		// INPUT_1
    		new Input(13,RaspiPin.GPIO_28),		// INPUT_2
    		new Input(12,RaspiPin.GPIO_29)};	// INPUT_3
    private Relay[] relays = {
    		new Relay(6,7,RaspiPin.GPIO_23),	// RELAY_1
    		new Relay(8,9,RaspiPin.GPIO_24),	// RELAY_2
    		new Relay(10,11,RaspiPin.GPIO_27)};	// RELAY_3
    
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
    
    public class Input {
    	private boolean autoLights = true;
    	private SNLight light;
        private GpioPinDigitalInput pin;
    	
    	public Input(int led, Pin p) {
    		light = new SNLight(led);
    		pin = gpio.provisionDigitalInputPin(p,PinPullResistance.PULL_DOWN);
            pin.addListener(new GpioPinListenerDigital() {
                @Override
                public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
        			updateLights(); 
                }
            });
            updateLights();
    	}
    	
    	private void updateLights() {
			if (autoLights)
				if (pin.isHigh()) light.on();
				else light.off();    		
    	}
    	
    	public boolean isHigh() {
    		return pin.isHigh();
    	}
    	
    	public boolean isLow() {
    		return pin.isLow();
    	}    	
    	
    	public void setAutoLights(boolean auto) {
    		autoLights = auto;
    	}
    }
    
    public class Relay {
    	private boolean autoLights = true;
    	private SNLight light_no, light_nc;
        private GpioPinDigitalOutput pin;
    	
    	public Relay(int led_no, int led_nc, Pin p) {
    		light_no = new SNLight(led_no);
    		light_nc = new SNLight(led_nc);
    		pin = gpio.provisionDigitalOutputPin(p,PinState.LOW);
    		updateLights();
    	}
    	
    	public void open() {
			pin.high();
			updateLights();
		}
    	public void close() {
			pin.low();
			updateLights();
			
		}
   	
    	private void updateLights() {
			if (autoLights)
				if (pin.isHigh()) {
					light_no.on();
					light_nc.off();
				} else {
					light_no.off();
					light_nc.on();
				}
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
//							logger.info("Updated leds");
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
	
	public Input getInput(int index) {
		if (index < 3) {
			return inputs[index];
		} else {
			return null;
		}
	}
	
	public Relay getRelay(int index) {
		if (index < 3) {
			return relays[index];
		} else {
			return null;
		}
	}
}
