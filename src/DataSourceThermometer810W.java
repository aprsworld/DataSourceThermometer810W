import java.io.IOException;

import javax.comm.SerialPort;


public class DataSourceThermometer810W implements Listener810W {
	/* fire it off via TCP/IP */
	public void packetReceivedTemperature(int[] rawBuffer) {
		
		
	}
	
	public void run(String[] args) throws IOException {
		SerialReader810W ser810W = new SerialReader810W("COM26", 57600, SerialPort.PARITY_EVEN);
		
		ser810W.addPacketListener(this);
		
	}

	public static void main(String[] args) throws IOException {
		System.err.println("# Major version: 2014-10-26 (precision)");
		System.err.println("# java.library.path: " + System.getProperty( "java.library.path" ));

		DataSourceThermometer810W d = new DataSourceThermometer810W();
		d.run(args);
	}




}
