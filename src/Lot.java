import net.jini.core.entry.Entry;

public class Lot implements Entry {

    private String itemName;
    private float startingPrice;
    private float buyOutPrice;
    private User creator;
    private String lotID;


    public Lot(){

    }

    public Lot(String lotID){
        if (lotID != null && lotID.length() > 0){
            this.lotID = lotID;
        }

    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public float getStartingPrice() {
        return startingPrice;
    }

    public void setStartingPrice(float startingPrice) {
        this.startingPrice = startingPrice;
    }

    public float getBuyOutPrice() {
        return buyOutPrice;
    }

    public void setBuyOutPrice(float buyOutPrice) {
        this.buyOutPrice = buyOutPrice;
    }

    public User getCreator() {
        return creator;
    }
}
