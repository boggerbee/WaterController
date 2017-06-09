package no.kreutzer.water;

import no.kreutzer.water.FullSwitch.State;

public interface FullEventHandler {
	void onChange(State state);
}
