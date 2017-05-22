package no.kreutzer.utils;

import java.io.IOException;
import java.net.URI;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@ClientEndpoint
public class WebSocketClient {
    private static final Logger logger = LogManager.getLogger(WebSocketClient.class);
    Session userSession = null;
    private MessageHandler messageHandler;

    public WebSocketClient(URI endpointURI) {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, endpointURI);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    @OnOpen
    public void onOpen(Session session) {
        logger.info("Connected to endpoint: " + session.getBasicRemote());
        this.userSession = userSession;
        try {
            String name = "Duke";
            logger.info("Sending message to endpoint: " + name);
            session.getBasicRemote().sendText(name);
        } catch (IOException ex) {
            logger.error(ex);
        }
    }

    @OnMessage
    public void processMessage(String message) {
        logger.info("Received message in client: " + message);
//       WebSocketService.messageLatch.countDown();
        if (this.messageHandler != null) {
            this.messageHandler.handleMessage(message);
        }        
    }

    @OnClose
    public void onClose(Session userSession, CloseReason reason) {
        logger.info("Closing websocket");
        this.userSession = null;
    }    
    
    @OnError
    public void processError(Throwable t) {
        logger.error(t);
    }
    
    public void addMessageHandler(MessageHandler msgHandler) {
        this.messageHandler = msgHandler;
    }
    
    public void sendMessage(String message) {
        this.userSession.getAsyncRemote().sendText(message);
    }    
    
    public static interface MessageHandler {

        public void handleMessage(String message);
    }    
}
