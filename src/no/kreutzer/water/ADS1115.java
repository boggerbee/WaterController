package no.kreutzer.water;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *  # Mapping of gain values to config register values.
	ADS1x15_CONFIG_GAIN = {
		2/3: 0x0000,
		1:   0x0200,
		2:   0x0400,
		4:   0x0600,
		8:   0x0800,
		16:  0x0A00
	}
	ADS1x15_CONFIG_MODE_CONTINUOUS  = 0x0000
	ADS1x15_CONFIG_MODE_SINGLE      = 0x0100
	# Mapping of data/sample rate to config register values for ADS1015 (faster).
	ADS1015_CONFIG_DR = {
		128:   0x0000,
		250:   0x0020,
		490:   0x0040,
		920:   0x0060,
		1600:  0x0080,
		2400:  0x00A0,
		3300:  0x00C0
	}
	# Mapping of data/sample rate to config register values for ADS1115 (slower).
	ADS1115_CONFIG_DR = {
		8:    0x0000,
		16:   0x0020,
		32:   0x0040,
		64:   0x0060,
		128:  0x0080, 
		250:  0x00A0,
		475:  0x00C0,
		860:  0x00E0
	}
	ADS1x15_CONFIG_COMP_WINDOW      = 0x0010
	ADS1x15_CONFIG_COMP_ACTIVE_HIGH = 0x0008
	ADS1x15_CONFIG_COMP_LATCHING    = 0x0004
	ADS1x15_CONFIG_COMP_QUE = {
		1: 0x0000,
		2: 0x0001,
		4: 0x0002
	}
	ADS1x15_CONFIG_COMP_QUE_DISABLE = 0x0003
 */
public class ADS1115 {
    private static final Logger logger = LogManager.getLogger(ADS1115.class);
	// Register and other configuration values:
	private int ADS1x15_DEFAULT_ADDRESS        = 0x48;
	private int ADS1x15_POINTER_CONVERSION     = 0x00;
	private int ADS1x15_POINTER_CONFIG         = 0x01;
	private int ADS1x15_POINTER_LOW_THRESHOLD  = 0x02;
	private int ADS1x15_POINTER_HIGH_THRESHOLD = 0x03;
	private int ADS1x15_CONFIG_OS_SINGLE       = 0x8000;
	private int ADS1x15_CONFIG_MUX_OFFSET      = 12;	
	// Config modes
	private int ADS1x15_CONFIG_MODE_CONTINUOUS  = 0x0000;
	private int ADS1x15_CONFIG_MODE_SINGLE      = 0x0100;
		
	// Create I2C bus
	private I2CBus bus = I2CFactory.getInstance(I2CBus.BUS_1);
	// Get I2C device
	private I2CDevice device = bus.getDevice(ADS1x15_DEFAULT_ADDRESS);
	
	/*static final Map<Integer, String> MY_MAP = ImmutableMap.of(
		1, "one",
		2, "two"
	);*/
	
	public ADS1115() throws Exception	{
		byte[] config = {(byte)0x84, (byte)0x83}; 
		// Select configuration register
		// AINP = AIN0 and AINN = AIN1, +/- 2.048V, Continuous conversion mode, 128 SPS
		device.write(0x01, config, ADS1x15_CONFIG_MODE_CONTINUOUS, 2);
		Thread.sleep(500);
	}
	
	public int read() throws IOException {
		// Read 2 bytes of data
		// raw_adc msb, raw_adc lsb
		byte[] data = new byte[2];
		device.read(0x00, data, 0, 2);
		
		// Convert the data
		int raw_adc = ((data[0] & 0xFF) * 256) + (data[1] & 0xFF);
		if (raw_adc > 32767) {
			raw_adc -= 65535;
		}
		return raw_adc;
	}
}
