/*
 * Main class of the water controller
 * 
 * 
 */
package no.kreutzer.water;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.Timer;
import java.util.TimerTask;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.apache.http.HttpEntity;

public class Controller {
    private static final Logger logger = LogManager.getLogger(Controller.class);
	private Tank tank;
	private Pump pump;
	private FlowMeter flow;
	private Valve valve;
	private Timer timer;
	private int pollInterval = 1;
	private boolean autoFill = true;
	
	private void init() {
		tank = new Tank();
		valve = new Valve();
		pump = new Pump();
		flow = new FlowMeter();
		
		try {
			RESTService rest = new RESTService();
			HttpEntity entity = rest.get("api/level");
			BufferedReader rd = new BufferedReader(new InputStreamReader(entity.getContent()));
			
			// Use javax.json.JsonObject or org.json.JSONObject
			
			String line = "";
			while ((line = rd.readLine()) != null) {
				logger.info(line);
			}
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		
		startPolling();
		logger.trace("Init done!");
	}
	
	private void startPolling() {
		timer = new Timer();
		timer.schedule(new Poll(),0,pollInterval * 1000);
    }
    
    private void startFill() {
		try {
			valve.open();
			flow.startMeasure();
			Thread.sleep(500);
			pump.on();
		} catch (InterruptedException e) {
			logger.error("Failed to sleep "+e.getMessage());
		}
	}
	
    private void stopFill() {
		try {
			pump.off();
			Thread.sleep(500);
			valve.close();
			flow.stopMeasure();
		} catch (InterruptedException e) {
			logger.error("Failed to sleep "+e.getMessage());
		}
	}
    
    private void checkLevel() {
		int level = tank.getLevel();
		if (level < Tank.LOWER_THRESHOLD && tank.getState() != Tank.State.FILLING) {
			logger.info("Start filling");
			tank.setState(Tank.State.FILLING);
			startFill();
		} else if (level >= Tank.UPPER_THRESHOLD && tank.getState() == Tank.State.FILLING) {
			logger.info("Stop filling");
			stopFill();
			tank.setState(Tank.State.FULL);
		} 
		logger.trace("Level="+level+" "+tank.getState()); 
	}
	
	class Poll extends TimerTask {
		public void run() {
			tank.updateLevel();
			
			if (autoFill)
				checkLevel();
			//timer.cancel(); 
		}
	}
	
  	public static void main (String args[]) {
		logger.info("Hello, Water World!");
		
		Controller controller = new Controller();
		controller.init();
	}
}

