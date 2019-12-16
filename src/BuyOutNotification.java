import net.jini.core.event.RemoteEvent;
import net.jini.core.event.RemoteEventListener;
import net.jini.core.event.UnknownEventException;

import java.rmi.RemoteException;

public class BuyOutNotification implements RemoteEventListener {

	public BuyOutNotification() {
	}

	@Override
	public void notify(RemoteEvent remoteEvent) throws UnknownEventException, RemoteException {

	}
}
