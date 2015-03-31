package team120;

public enum Strategy {
	NONE(0),
	TANKS(1),
	TANKS_DRONES(2),
	DRONES(3);
	
	int value;
	
	private Strategy(int val) {
		this.value = val;
	}
}
