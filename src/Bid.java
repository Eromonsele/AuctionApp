import net.jini.core.entry.Entry;

public class Bid implements Entry {
    public Lot LotItem;
    public Float bidValue;

    public Bid() {

    }

    public Bid(Lot item, float bValue){
        LotItem = item;
        bidValue = bValue;
    }

    @Override
    public String toString() {
        return bidValue.toString();
    }
}
