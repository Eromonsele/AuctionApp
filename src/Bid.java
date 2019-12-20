import net.jini.core.entry.Entry;

/**
 * A Bid object represents a user bid on an lot.
 */
public class Bid implements Entry {
    //Fields
    public EO2Lot EO2LotItem; // The lot item that a bid is placed on
    public Double bidValue; // The value of the bid
    public EOKUser bidder; // The bidder

    //Creates a new Bid
    public Bid() {

    }

    // Creates a new bid with the specified lot, bid value and bidder
    public Bid(EO2Lot item, Double bValue, EOKUser owner){
        EO2LotItem = item;
        bidValue = bValue;
        bidder = owner;
    }

    @Override
    public String toString() {
//        return "Bidder: " + bidder + "\nBid Value: £" + bidValue.toString() + "\n\n";
        return null;
    }
}
