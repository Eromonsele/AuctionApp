import net.jini.core.entry.Entry;
import java.util.ArrayList;
import java.util.Objects;

/**
 * The EO2Lot class represents the lots to be added and bought or bidded by the users.
 */

public class EOKHLot implements Entry {

    //Fields
    public String lotName; //Name of the Lot
    public String lotDescription; // Description of the Lot
    public Double lotStartPrice; // Starting Bid Price of the lot
    public Double lotBuyOutPrice; // Buy Now price for the lot
    public EOKHUser lotOwner; // Owner of the Lot
    public Boolean sold; // true If lot item is Sold, false Otherwise
    public ArrayList<EOKHBid> EOKHBids; // a collection of Bids for that item
    public String lotId;

    // Creates a lot
    public EOKHLot(){

    }

    // Creates or gets a lot with the specified owner
    public EOKHLot(EOKHUser owner){
        lotOwner = owner;
    }

    // Creates or gets a lot with the specified owner or lot name
    public EOKHLot(EOKHUser owner, String lName){
        lotOwner = owner;
        lotName = lName;
    }

    public EOKHLot(String lotId){
        this.lotId = lotId;
    }

    @Override
    public String toString() {
        return lotName;
    }

    @Override
    public int hashCode() {
        return Objects.hash(lotName, lotDescription, lotStartPrice, lotBuyOutPrice, lotOwner,lotId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        EOKHLot eo2Lot = (EOKHLot) obj;
        return lotName.equals(eo2Lot.lotName) &&
                lotDescription.equals(eo2Lot.lotDescription) &&
                lotStartPrice.equals(eo2Lot.lotStartPrice) &&
                lotBuyOutPrice.equals(eo2Lot.lotBuyOutPrice) &&
                lotOwner.equals(eo2Lot.lotOwner) &&
                lotId.equals(eo2Lot.lotId);
    }
}
