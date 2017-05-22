package no.kreutzer.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Config {
    private static final Logger logger = LogManager.getLogger(Config.class);
	private Properties prop;
	
	public Properties getProperties() {
		return prop;
	}

	public Config() {
    	prop = new Properties();
    	InputStream input = null;

    	try {
    		String filename = "water.properties";
    		input = Config.class.getClassLoader().getResourceAsStream(filename);
    		if(input==null){
    	            logger.error("Sorry, unable to find " + filename);
    		    return;
    		}

    		//load a properties file from class path, inside static method
    		prop.load(input);

            //get the property value and print it out
            logger.info("usr="+prop.getProperty("usr"));

    	} catch (IOException ex) {
    		ex.printStackTrace();
        } finally{
        	if(input!=null){
        		try {
				input.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        	}
        }

    }		
}
