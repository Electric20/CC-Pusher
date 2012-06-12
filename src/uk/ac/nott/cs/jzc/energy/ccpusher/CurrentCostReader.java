package uk.ac.nott.cs.jzc.energy.ccpusher;

import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Scanner;
import java.util.TooManyListenersException;

public class CurrentCostReader implements Runnable, SerialPortEventListener
{
	static CommPortIdentifier	portId;
	@SuppressWarnings("rawtypes")
	static Enumeration			portList;
	Scanner						inputScanner;
	SerialPort					serialPort;
	Thread						readThread;
	static int					hubId;
	static String				user;
	static String				apiKey;

	public static void main(String[] args)
	{
		boolean portFound = false;
		String defaultPort = "/dev/tty.usbserial";
		if (args.length != 4)
		{
			System.out.println("YOU FAIL");
			System.exit( - 1);
		}
		else
		{
			hubId = Integer.parseInt(args[0]);
			defaultPort = args[1];
			user = args[2];
			apiKey = args[3];
		}
		portList = CommPortIdentifier.getPortIdentifiers();
		while (portList.hasMoreElements())
		{
			portId = (CommPortIdentifier)portList.nextElement();
			if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL)
			{
				if (portId.getName().equals(defaultPort))
				{
					System.out.println("Found port: " + defaultPort);
					portFound = true;
					@SuppressWarnings("unused")
					CurrentCostReader reader = new CurrentCostReader();
				}
			}
		}
		if ( ! portFound)
		{
			System.out.println("port " + defaultPort + " not found.");
		}

	}

	public CurrentCostReader()
	{
		try
		{
			serialPort = (SerialPort)portId.open("SimpleReadApp", 2000);
			inputScanner = new Scanner(serialPort.getInputStream());
			serialPort.addEventListener(this);
			serialPort.notifyOnDataAvailable(true);
			serialPort.setSerialPortParams(57600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
			readThread = new Thread(this);
			readThread.start();

		}
		catch (PortInUseException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (TooManyListenersException e)
		{
			e.printStackTrace();
		}
		catch (UnsupportedCommOperationException e)
		{
			e.printStackTrace();
		}

	}

	public void run()
	{

	}

	public void serialEvent(SerialPortEvent event)
	{
		switch (event.getEventType())
		{

			case SerialPortEvent.BI:

			case SerialPortEvent.OE:

			case SerialPortEvent.FE:

			case SerialPortEvent.PE:

			case SerialPortEvent.CD:

			case SerialPortEvent.CTS:

			case SerialPortEvent.DSR:

			case SerialPortEvent.RI:

			case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
				break;

			case SerialPortEvent.DATA_AVAILABLE:
				ArrayList<Reading> readings = new ArrayList<Reading>();
				System.out.println("starting input scanner");
				while (inputScanner.hasNext())
				{

					String parsableLine = inputScanner.next();
					System.out.println(parsableLine);
					if(parsableLine.contains("hist"))
					{
						// do nothing
					}
					else
					{
						if(parsableLine.contains("msg"))
						{
							try
							{
								int sensorId = Integer.parseInt(parseSingleElement(parsableLine, "sensor"));
								double value = Double.parseDouble(parseSingleElement(parsableLine, "watts"));
								readings.add(new Reading(sensorId, value));
							}
							catch (NumberFormatException n)
							{
								n.printStackTrace();
							}
							if (parsableLine.contains("</msg>"))
							{

								ReadingSet rSet = new ReadingSet(user, apiKey, hubId, System.currentTimeMillis(), readings);
								try
								{
									rSet.upload();
								}
								catch (FileNotFoundException e)
								{
									e.printStackTrace();
								}
								readings.clear();
							}
						}
					}
				}
		}
	}

	public String parseSingleElement(String m, String t)
	{
		int start = m.indexOf("<" + t + ">") + t.length() + 2;
		int end = m.indexOf("</" + t + ">");
		return (m.substring(start, end));
	}
}
