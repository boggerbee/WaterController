package no.kreutzer.water;

import no.kreutzer.water.FullSensor.State;

public interface FullEventHandler {
	void onChange(State state);
}
