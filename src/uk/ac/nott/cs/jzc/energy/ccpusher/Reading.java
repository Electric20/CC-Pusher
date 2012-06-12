package uk.ac.nott.cs.jzc.energy.ccpusher;

public class Reading
{

	private int sensorId;
	private double value;

	public Reading(int sensorId, double value)
	{
		this.sensorId = sensorId;
		this.value = value;
	}

	public int getSensorId()
	{
		return sensorId;
	}

	public double getValue()
	{
		return value;
	}

}
