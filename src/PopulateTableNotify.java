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

    private static final long TWO_SECONDS = 2 * 1000;  // two thousand milliseconds
    private static final long TWO_MINUTES = 2 * 1000 * 60;
    private static final long ONE_SECOND = 1000;  // one thousand milliseconds
    private static int THREE_SECONDS = 3000;  // 3000 milliseconds

    public PopulateTableNotify() {

        space = SpaceUtils.getSpace();
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

    }


}
