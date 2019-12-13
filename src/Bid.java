import net.jini.core.entry.Entry;

public class Bid implements Entry {
    public EO2Lot EO2LotItem;
    public Float bidValue;
    public EOK3User bidder;

    public Bid() {

    }

    public Bid(EO2Lot item, float bValue){
        EO2LotItem = item;
        bidValue = bValue;
    }

    @Override
    public String toString() {
        return bidValue.toString();
    }
}
