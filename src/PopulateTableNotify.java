import net.jini.core.event.*;
import net.jini.core.lease.Lease;
import net.jini.export.Exporter;
import net.jini.jeri.BasicILFactory;
import net.jini.jeri.BasicJeriExporter;
import net.jini.jeri.tcp.TcpServerEndpoint;
import net.jini.space.JavaSpace;

import java.rmi.RemoteException;
import java.rmi.server.ExportException;

public class PopulateTableNotify implements RemoteEventListener {
    private JavaSpace space;
    private RemoteEventListener theStub;
    private SessionManager sessionManager;


    public PopulateTableNotify(SessionManager sManager) {

        sessionManager = sManager;
        space = sessionManager.getSpace();
        // create the exporter
        Exporter myDefaultExporter =
                new BasicJeriExporter(TcpServerEndpoint.getInstance(0),
                        new BasicILFactory(), false, true);

        try {
            // register this as a remote object
            // and get a reference to the 'stub'
            theStub = (RemoteEventListener) myDefaultExporter.export(this);

            // add the listener
            Lot template = new Lot();
            space.notify(template,null, this.theStub, Lease.FOREVER, null);

        } catch (Exception e) {
            e.printStackTrace();
        }

//        // create an example object being listened for
//        try{
//            Lot msg = new Lot();
//            space.write(msg, null, Lease.FOREVER);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }


    @Override
    public void notify(RemoteEvent remoteEvent) throws UnknownEventException, RemoteException {
        // this is the method called when we are notified
        // of an object of interest

        System.out.println("update list now fucker");
    }
}
