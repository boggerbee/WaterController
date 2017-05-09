package no.kreutzer.utils;
import java.net.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;

public class SocketServer {
    private static final Logger logger = LogManager.getLogger(SocketServer.class);
    int portNumber = 4242;
    SocketCommand callback;
    
    public SocketServer(SocketCommand cb) throws IOException {
    	callback = cb;
    	
        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
        	logger.info("Accepting commands on port: "+portNumber);
            while (true) {
                new SocketServerThread(serverSocket.accept(),cb).start();
            }
        } catch (IOException e) {
            System.err.println("Could not listen on port " + portNumber);
            System.exit(-1);
        }
    }
}