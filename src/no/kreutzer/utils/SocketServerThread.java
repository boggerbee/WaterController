package no.kreutzer.utils;

import java.net.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
 
public class SocketServerThread extends Thread {
    private static final Logger logger = LogManager.getLogger(SocketServerThread.class);
    private Socket socket = null;
    SocketCommand callback;
 
    public SocketServerThread(Socket socket, SocketCommand cb) {
        super("SocketServerThread");
        this.socket = socket;
        callback = cb;
    }
     
    public void run() {
 
        try (
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        ) {
            String inputLine = in.readLine();
            logger.info("Got command: "+inputLine);
            if (inputLine.equals("startcal")) {
            	callback.calStart(out);
                out.println("Started calibration");
            } else if (inputLine.equals("stopcal")) {
            	callback.calStop(out);
                out.println("Stopped calibration");
            } else {
            	out.println("Unknown command: "+inputLine);
            	out.println("Usage: water <cmd>\n"+
            			"Commands:\n"+
            			" startcal -- start calibration\n"+
            			" stopcal  -- stop calibration");
            }
            
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
