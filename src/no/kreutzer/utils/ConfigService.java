package no.kreutzer.utils;

import java.io.File;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ConfigService {
    private static final Logger logger = LogManager.getLogger(ConfigService.class);
    private String fileName = "config.json";
    private ConfigPOJO config;

	public ConfigService() {
		config = readConfig();
		if (config == null) {
			config = new ConfigPOJO();
			writeConfig();
			logger.info("Created new config:");
		} else {
			logger.info("Loaded config:");
		}
		writeConfig(); // in case new fields in ConfigPOJO, write them
		try {
			logger.info((new ObjectMapper()).writeValueAsString(config));
		} catch (JsonProcessingException e) {
			logger.error(e.getMessage());
		}
    }
	
	public void writeConfig() {
		ObjectMapper mapper = new ObjectMapper();

		try {
			mapper.writeValue(new File(fileName), config);
		} catch (IOException e) {
			logger.error(e.getMessage());
		}		
	}
	
	public ConfigPOJO readConfig() {
		ObjectMapper mapper = new ObjectMapper();

		try {
			return mapper.readValue(new File(fileName), ConfigPOJO.class);
		} catch (IOException e) {
			logger.error(e.getMessage());
			return null;
		}		
	}
	
	public ConfigPOJO getConfig() {
		return config;
	}
}
