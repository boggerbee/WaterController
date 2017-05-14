package no.kreutzer.utils;

import java.net.*;
import java.util.StringTokenizer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
 
public class SocketServerThread extends Thread {
    private static final Logger logger = LogManager.getLogger(SocketServerThread.class);
    private Socket socket = null;
    private SocketCommand callback;
    private PrintWriter out;
 
    public SocketServerThread(Socket socket, SocketCommand cb) {
        super("SocketServerThread");
        this.socket = socket;
        callback = cb;
    }
     
    public void run() {
 
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String inputLine = in.readLine();
            logger.info("Got command: "+inputLine);
            StringTokenizer st = new StringTokenizer(inputLine);
            
            String cmd = st.nextToken();
            if (cmd.equals("calibrate")) {
            	doCalibrate(st.nextToken());
            } else if (inputLine.equals("mode")) {
            	doMode(st.nextToken());
            } else {
            	out.println("Unknown command: "+inputLine);
            	out.println("Usage: water <cmd> <option>\n"+
            			"Commands:\n"+
            			" calibrate [start|stop]	-- start/stop calibration\n"+
            			" mode  	[0,1,2]			-- fill mode, 0:off, 1:slow, 2:fast");
            }
            
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void doMode(String cmd) {
		try {
			int mode = Integer.parseInt(cmd);
			callback.setMode(out, mode);
		} catch (NumberFormatException e) {
			out.println("Invalid option: "+cmd);
		}
	}

	private void doCalibrate(String cmd) {
		if (cmd==null) cmd="";
		
    	if (cmd.equals("start")) {
	    	callback.calStart(out);
	        out.println("Started calibration");
	    } else if (cmd.equals("stop")) {
	    	callback.calStop(out);
	        out.println("Stopped calibration");
	    } else {
	        out.println("Unknown option: "+cmd);
	    }
    	
    }
}
