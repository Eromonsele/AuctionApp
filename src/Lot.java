import net.jini.core.entry.Entry;

public class Lot implements Entry {

    public String lotName;
    public String lotDescription;
    public float lotStartPrice;
    public float lotBuyOutPrice;
    public EOKUser lotOwner;
    public boolean sold;


    public Lot(){

    }
    public Lot(EOKUser owner){
        this.lotOwner = owner;
    }

    public Lot(EOKUser owner, String lotName){
        this.lotOwner = owner;
        this.lotName = lotName;
    }

}
