package no.kreutzer.utils;

import no.kreutzer.water.Tank;

import com.fasterxml.jackson.annotation.JsonRootName;

import no.kreutzer.water.Controller.Mode;

@JsonRootName("config")
public class ConfigPOJO {
	private String id = "Almedalen25";
	private String restEndPoint = "http://data.kreutzer.no/dataserver";
	private Mode fillMode = Mode.FAST;
	private String wsEndPoint = "ws://data.kreutzer.no/dataserver/websocket";
	private long totalFlow = 0;
	private Tank.FullMode fullMode = Tank.FullMode.SWITCH;
	private boolean liveFlow = false;
	private String flowSensorClassName = "no.kreutzer.water.HallEffectFlowMeter";
	private String ADCClassName = "no.kreutzer.water.ADS1115";
	private int pulsesPerLitre = 585;
	
	public int getPulsesPerLitre() {
        return pulsesPerLitre;
    }
    public void setPulsesPerLitre(int pulsesPerLitre) {
        this.pulsesPerLitre = pulsesPerLitre;
    }
    public String getADCClassName() {
		return ADCClassName;
	}
	public void setADCClassName(String aDCClassName) {
		ADCClassName = aDCClassName;
	}
	
	public String getFlowSensorClassName() {
		return flowSensorClassName;
	}
	public void setFlowSensorClassName(String flowSensorClassName) {
		this.flowSensorClassName = flowSensorClassName;
	}
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
	public long getTotalFlow() {
		return totalFlow;
	}
	public void setTotalFlow(long totalFlow) {
		this.totalFlow = totalFlow;
	}
	public Tank.FullMode getFullMode() {
		return fullMode;
	}
	public void setFullMode(Tank.FullMode fullMode) {
		this.fullMode = fullMode;
	}
	public void setLiveFlow(boolean b) {
		liveFlow = b;
	}
	public boolean isLiveFlow() {
		return liveFlow;
	}


}
