import net.jini.core.entry.Entry;

import java.util.Date;

public class Message implements Entry {
	public String messageType;
	public String otherUser;
	public String owner;
	public String itemName;

	public Message(){

	}

	@Override
	public String toString() {
		switch (messageType){
			case "bidByOther":
				return "On " + new Date().toString() + ": User: " + otherUser + "bid on "+ itemName;
			case "bidByOwner":
				return "On " + new Date().toString() + ": You placed a bid on "+ itemName;
			case "buyOut":
				return "On " + new Date().toString() + ": User: " + otherUser + "bought "+ itemName;
			default:
				return "";
		}
	}
}
