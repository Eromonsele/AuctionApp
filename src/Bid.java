import net.jini.core.entry.Entry;

public class Bid implements Entry {
    public EO2Lot EO2LotItem;
    public Double bidValue;
    public EOKUser bidder;

    public Bid() {

    }

    public Bid(EO2Lot item, Double bValue, EOKUser owner){
        EO2LotItem = item;
        bidValue = bValue;
        bidder = owner;
    }

    @Override
    public String toString() {
        return "Bidder: " + bidder + "\nBid Value: £" + bidValue.toString() + "\n\n";
    }
}
