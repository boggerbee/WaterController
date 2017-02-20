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
import org.apache.http.HttpEntity;
import javax.json.JsonObjectBuilder;
import javax.json.JsonObject;
import javax.json.Json;

import no.kreutzer.utils.RESTService;

public class Controller {
    private static final Logger logger = LogManager.getLogger(Controller.class);
	private Tank tank;
	private Pump pump;
	private FlowMeter flow;
	private Valve valve;
	private Timer timer;
	private int pollInterval = 5;
	private boolean autoFill = true;
	private String id = "Almedalen25"; //@TODO: put in property-file
	RESTService rest = new RESTService();
	
	private void init() {
		tank = new Tank();
		valve = new Valve();
		pump = new Pump();
		flow = new FlowMeter();
		
		startPolling();
		updateStatus();
		logger.trace("Init done!");
	}
	
	private void updateStatus() {

 	JsonObject json = Json.createObjectBuilder()
			.add("id",id)
			.add("level",tank.getLevel())
			.add("flow",flow.getFlow())
			.add("state",tank.getState().toString())
			.add("pumpState",pump.getState().toString())
			.add("valveState",valve.getState().toString())
			.build();
		rest.doPost("api/tank",json);
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
		float level = tank.getLevel();
		if (level < Tank.LOWER_THRESHOLD && tank.getState() != Tank.State.FILLING) {
			logger.info("Start filling");
			tank.setState(Tank.State.FILLING);
			startFill();
		} else if (level >= Tank.UPPER_THRESHOLD && tank.getState() == Tank.State.FILLING) {
			logger.info("Stop filling");
			tank.setState(Tank.State.FULL);
			stopFill();
		} 
//		logger.trace("Level="+level+" "+tank.getState()); 
		updateStatus();
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

