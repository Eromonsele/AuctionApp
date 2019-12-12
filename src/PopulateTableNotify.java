import net.jini.core.event.*;
import net.jini.core.lease.Lease;
import net.jini.core.transaction.Transaction;
import net.jini.core.transaction.TransactionFactory;
import net.jini.core.transaction.server.TransactionManager;
import net.jini.export.Exporter;
import net.jini.jeri.BasicILFactory;
import net.jini.jeri.BasicJeriExporter;
import net.jini.jeri.tcp.TcpServerEndpoint;
import net.jini.space.JavaSpace;

import javax.swing.*;
import java.rmi.RemoteException;

public class PopulateTableNotify implements RemoteEventListener {
    private JavaSpace space;
    private RemoteEventListener theStub;
    private SessionManager sessionManager;

    private TransactionManager mgr;
    private JList featuredList;

    private static final long TWO_SECONDS = 2 * 1000;  // two thousand milliseconds
    private static final long TWO_MINUTES = 2 * 1000 * 60;
    private static final long ONE_SECOND = 1000;  // one thousand milliseconds
    private static int THREE_SECONDS = 3000;  // 3000 milliseconds

    public PopulateTableNotify(JList fList) {

        mgr = SpaceUtils.getManager();
        if (mgr == null) {
            System.err.println("Failed to find the transaction manager");
            System.exit(1);
        }

        space = SpaceUtils.getSpace();
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
            EOLot template = new EOLot();
            space.notify(template,null, this.theStub, Lease.FOREVER, null);

        } catch (Exception e) {
            e.printStackTrace();
        }

        // create an example object being listened for
        try{
            EOLot msg = new EOLot();
            space.write(msg, null, ONE_SECOND);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void notify(RemoteEvent remoteEvent) throws UnknownEventException, RemoteException {
        // this is the method called when we are notified
        // of an object of interest
        DefaultListModel<String> temp = new DefaultListModel<String>();

        for (int i = 0; i < getAllLots().getSize(); i++) {
            temp.addElement(getAllLots().get(i).lotName);
        }
        featuredList.setModel(temp);

    }

    public DefaultListModel<EOLot> getAllLots(){
        DefaultListModel<EOLot> lotsCollection = new DefaultListModel<EOLot>();

        try {
            Transaction.Created trc = null;

            try {
                trc = TransactionFactory.create(mgr, THREE_SECONDS);
            } catch (Exception e) {
                System.out.println("Could not create transaction " + e);
            }

            Transaction txn = trc.transaction;
            int counter = 0;
            while(true) {
                try {
                    EOLot EOLotItem = new EOLot();
                    EOLot lots = (EOLot) space.takeIfExists(EOLotItem, txn, ONE_SECOND);
                    if (lots == null) {
                        break;
                    } else {
//                        System.out.println(lots.lotName);
                        lotsCollection.addElement(lots);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    txn.abort();
                    break;
                }
            }

            // ... and commit the transaction.
            txn.commit();
            for (int i = 0; i < lotsCollection.getSize(); i++) {
                space.write(lotsCollection.get(i),null,Lease.FOREVER);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
//		}
        return lotsCollection;

    }
}
