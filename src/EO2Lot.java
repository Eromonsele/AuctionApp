import net.jini.core.entry.Entry;

import java.util.ArrayList;

public class EO2Lot implements Entry {

    public String lotName;
    public String lotDescription;
    public Float lotStartPrice;
    public Float lotBuyOutPrice;
    public EOK3User lotOwner;
    public Boolean sold;
    public ArrayList<Bid> bids;


    public EO2Lot(){

    }
    public EO2Lot(EOK3User owner){
        lotOwner = owner;
    }

    public EO2Lot(EOK3User owner, String lName){
        lotOwner = owner;
        lotName = lName;
    }

    @Override
    public String toString() {
        return lotName;
    }
}
