package no.kreutzer.utils;

import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CountDownLatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.java_websocket.drafts.Draft_6455;

import no.kreutzer.water.Controller;

public class WebSocketService {
    private static final Logger logger = LogManager.getLogger(WebSocketService.class);
	final static String uri = "ws://localhost:8088/dataserver-0.1/websocket";
//	final String uri = "ws://data.kreutzer.no/dataserver/websocket";
    final static CountDownLatch messageLatch = new CountDownLatch(1);

    public WebSocketService(SocketCommand cmd) {
    	SocketCommand callback = cmd;

        try {
            // open websocket
            final JavaWebSocketClient clientEndPoint = new JavaWebSocketClient(new URI(uri),new Draft_6455());

            // add listener
            clientEndPoint.setMsgHandler(new WebSocketMessageHandler() {
                public void onMessage(String message) {
                    logger.info(message);
                    //TODO: callback..
                }
            });
            clientEndPoint.connect();            

            // send message to websocket
            clientEndPoint.send("Hello from Raspberry Pi!");

            // wait 5 seconds for messages from websocket
            Thread.sleep(5000);

        } catch (InterruptedException ex) {
            logger.error("InterruptedException exception: " + ex.getMessage());
        } catch (URISyntaxException ex) {
        	logger.error("URISyntaxException exception: " + ex.getMessage());
        }    	
    }
    
    // For testing
  	public static void main (String args[]) {
		logger.info("Connecting Websocket client to "+uri);
		new WebSocketService(new SocketCommand() {
			
			@Override
			public void setMode(PrintWriter out, int mode) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setFull(PrintWriter out, int mode) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void calStop(PrintWriter out) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void calStart(PrintWriter out) {
				// TODO Auto-generated method stub
				
			}
		});
	}    
}