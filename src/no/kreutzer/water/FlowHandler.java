package no.kreutzer.water;

public interface FlowHandler {
	/**
	 * 
	 * @param total		Total pulse count
	 * @param current	Current pulses per second 
	 */
	void onCount(int total, int current);

}
