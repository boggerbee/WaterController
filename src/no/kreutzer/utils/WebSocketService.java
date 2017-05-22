package no.kreutzer.utils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CountDownLatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WebSocketService {
    private static final Logger logger = LogManager.getLogger(WebSocketService.class);
	private String uri = "ws://data.kreutzer.no:80/websocket";
    final static CountDownLatch messageLatch = new CountDownLatch(1);

    public WebSocketService(SocketCommand cmd) {
    	SocketCommand callback = cmd;
    	/*
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            logger.info("Connecting to " + uri);
            container.connectToServer(WebSocketClient.class, URI.create(uri));
            messageLatch.await(100, TimeUnit.SECONDS);
        } catch (DeploymentException | InterruptedException | IOException ex) {
            logger.error(ex);
        }*/
        try {
            // open websocket
            final WebSocketClient clientEndPoint = new WebSocketClient(new URI(uri));

            // add listener
            clientEndPoint.addMessageHandler(new WebSocketClient.MessageHandler() {
                public void handleMessage(String message) {
                    logger.info(message);
                    //TODO: callback..
                }
            });

            // send message to websocket
            clientEndPoint.sendMessage("Hello from Raspberry Pi!");

            // wait 5 seconds for messages from websocket
            Thread.sleep(5000);

        } catch (InterruptedException ex) {
            logger.error("InterruptedException exception: " + ex.getMessage());
        } catch (URISyntaxException ex) {
        	logger.error("URISyntaxException exception: " + ex.getMessage());
        }    	
    }
}