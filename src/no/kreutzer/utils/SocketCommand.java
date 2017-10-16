package no.kreutzer.utils;

public interface SocketCommand {
	String calStart();
	String calStop();
	String setMode(int mode);
	String setFull(int mode);
	String testSwitch(int mode);
	String getConfig();
	String pump(int i);
	String valve(int i);
	String getState();
	String live(boolean b);
}
