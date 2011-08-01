package uk.ac.nott.cs.horizon.energy.data.push.currentcost;

public class Reading {
	
	private int sensorId;
	private double value;
	
	public Reading (int sensorId, double value) {
		setSensorId(sensorId);
		setValue(value);
	}
	
	public void setSensorId(int sensorId) {
		this.sensorId = sensorId;
	}
	public int getSensorId() {
		return sensorId;
	}
	public void setValue(double value) {
		this.value = value;
	}
	public double getValue() {
		return value;
	}

}
