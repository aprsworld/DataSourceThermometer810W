import java.io.IOException;

import javax.comm.SerialPort;


public class DataSourceThermometer810W implements Listener810W {
	public static final boolean debug=false;

	/* fire it off via TCP/IP */
	public void packetReceivedTemperature(int[] rawBuffer) {
		int lChecksum=0;


		for ( int i=0 ; i<rawBuffer.length ; i++ ) {
			if ( debug ) {
				System.err.printf("# [%d] 0x%02x\n",i,rawBuffer[i]);
			}

			lChecksum += rawBuffer[i];
		}

		int rChecksum = rawBuffer[rawBuffer.length-1];
		/* remove the last element, which is the checksum */
		lChecksum -= rChecksum;

		/* single byte checksum */
		lChecksum = (lChecksum & 0xff);
		if ( debug ) {
			System.err.printf("\n\n# sum=0x%x\n", lChecksum);
		}

		/* all this checksum stuff should be done in SerialReader810W */
		if ( lChecksum != rChecksum ) {
			System.err.println("# checksum failure");
			return;
		}


		int t1 = (rawBuffer[11]<<8) + rawBuffer[12];
		if ( t1 > 32768 ) {
			t1 = t1 - 65535;
		}
		double t1DegreesC = t1 / 10.0;
		double t1DegreesF = t1DegreesC * 1.8 + 32.0;

		System.err.printf("# T1 = %.1f degrees C / %.1f degrees F\n", t1DegreesC,t1DegreesF);

		int t2 = (rawBuffer[16]<<8) + rawBuffer[17];
		if ( t2 > 32768 ) {
			t2 = t2 - 65535;
		}
		double t2DegreesC = t2 / 10.0;
		double t2DegreesF = t2DegreesC * 1.8 + 32.0;


		System.err.printf("# T2 = %.1f degrees C / %.1f degrees F\n", t2DegreesC,t2DegreesF);


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
