package no.kreutzer.utils;

import java.io.PrintWriter;

public interface SocketCommand {
	void calStart(PrintWriter out);
	void calStop(PrintWriter out);
}
