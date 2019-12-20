import net.jini.core.entry.Entry;

import java.util.Date;


public class Message implements Entry {
	//Fields
	public String messageType; // The type of message
	public String otherUser; // The sender of the message
	public String owner; // The receiver of the message
	public String itemName;// The Name of the Item

	// Creates a new Message
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
