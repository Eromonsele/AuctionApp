import net.jini.core.event.RemoteEvent;
import net.jini.core.event.RemoteEventListener;
import net.jini.core.event.UnknownEventException;
import net.jini.core.lease.Lease;
import net.jini.export.Exporter;
import net.jini.jeri.BasicILFactory;
import net.jini.jeri.BasicJeriExporter;
import net.jini.jeri.tcp.TcpServerEndpoint;
import net.jini.space.JavaSpace05;
import net.jini.space.MatchSet;

import javax.swing.*;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;

public class AddItemRefresh implements RemoteEventListener {

	private JavaSpace05 space;
	private RemoteEventListener theStub;
	private AuctionGUI auctionGUI;

	private static final long TWO_MINUTES = 1000 * 60;
	private final static int FIVE_SECONDS = 1000 * 5; // that's 5000 Milliseconds
	private final static int NUMBER_OF_OBJECTS_TO_RETURN = 100;

	public AddItemRefresh(AuctionGUI acAuctionGUI) {
		// find the space
		space = (JavaSpace05) SpaceUtils.getSpace();
		if (space == null){
			System.err.println("Failed to find the javaspace");
			System.exit(1);
		}

		auctionGUI = acAuctionGUI;

		// create the exporter
		Exporter myDefaultExporter =
				new BasicJeriExporter(TcpServerEndpoint.getInstance(0),
						new BasicILFactory(), false, true);

		try {
			// register this as a remote object
			// and get a reference to the 'stub'
			theStub = (RemoteEventListener) myDefaultExporter.export(this);

			// add the listener
			EOKHLot template = new EOKHLot();
			space.notify(template, null, this.theStub, Lease.FOREVER, null);

//			EOKHBid bidTemplate = new EOKHBid();
//			space.notify(bidTemplate, null, this.theStub, Lease.FOREVER, null);

		} catch (Exception e) {
			e.printStackTrace();
		}

		// create an example object being listened for
		try{
			EOKHLot msg = new EOKHLot();
			space.write(msg, null, Lease.FOREVER);
//			EOKHBid bidMsg = new EOKHBid();
//			space.write(bidMsg, null, Lease.FOREVER);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void notify(RemoteEvent remoteEvent) throws UnknownEventException, RemoteException {
		System.out.println("Refresh Triggered");
		DefaultListModel<EOKHLot> lotsCollection = new DefaultListModel<EOKHLot>();

		Collection<EOKHLot> templates = new ArrayList<EOKHLot>();
		EOKHLot template = new EOKHLot();
		template.sold = false;
		templates.add(template);
		try {

			MatchSet results = space.contents(templates, null, FIVE_SECONDS, NUMBER_OF_OBJECTS_TO_RETURN);
			EOKHLot result = (EOKHLot)results.next();
			while (result != null){
				if (result.sold == false){
					lotsCollection.addElement(result);
				}
				result = (EOKHLot) results.next();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		auctionGUI.updateScreen(lotsCollection);

	}
}
