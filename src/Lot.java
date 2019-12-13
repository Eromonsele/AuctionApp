import net.jini.core.entry.Entry;

import java.util.ArrayList;

public class Lot implements Entry {

    public String lotName;
    public String lotDescription;
    public Float lotStartPrice;
    public Float lotBuyOutPrice;
    public EOKUser lotOwner;
    public Boolean sold;
    public ArrayList<Bid> bids;


    public Lot(){

    }
    public Lot(EOKUser owner){
        lotOwner = owner;
    }

    public Lot(EOKUser owner, String lName){
        lotOwner = owner;
        lotName = lName;
    }

    @Override
    public String toString() {
        return lotName;
    }
}
