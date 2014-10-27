
import java.io.IOException;
import java.util.TooManyListenersException;
import java.util.Vector;

import javax.comm.SerialPortEvent;
import javax.comm.SerialPortEventListener;
//import gnu.io.*;

public class SerialReader810W extends Thread implements SerialPortEventListener {
	LinkSerial link;
	Boolean connected;
	Vector<Listener810W> packetListeners;

	Vector<Integer> buff;
	long lastCharacter;

	int lastCRC;



	public void addPacketListener(Listener810W b) {
		packetListeners.add(b);
	}


	private void addChar(int c) {

		long now=System.currentTimeMillis();
		long age=now - lastCharacter;



		if ( buff.size() > 0 && age > 250 ) {
			System.err.println("# SerialReader810W clearing buffer (length=" + buff.size() + " age=" + age + ")");
			buff.clear();
		}
		lastCharacter=now;


		buff.add(c);
		// System.err.println("# WorldDataSerialReader.addChar was called with character=" + c + " buff.size()=" + buff.size() + " buff.elementAt(buff.size()-1)=" + buff.elementAt(buff.size()-1));

		/* if we have a short packet, we don't need to further test it */
		if ( buff.size() <= 10 ) {
			return;
		}
		

		if ( 26 == buff.size() ) {
		//	System.out.println("# CRC matches ... have valid packet");
			
			int b[]=new int[buff.size()];

			/* copy vector to integer array */
			for ( int i=0 ; i<buff.size() ; i++ ) {
				b[i]=(int) buff.elementAt(i);
			}
			
			
			/* send packet to listeners */
			for ( int i=0 ; i<packetListeners.size(); i++ ) {
				packetListeners.elementAt(i).packetReceivedTemperature(b);
			}
			
			/* clear for our next pass through */
			buff.clear();
		}

	}


	/* triggered by a SerialPortEvent ... which is new data being available */
	public void serialEvent(SerialPortEvent event) {
		if ( SerialPortEvent.DATA_AVAILABLE == event.getEventType() ) {
			try { 
				while ( link.is.available() > 0 ) {
					int c=0;
					try { 
						c = link.is.read();
					} catch (IOException e) {
						e.printStackTrace();
						return;
					}
					/* actually read the character */
					addChar(c);
				}
			} catch ( IOException e ) {
				e.printStackTrace();
			}
		}

	}


	public SerialReader810W(String spName, int spSpeed, int spParity) {
		buff = new Vector<Integer>();
		packetListeners = new Vector<Listener810W>();
		lastCharacter=0;

		link = new LinkSerial(spName,spSpeed,spParity);

		if ( null == link || false == link.Connect()) {
			System.err.println("# Error establishing serial link to device");
			connected=false;
			return;
		}
		connected=true;

		try {
			link.p.addEventListener(this);
		} catch ( TooManyListenersException e ) {
			System.err.println("# Serial port only supports one SerialPortEventListener!");
		}

		link.p.notifyOnDataAvailable(true);
	}

	public void close() {
		link.Disconnect();
		connected=false;
	}
}
