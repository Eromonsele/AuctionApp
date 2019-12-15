import net.jini.core.entry.Entry;

import java.util.ArrayList;

public class EO2Lot implements Entry {

    public String lotName;
    public String lotDescription;
    public Double lotStartPrice;
    public Double lotBuyOutPrice;
    public EOKUser lotOwner;
    public Boolean sold;
    public ArrayList<Bid> bids;


    public EO2Lot(){

    }
    public EO2Lot(EOKUser owner){
        lotOwner = owner;
    }

    public EO2Lot(EOKUser owner, String lName){
        lotOwner = owner;
        lotName = lName;
    }

    @Override
    public String toString() {
        return lotName;
    }


}
