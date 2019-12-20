import net.jini.core.entry.Entry;

/**
 * A Bid object represents a user bid on an lot.
 */
public class EOKHBid implements Entry {
    //Fields
    public EOKHLot EOKHLotItem; // The lot item that a bid is placed on
    public Double bidValue; // The value of the bid
    public EOKHUser bidder; // The bidder
    public String bidId;

    //Creates a new Bid
    public EOKHBid() {

    }

    // Creates a new bid with the specified lot, bid value and bidder
    public EOKHBid(EOKHLot item, Double bValue, EOKHUser owner){
        EOKHLotItem = item;
        bidValue = bValue;
        bidder = owner;
    }

    @Override
    public String toString() {
        return "Bidder: " + bidder + "\nBid Value: £" + bidValue.toString() + "\n\n";
//        return null;
    }
}
