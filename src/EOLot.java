import net.jini.core.entry.Entry;

public class EOLot implements Entry {

    public String lotName;
    public String lotDescription;
    public Float lotStartPrice;
    public Float lotBuyOutPrice;
    public EOKUser lotOwner;
    public Boolean sold;


    public EOLot(){

    }
    public EOLot(EOKUser owner){
        lotOwner = owner;
    }

    public EOLot(EOKUser owner, String lName){
        lotOwner = owner;
        lotName = lName;
    }

}
