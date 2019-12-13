import net.jini.core.event.RemoteEvent;
import net.jini.core.event.RemoteEventListener;
import net.jini.core.event.UnknownEventException;

import java.rmi.RemoteException;

public class buyoutNotification implements RemoteEventListener {

    public buyoutNotification(){

    }

    @Override
    public void notify(RemoteEvent remoteEvent) throws UnknownEventException, RemoteException {

    }
}
