package no.kreutzer.utils;

import java.io.PrintWriter;

public interface SocketCommand {
	void calStart(PrintWriter out);
	void calStop(PrintWriter out);
	void setMode(PrintWriter out,int mode);
	void setFull(PrintWriter out, int mode);
}
