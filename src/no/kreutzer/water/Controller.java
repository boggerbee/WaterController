/*
 * Main class of the water controller
 * 
 * 
 */
package no.kreutzer.water;

import org.apache.logging.log4j.LogManager;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.json.JsonObject;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.json.Json;

import no.kreutzer.utils.ConfigService;
import no.kreutzer.utils.RESTService;
import no.kreutzer.utils.SocketCommand;
import no.kreutzer.utils.SocketServer;
import no.kreutzer.utils.WebSocketService;
import no.kreutzer.water.FullSwitch.State;

@Dependent
public class Controller {
    private static final Logger logger = LogManager.getLogger(Controller.class);
    private @Inject ConfigService conf;
    private @Inject Tank tank;
    private @Inject Pump pump;
    private @Inject Valve valve;
    private @Inject RESTService rest;

    private FlowMeter flow;
    private ScheduledExecutorService scheduledPool;
    private static int FILL_INTERVAL = 10;
    private static int FULL_INTERVAL = 60;
    private WebSocketService ws;

    public enum Mode {
        OFF,    // No filling
        SLOW,   // Only open valve
        FAST,   // Open valve and start pump
        CAL     // calibration mode
    }; 

    private Mode mode;
    private Mode tmpMode = mode;
    private int startCnt;

//    private AutomationHat hat;
//    private AutomationHat.SNLight commsLight;

    private FlowHandler flowHandler = new FlowHandler() {
        @Override
        public void onCount(long total, int current) {

        }
    };

    private void init() {
        mode = conf.getConfig().getFillMode();
        ws = new WebSocketService(socketCommand, conf.getConfig().getWsEndPoint());
        flow = conf.getFlowSensorImpl(flowHandler);
        flow.setTotal(conf.getConfig().getTotalFlow());
        flow.setPPL(conf.getConfig().getPulsesPerLitre());

        tank.getFullSwitch().setFullEventHandler(new FullEventHandler() {
            @Override
            public void onChange(State state) {
                postEvent("fullSwitch", state.toString());
                if (tank.getMode() == Tank.FullMode.SWITCH) {
                    checkLevel();
                }
                ;
            }
        });

        scheduledPool = Executors.newScheduledThreadPool(4);
        scheduledPool.schedule(runnableTask, 1, TimeUnit.SECONDS);
        scheduledPool.scheduleAtFixedRate(flowPoller, 1000, 200, TimeUnit.MILLISECONDS);
        /*
         * hat = new AutomationHat(); commsLight =
         * hat.getLight(AutomationHat.COMMS);
         * hat.getLight(AutomationHat.POWER).on(); hat.getRelay(0).open();
         */
        try {
            new SocketServer(socketCommand); // for cli interface
        } catch (IOException e) {
            logger.error(e);
        }
        logger.info("Init done!");
    }

    private void postTank() {

        JsonObject json = Json.createObjectBuilder().add("id", conf.getConfig().getId()).add("level", tank.getLevel())
                .add("flow", flow.getFlow()).add("state", tank.getState().toString())
                .add("pumpState", pump.getState().toString()).add("valveState", valve.getState().toString())
                .add("switchState", tank.getFullSwitch().getState().toString()).build();
        try {
            rest.post("api/tank", json);
        } catch (IOException e) {
            logger.error(e);
        }
    }

    private void postEvent(String key, String value) {

        JsonObject json = Json.createObjectBuilder().add("id", conf.getConfig().getId()).add("mode", mode.toString())
                .add("flow", flow.getTotalCount()).add("key", key).add("value", value).build();
        try {
            rest.post("api/controller", json);
        } catch (IOException e) {
            logger.error(e);
        }
    }

    private void startFill() {
        try {
            valve.open();
            Thread.sleep(500);
            if (mode == Mode.FAST || mode == Mode.CAL)
                pump.on();
            postEvent("tank", tank.getState().toString());
        } catch (InterruptedException e) {
            logger.error("Failed to sleep " + e.getMessage());
        }
    }

    private void stopFill() {
        try {
            pump.off();
            Thread.sleep(500);
            valve.close();
            postEvent("tank", tank.getState().toString());
        } catch (InterruptedException e) {
            logger.error("Failed to sleep " + e.getMessage());
        }
    }

    private void checkLevel() {
        if (!tank.isFull() && !tank.isFilling()) {
            logger.info("Start filling");
            tank.setState(Tank.State.FILLING);
            startFill();
        } else if (tank.isFull() && tank.isFilling()) {
            logger.info("Stop filling");
            tank.setState(Tank.State.FULL);
            stopFill();
        }
    }

    private Runnable runnableTask = new Runnable() {
        @Override
        public void run() {
        	try {
	            // commsLight.on();
	            if (mode != Mode.OFF && mode != Mode.CAL) {
	                checkLevel();
	            }
	            postTank();
	            conf.getConfig().setTotalFlow(flow.getTotalCount());
	            conf.writeConfig();
	            
	            ws.checkAlive(); 
	
	            // schedule the next poll
	            if (tank.getState() == Tank.State.FILLING && !mode.equals(Mode.OFF)) {
	                scheduledPool.schedule(runnableTask, FILL_INTERVAL, TimeUnit.SECONDS);
	            } else {
	                scheduledPool.schedule(runnableTask, FULL_INTERVAL, TimeUnit.SECONDS);
	            }
	            // commsLight.off();
	        } catch (RuntimeException e){
	            logger.error("Uncaught Runtime Exception",e);
	            return; // Keep working
	        } catch (Throwable e){
	            logger.error("Unrecoverable error",e);
	            throw e;
	        }
        }
    };
    
    private Runnable flowPoller = new Runnable() {
        @Override
        public void run() {
        	try {
	            if (conf.getConfig().isLiveFlow()) {
	                sendFlowMessage(flow.getTotalCount(), flow.getPulsesPerSecond());
	            }    
            } catch (RuntimeException e){
                logger.error("Uncaught Runtime Exception",e);
                return; // Keep working
            } catch (Throwable e){
                logger.error("Unrecoverable error",e);
                throw e;
            }        
        }
    };
    
    private void sendFlowMessage(long total, int current) {
        JsonObject json = Json.createObjectBuilder()
                .add("flow", Json.createObjectBuilder().add("total", total).add("current", current).build())
                .build();
        ws.sendMessage(json.toString());
    }    

    private SocketCommand socketCommand = new SocketCommand() {
        @Override
        public String calStart() {
            logger.info("Start calibration, fill a known amount of water");
            tmpMode = mode;
            mode = Mode.CAL;
            startCnt = flow.getTotalCount();
            startFill();
            return "Flow meter reads: (total/start) [" + flow.getFlow() + "/" + startCnt + "]";
        }

        @Override
        public String calStop() {
            logger.info("Stop calibration");
            mode = tmpMode;

            int stopCnt = flow.getTotalCount();
            int ppl = stopCnt - startCnt;
            // flow.setPPL(ppl); //assumes volume is 1 litre
            stopFill();
            return "Flow meter reads: (total/stop) [" + flow.getFlow() + "/" + stopCnt + "] diff=" + ppl;
        }

        @Override
        public String setMode(int m) {
            switch (m) {
            case 0:
                mode = Mode.OFF;
                if (tank.getState().equals(Tank.State.FILLING)) {
                    valve.close();
                    pump.off();
                }
                tank.setState(Tank.State.OFF);
                conf.getConfig().setFillMode(mode);
                conf.writeConfig();
                return "Mode set to OFF";
            case 1:
                mode = Mode.SLOW;
                if (tank.getState().equals(Tank.State.FILLING)) {
                    pump.off();
                    valve.open();
                }
                checkLevel();
                conf.getConfig().setFillMode(mode);
                conf.writeConfig();
                return "Mode set to SLOW";
            case 2:
                mode = Mode.FAST;
                if (tank.getState().equals(Tank.State.FILLING)) {
                    valve.open();
                    pump.on();
                }
                checkLevel();
                conf.getConfig().setFillMode(mode);
                conf.writeConfig();
                return "Mode set to FAST";
            default:
                return "Unknown mode: " + m;
            }

        }

        @Override
        public String setFull(int m) {
            switch (m) {
            case 0:
                tank.setMode(Tank.FullMode.SWITCH);
                conf.getConfig().setFullMode(Tank.FullMode.SWITCH);
                conf.writeConfig();
                return "Full mode set to SWITCH";
            case 1:
                tank.setMode(Tank.FullMode.LEVEL);
                conf.getConfig().setFullMode(Tank.FullMode.LEVEL);
                conf.writeConfig();
                return "Full mode set to LEVEL";
            default:
                return "Unknown mode: " + m;
            }
        }

        @Override
        public String testSwitch(int mode) {
            // tank.setFullSensor(new FullSwitchTest());
            return null;
        }

        @Override
        public String getConfig() {
            return conf.getConfigAsJSON();
        }

        @Override
        public String pump(int i) {
            if (i > 0) {
                pump.on();
            } else {
                pump.off();
            }
            return getState();
        }

        @Override
        public String valve(int i) {
            if (i > 0) {
                valve.open();
            } else {
                valve.close();
            }
            return getState();
        }

        @Override
        public String getState() {
            JsonObject json = Json.createObjectBuilder()
                    .add("state",
                            Json.createObjectBuilder().add("pump", pump.getState().toString())
                                    .add("valve", valve.getState().toString())
                                    .add("switch", tank.getFullSwitch().getState().toString())
                                    .add("level", tank.getLevel()).add("flow", flow.getFlow()).build())
                    .build();
            return json.toString();
        }

        @Override
        public String live(boolean b) {
            conf.getConfig().setLiveFlow(b);
            conf.writeConfig();
            logger.info("Live set to: "+conf.getConfig().isLiveFlow());
            return getConfig();
        }
    };

    public static void main(String args[]) {
        logger.info("Hello, Water World!");
        // Bootstrapping CDI
        Weld weld = new Weld();
        try (WeldContainer container = weld.initialize()) {
            container.select(Controller.class).get().init();
            while (true) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
