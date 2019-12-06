import net.jini.core.entry.Entry;

public class Bid implements Entry {
    public Lot lotItem;
    public float bidValue;
    public EOKUser bidder;

    public Bid() {

    }

    public Bid(EOKUser lOwner){
        this.bidder = lOwner;
    }

    public Bid(EOKUser lOwner, float bValue){
        this.bidder = lOwner;
        this.bidValue = bValue;
    }

    public Bid(Lot item){
        this.lotItem = item;
    }
}
