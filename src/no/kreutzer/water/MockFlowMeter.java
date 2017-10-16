package no.kreutzer.water;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MockFlowMeter implements FlowMeter {
    private static final Logger logger = LogManager.getLogger(MockFlowMeter.class);
	private FlowHandler flowHandler;
	private AtomicInteger pulseTotal = new AtomicInteger();
	
	
	public MockFlowMeter(FlowHandler f) {
		logger.info("MockFlowMeter created");  
		flowHandler = f;

        Executors.newScheduledThreadPool(2).scheduleAtFixedRate(new Runnable() {
    		@Override
    		public void run() {
				pulseTotal.incrementAndGet();
				if (flowHandler!=null) flowHandler.onCount(pulseTotal.get(),0);
    		}
    	}, 1000,50, TimeUnit.MILLISECONDS);		
	}

	@Override
	public float getFlow() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getTotalCount() {
		// TODO Auto-generated method stub
		return pulseTotal.get();
	}

	@Override
	public void setPPL(int ppl) {
		// TODO Auto-generated method stub
		
	}

}
