/*
 * Main class of the water controller
 * 
 * 
 */
package no.kreutzer.water;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.json.JsonObject;
import javax.json.Json;

import no.kreutzer.utils.Config;
import no.kreutzer.utils.RESTService;
import no.kreutzer.utils.SocketCommand;
import no.kreutzer.utils.SocketServer;

public class Controller {
    private static final Logger logger = LogManager.getLogger(Controller.class);
	private Tank tank;
	private Pump pump;
	private FlowMeter flow;
	private Valve valve;
	private ScheduledExecutorService scheduledPool;
	private static int FILL_INTERVAL = 1;
	private static int FULL_INTERVAL = 60;

	private String id = "Almedalen25"; 	//@TODO: put in property-file
	private RESTService rest = new RESTService();
	
	public enum Mode {OFF	// No filling
					,SLOW,	// Only open valve
					FAST};	// Open valve and start pump
	private Mode mode = Mode.FAST; 		//@TODO: read from property
	private Mode tmpMode = mode;
	
	private int startCnt;
	
	private void init() {
		tank = new Tank();
		valve = new Valve();
		pump = new Pump();
		flow = new FlowMeter();
		
        scheduledPool = Executors.newScheduledThreadPool(4);
        scheduledPool.schedule(runnableTask, 1,TimeUnit.SECONDS);
        
        try {
			new SocketServer(socketCommand);
		} catch (IOException e) {
			logger.error(e);
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
				.add("switchState",tank.getFullSwitch().getState().toString())
				.build();
		try {
			rest.post("api/tank",json);
		} catch (IOException e) {
			logger.error(e);
		}
	}
	
    private void startFill() {
		try {
			valve.open();
			Thread.sleep(500);
			if (mode.equals(Mode.FAST)) pump.on();
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
	}
	
	private Runnable runnableTask = new Runnable() {
		@Override
		public void run() {
			tank.updateLevel();
			
			if (!mode.equals(Mode.OFF)) {
				checkLevel();
			}
			updateStatus();
			
			// schedule the next poll
			if (tank.getState() == Tank.State.FILLING && !mode.equals(Mode.OFF)) {
		        scheduledPool.schedule(runnableTask, FILL_INTERVAL,TimeUnit.SECONDS);
			} else {
		        scheduledPool.schedule(runnableTask, FULL_INTERVAL,TimeUnit.SECONDS);
			}
		}
	};
	
	private SocketCommand socketCommand = new SocketCommand() {
		@Override
		public void calStart(PrintWriter out) {
			logger.info("Start calibration, fill a known amount of water");
			tmpMode = mode;
			mode = Mode.OFF;
			startCnt = flow.getTotalCount();
			startFill();
			out.println("Flow meter reads: ["+flow.getFlow()+"/"+startCnt+"]");
		}

		@Override
		public void calStop(PrintWriter out) {
			logger.info("Stop calibration");
			mode = tmpMode;
			
			int stopCnt = flow.getTotalCount();
			int ppl = stopCnt-startCnt;
			//flow.setPPL(ppl); //assumes volume is 1 litre
			
			out.println("Pulses: "+ppl);
			out.println("Flow meter reads: ["+flow.getFlow()+"/"+stopCnt+"]");
			
			stopFill();
		}

		@Override
		public void setMode(PrintWriter out, int m) {
			switch(m) {
			case 0: 
				mode = Mode.OFF;
				out.println("Mode set to OFF");
				if (tank.getState().equals(Tank.State.FILLING)) {
					valve.close();
					pump.off();
				}
				break;
			case 1: 
				mode = Mode.SLOW;
				out.println("Mode set to SLOW");
				if (tank.getState().equals(Tank.State.FILLING)) {
					pump.off();
					valve.open();
				}
				break;
			case 2: 
				mode = Mode.FAST;
				out.println("Mode set to FAST");
				if (tank.getState().equals(Tank.State.FILLING)) {
					valve.open();
					pump.on();
				}
				break;
			default:
				out.println("Unknown mode: "+m);
			}
			
		}		
	};
	
  	public static void main (String args[]) {
		logger.info("Hello, Water World!");
		
		Config conf = new Config();
		
		Controller controller = new Controller();
		controller.init();
	}
}

