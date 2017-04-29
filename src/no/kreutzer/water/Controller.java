/*
 * Main class of the water controller
 * 
 * 
 */
package no.kreutzer.water;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pi4j.wiringpi.Gpio;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.json.JsonObject;
import javax.json.Json;

import no.kreutzer.utils.RESTService;

public class Controller {
    private static final Logger logger = LogManager.getLogger(Controller.class);
	private Tank tank;
	private Pump pump;
	private FlowMeter flow;
	private Valve valve;
	private ScheduledExecutorService scheduledPool;
	private static int FILL_INTERVAL = 1;
	private static int FULL_INTERVAL = 60;
	private boolean autoFill = true;
	private String id = "Almedalen25"; //@TODO: put in property-file
	private RESTService rest = new RESTService();
	private FullSensor fullSwitch;
	
	private void init() {
		tank = new Tank();
		valve = new Valve();
		pump = new Pump();
		flow = new FlowMeter();
		fullSwitch = new FullSensor();
		
        scheduledPool = Executors.newScheduledThreadPool(4);
        scheduledPool.schedule(runnableTask, 1,TimeUnit.SECONDS);
        
        // test WiringPi for output
        if (Gpio.wiringPiSetup() == -1) {
            logger.error(" ==>> GPIO SETUP FAILED");
        }        
        
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
			//	.add("fullSwitch",fullSwitch.getState().toString())
				.build();
		try {
			rest.post("api/tank",json);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error(e);
		}
	}
	
    private void startFill() {
		try {
			valve.open();
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
		updateStatus();
	}
	
	Runnable runnableTask = new Runnable() {
		@Override
		public void run() {
			tank.updateLevel();
			
			if (autoFill)
				checkLevel();

			flow.reset();
			// schedule the next poll
			if (tank.getState() == Tank.State.FILLING) {
		        scheduledPool.schedule(runnableTask, FILL_INTERVAL,TimeUnit.SECONDS);
			} else {
		        scheduledPool.schedule(runnableTask, FULL_INTERVAL,TimeUnit.SECONDS);
			}
		}
	};
	
  	public static void main (String args[]) {
		logger.info("Hello, Water World!");
		
		Controller controller = new Controller();
		controller.init();
	}
}

