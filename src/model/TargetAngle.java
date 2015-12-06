package model;

public enum TargetAngle {
	A0(0, "0"),
	A45(1, "45");
	
	private int value;
	private String name;

	private TargetAngle(int value, String name) {
		this.value = value;
		this.name = name;
	}

	public int getValue() { 
		return value;
	}

	@Override
	public String toString() {
		return name;
	}
}