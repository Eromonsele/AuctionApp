import net.jini.core.entry.Entry;
import java.util.ArrayList;

/**
 * The EO2Lot class represents the lots to be added and bought or bidded by the users.
 */

public class EO2Lot implements Entry {

    //Fields
    public String lotName; //Name of the Lot
    public String lotDescription; // Description of the Lot
    public Double lotStartPrice; // Starting Bid Price of the lot
    public Double lotBuyOutPrice; // Buy Now price for the lot
    public EOKUser lotOwner; // Owner of the Lot
    public Boolean sold; // true If lot item is Sold, false Otherwise
    public ArrayList<Bid> bids; // a collection of Bids for that item

    // Creates a lot
    public EO2Lot(){

    }

    // Creates or gets a lot with the specified owner
    public EO2Lot(EOKUser owner){
        lotOwner = owner;
    }

    // Creates or gets a lot with the specified owner or lot name
    public EO2Lot(EOKUser owner, String lName){
        lotOwner = owner;
        lotName = lName;
    }

    @Override
    public String toString() {
        return lotName;
    }


}
