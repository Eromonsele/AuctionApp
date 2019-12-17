import net.jini.core.entry.UnusableEntryException;
import net.jini.core.event.RemoteEvent;
import net.jini.core.event.RemoteEventListener;
import net.jini.core.event.UnknownEventException;
import net.jini.core.lease.Lease;
import net.jini.export.Exporter;
import net.jini.jeri.BasicILFactory;
import net.jini.jeri.BasicJeriExporter;
import net.jini.jeri.tcp.TcpServerEndpoint;
import net.jini.space.AvailabilityEvent;
import net.jini.space.JavaSpace05;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.rmi.RemoteException;
import java.util.Date;

public class BidNotification implements RemoteEventListener {
	private JavaSpace05 space;
	private RemoteEventListener theStub;
	private JTextArea notifyArea;


	private final static int FIVE_SECONDS = 1000 * 5; // that's 5000 Milliseconds
	private final static int NUMBER_OF_OBJECTS_TO_RETURN = 100;

	public BidNotification() {


		space = (JavaSpace05) SpaceUtils.getSpace();
		if (space == null){
			System.err.println("Failed to find the javaspace");
			System.exit(1);
		}

		// create the exporter
		Exporter myDefaultExporter =
				new BasicJeriExporter(TcpServerEndpoint.getInstance(0),
						new BasicILFactory(), false, true);

		try {
			// register this as a remote object
			// and get a reference to the 'stub'
			theStub = (RemoteEventListener) myDefaultExporter.export(this);

			// add the listener
			Bid template = new Bid();
			space.notify(template, null, this.theStub, Lease.FOREVER, null);

		} catch (Exception e) {
			e.printStackTrace();
		}

		// create an example object being listened for
		try{
			Bid msg = new Bid();
			space.write(msg, null, Lease.FOREVER);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void notify(RemoteEvent remoteEvent) throws UnknownEventException, RemoteException {
		System.out.println("kinda works" );



	}
}
