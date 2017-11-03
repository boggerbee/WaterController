package no.kreutzer.water;

import java.time.LocalDateTime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ADSMock implements ADConverter {
    private static final Logger logger = LogManager.getLogger(ADSMock.class);
    private int deg = 0;
	
	public ADSMock() {
		logger.info("Created ADSMock");
	}

	@Override
	public int read() throws Exception {
		//LocalDateTime timePoint = LocalDateTime.now();
		//int sec = timePoint.getSecond();
		deg++;
		if (deg==180) deg=0;
		return (int) ((Math.sin(Math.toRadians(deg))+1)*3250);
	}

}
