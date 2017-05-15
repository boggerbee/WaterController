package no.kreutzer.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
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
    	            System.out.println("Sorry, unable to find " + filename);
    		    return;
    		}

    		//load a properties file from class path, inside static method
    		prop.load(input);

            //get the property value and print it out
            System.out.println(prop.getProperty("usr"));

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
