import net.jini.core.entry.Entry;

public class Bid implements Entry {
    public EOLot EOLotItem;
    public Float bidValue;
    public EOUser bidder;

    public Bid() {

    }

    public Bid(EOUser lOwner){
        bidder = lOwner;
    }

    public Bid(EOUser lOwner, float bValue){
        bidder = lOwner;
        bidValue = bValue;
    }

    public Bid(EOLot item){
        EOLotItem = item;
    }
}
