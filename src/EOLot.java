import net.jini.core.entry.Entry;

import java.util.ArrayList;

public class EOLot implements Entry {

    public String lotName;
    public String lotDescription;
    public Float lotStartPrice;
    public Float lotBuyOutPrice;
    public EOUser lotOwner;
    public Boolean sold;
    public ArrayList<Bid> bids;


    public EOLot(){

    }
    public EOLot(EOUser owner){
        lotOwner = owner;
    }

    public EOLot(EOUser owner, String lName){
        lotOwner = owner;
        lotName = lName;
    }

}
