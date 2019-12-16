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

public class BuyOutNotification implements RemoteEventListener {

	private JavaSpace05 space;
	private RemoteEventListener theStub;
	private JList<EO2Lot> featuredList;

	private final static int FIVE_SECONDS = 1000 * 5; // that's 5000 Milliseconds
	private final static int NUMBER_OF_OBJECTS_TO_RETURN = 100;

	public BuyOutNotification(JList<EO2Lot> fList) {
		// find the space
		space = (JavaSpace05) SpaceUtils.getSpace();
		if (space == null){
			System.err.println("Failed to find the javaspace");
			System.exit(1);
		}

		featuredList = fList;

		// create the exporter
		Exporter myDefaultExporter =
				new BasicJeriExporter(TcpServerEndpoint.getInstance(0),
						new BasicILFactory(), false, true);

		try {
			// register this as a remote object
			// and get a reference to the 'stub'
			theStub = (RemoteEventListener) myDefaultExporter.export(this);

			// add the listener
			EO2Lot template = new EO2Lot();
			template.sold = true;
			space.notify(template, null, this.theStub, Lease.FOREVER, null);

		} catch (Exception e) {
			e.printStackTrace();
		}

		// create an example object being listened for
		try{
			EO2Lot msg = new EO2Lot();
			msg.sold = true;
			space.write(msg, null, Lease.FOREVER);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void notify(RemoteEvent remoteEvent) throws UnknownEventException, RemoteException {
		System.out.println("notification is working");
		DefaultListModel<EO2Lot> lotsCollection = new DefaultListModel<EO2Lot>();

		Collection<EO2Lot> templates = new ArrayList<EO2Lot>();
		EO2Lot template = new EO2Lot();
		templates.add(template);
		try {

			MatchSet results = space.contents(templates, null, FIVE_SECONDS, NUMBER_OF_OBJECTS_TO_RETURN);
			EO2Lot result = (EO2Lot)results.next();
			while (result != null){
				lotsCollection.addElement(result);
				result = (EO2Lot) results.next();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		featuredList.setModel(lotsCollection);
	}
}
