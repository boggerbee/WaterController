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
    private JavaWebSocketClient clientEndPoint;

    public WebSocketService(SocketCommand cmd, String uri) {
        try {
        	logger.info("Open websocket to: "+uri);
            clientEndPoint = new JavaWebSocketClient(new URI(uri),new Draft_6455());

            clientEndPoint.setMsgHandler(new WebSocketMessageHandlerImpl(cmd));
            clientEndPoint.connect();            
        } catch (URISyntaxException ex) {
        	logger.error("URISyntaxException exception: " + ex.getMessage());
        }    	
    }
    
    public void sendMessage(String message) {
    	try {
    		clientEndPoint.send(message);
    	} catch (Exception e) {
    		//noop
    	}
    }
}