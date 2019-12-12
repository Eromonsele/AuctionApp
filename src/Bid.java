import net.jini.core.entry.Entry;

public class Bid implements Entry {
    public EOLot EOLotItem;
    public Float bidValue;
    public EOKUser bidder;

    public Bid() {

    }

    public Bid(EOKUser lOwner){
        bidder = lOwner;
    }

    public Bid(EOKUser lOwner, float bValue){
        bidder = lOwner;
        bidValue = bValue;
    }

    public Bid(EOLot item){
        EOLotItem = item;
    }
}
