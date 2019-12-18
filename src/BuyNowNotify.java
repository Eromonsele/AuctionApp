import net.jini.core.entry.UnusableEntryException;
import net.jini.core.event.RemoteEvent;
import net.jini.core.event.RemoteEventListener;
import net.jini.core.event.UnknownEventException;
import net.jini.core.lease.Lease;
import net.jini.core.transaction.TransactionException;
import net.jini.space.AvailabilityEvent;
import net.jini.space.JavaSpace05;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class BuyNowNotify {
	private static final int FIVE_SECONDS = 1000 * 5;

	public static void main(String[] args) {
		// Get a reference to the space, and cast it to a JavaSpace05
		JavaSpace05 space = (JavaSpace05) SpaceUtils.getSpace();
		if(space == null) {
			System.err.println("JavaSpace not found.");
			System.exit(1);
		}
		List<Bid> templates = new ArrayList<>();

		Bid template = new Bid();
		templates.add(template);

		final Object lock = new Object();

		RemoteEventListener listener = new RemoteEventListener() {
			@Override
			public void notify(RemoteEvent remoteEvent) {
				// Cast the RemoteEvent to an AvailabilityEvent, as this adds extra functionality
				AvailabilityEvent event = (AvailabilityEvent) remoteEvent;

				try {
					Bid bid = (Bid) event.getEntry();

					System.out.println("crazy " + bid.bidder);
				} catch (UnusableEntryException e) {
					e.printStackTrace();
				} finally {
					// Remember - sequential execution is required to make this test work
					// Comment this out to try it without the locking
					synchronized(lock) {
						lock.notify();
					}
				}
			}
		};

		try {
			// export the listener object, so its "notify" method can be called remotely from the space
			UnicastRemoteObject.exportObject(listener, 0);

			// add the "registerForAvailabilityEvent, much like adding a "notify" to the space
			space.registerForAvailabilityEvent(templates, null, false, listener, Lease.FOREVER, null);

			Bid bid = new Bid();
			space.write(bid,null,FIVE_SECONDS);
			System.out.println("drake");

		} catch (RemoteException | TransactionException e) {
			e.printStackTrace();
		}

	}
}
