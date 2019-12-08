import net.jini.core.entry.Entry;

public class Lot implements Entry {

    public String lotName;
    public String lotDescription;
    public Float lotStartPrice;
    public Float lotBuyOutPrice;
    public EOKUser lotOwner;
    public Boolean sold;


    public Lot(){

    }
    public Lot(EOKUser owner){
        lotOwner = owner;
    }

    public Lot(EOKUser owner, String lName){
        lotOwner = owner;
        lotName = lName;
    }

}
