package no.kreutzer.utils;

public interface SocketCommand {
	String calStart();
	String calStop();
	String setMode(int mode);
	String setFull(int mode);
	String testSwitch(int mode);
}
