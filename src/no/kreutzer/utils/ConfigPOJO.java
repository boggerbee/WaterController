package no.kreutzer.utils;

import no.kreutzer.water.Controller.Mode;

public class ConfigPOJO {
	private String id = "Almedalen25";
	private String restEndPoint = "http://data.kreutzer.no/dataserver";
	private Mode fillMode = Mode.FAST;
	private String wsEndPoint = "ws://data.kreutzer.no/dataserver/websocket";
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getRestEndPoint() {
		return restEndPoint;
	}
	public void setRestEndPoint(String restEndPoint) {
		this.restEndPoint = restEndPoint;
	}
	public Mode getFillMode() {
		return fillMode;
	}
	public void setFillMode(Mode fillMode) {
		this.fillMode = fillMode;
	}
	public String getWsEndPoint() {
		return wsEndPoint;
	}
	public void setWsEndPoint(String wsEndPoint) {
		this.wsEndPoint = wsEndPoint;
	}

}
