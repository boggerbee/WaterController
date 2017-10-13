package no.kreutzer.utils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CountDownLatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.java_websocket.drafts.Draft_6455;

public class WebSocketService {
    private static final Logger logger = LogManager.getLogger(WebSocketService.class);
    final static CountDownLatch messageLatch = new CountDownLatch(1);

    public WebSocketService(SocketCommand cmd, String uri) {
        try {
        	logger.info("Open websocket to: "+uri);
            final JavaWebSocketClient clientEndPoint = new JavaWebSocketClient(new URI(uri),new Draft_6455());

            clientEndPoint.setMsgHandler(new WebSocketMessageHandlerImpl(cmd));
            clientEndPoint.connect();            
        } catch (URISyntaxException ex) {
        	logger.error("URISyntaxException exception: " + ex.getMessage());
        }    	
    }
    
    // For testing
  	public static void main (String args[]) {
  		String uri = "ws://localhost:8088/dataserver-0.1/websocket";
		logger.info("Connecting Websocket client to "+uri);
		new WebSocketService(new SocketCommand() {
			@Override
			public String calStart() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String calStop() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String setMode(int mode) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String setFull(int mode) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String testSwitch(int mode) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getConfig() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String pump(int i) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String valve(int i) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getState() {
				// TODO Auto-generated method stub
				return null;
			}
		},uri);
	}    
}