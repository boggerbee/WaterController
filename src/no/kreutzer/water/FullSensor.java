package no.kreutzer.water;

import no.kreutzer.water.Tank.State;

public interface FullSensor {
	public boolean isFull(State state);
}
