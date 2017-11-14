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
            String opt;
            if (st.hasMoreTokens()) {
            	opt = st.nextToken();
                if (cmd.equals("calibrate")) {
                	doCalibrate(opt);
                } else if (cmd.equals("mode")) {
                	doMode(opt);
                } else if (cmd.equals("full")) {
                	doFull(opt);
                } else if (cmd.equals("testsw")) {
                	doTestSW(opt);
                } else {
                	printUsage(inputLine);
                }
            } else {
            	printUsage(inputLine);
            }
            
            socket.close();
        } catch (IOException e) {
            logger.error(e);
        } 
    }
    
	private void doTestSW(String opt) {
		try {
			int mode = Integer.parseInt(opt);
			out.println(callback.testSwitch(mode));
		} catch (NumberFormatException e) {
			out.println("Invalid option: "+opt);
		}		
	}

	private void printUsage(String in) {
    	out.println("Unknown command: "+in);
    	out.println("Usage: water <cmd> <option>\n"+
    			"Commands:\n"+
    			" calibrate [start|stop]	-- start/stop calibration\n"+
    			" testsw 	[1,0]			-- test FullSwitch, 1:on, 0:off\n"+
    			" mode  	[0,1,2]			-- fill mode, 0:off, 1:slow, 2:fast\n"+
				" full  	[0,1]			-- full mode, 0:switch, 1:level meter");
		
	}

    private void doFull(String opt) {
		try {
			int mode = Integer.parseInt(opt);
			out.println(callback.setFull(mode));
		} catch (NumberFormatException e) {
			out.println("Invalid option: "+opt);
		}	
	}

	private void doMode(String cmd) {
		try {
			int mode = Integer.parseInt(cmd);
			out.println(callback.setMode(mode));
		} catch (NumberFormatException e) {
			out.println("Invalid option: "+cmd);
		}
	}

	private void doCalibrate(String cmd) {
		if (cmd==null) cmd="";
		
    	if (cmd.equals("start")) {
    		out.println(callback.calStart());
	        out.println("Started calibration");
	    } else if (cmd.equals("stop")) {
	    	out.println(callback.calStop());
	        out.println("Stopped calibration");
	    } else {
	        out.println("Unknown option: "+cmd);
	    }
    	
    }
}
