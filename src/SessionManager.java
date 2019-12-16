import net.jini.core.entry.Entry;
import net.jini.core.entry.UnusableEntryException;
import net.jini.core.lease.Lease;
import net.jini.core.transaction.Transaction;
import net.jini.core.transaction.TransactionException;
import net.jini.core.transaction.TransactionFactory;
import net.jini.core.transaction.server.TransactionManager;
import net.jini.space.JavaSpace05;

import javax.crypto.spec.PBEKeySpec;
import javax.swing.*;
import java.rmi.RemoteException;
import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class SessionManager {
	private static final long TWO_SECONDS = 2 * 1000;  // two thousand milliseconds
	private static final long TWO_MINUTES = 1000 * 60;
	private static final long ONE_SECOND = 1000;  // one thousand milliseconds
	private static int THREE_SECONDS = 3000;  // 3000 milliseconds
	private final static int FIVE_SECONDS = 1000 * 5; // that's 5000 Milliseconds
	private final static int NUMBER_OF_OBJECTS_TO_RETURN = 100;


	private JavaSpace05 space;
	private TransactionManager mgr;

	public String errorMessage;
	public EOKUser sessionUser;

	public SessionManager() {
		// set up the security manager
		if (System.getSecurityManager() == null)
			System.setSecurityManager(new SecurityManager());

		// Find the java space
		space = (JavaSpace05)SpaceUtils.getSpace();
		if (space == null){
			System.err.println("Failed to find the javaspace");
			System.exit(1);
		}

		// Find the transaction manager on the network
		mgr = SpaceUtils.getManager();
		if (mgr == null) {
			System.err.println("Failed to find the transaction manager");
			System.exit(1);
		}
	}

	/**
	 * registerUser:
	 *
	 * @param userInfo
	 * @return
	 */
	public boolean registerUser(Map<String,String> userInfo){
		if (!userInfo.isEmpty()){
			try {
				EOKUser EOKUserTemplate = new EOKUser(userInfo.get("userName"));
				EOKUser EOKUserRegister = (EOKUser)space.readIfExists(EOKUserTemplate,null,TWO_MINUTES);

				if (EOKUserRegister == null){
					try{
						EOKUser EOKUserReg = new EOKUser(userInfo.get("userName"),userInfo.get("password"));
						EOKUserReg.firstName = userInfo.get("firstName");
						EOKUserReg.secondName = userInfo.get("secondName");
						EOKUserReg.emailAddress = userInfo.get("email");
						EOKUserReg.loggedIn = false;
						EOKUserReg.bids = new ArrayList<Bid>();
						EOKUserReg.lots = new ArrayList<EO2Lot>();

						space.write(EOKUserReg,null, Lease.FOREVER);
						return true;
					}
					catch (Exception e){
						e.printStackTrace();
					}
				}else{
					errorMessage = "User is already exist";
				}
			} catch (Exception e){
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * LoginUser: Logs in user
	 * @param userName
	 * @param password
	 * @return
	 */
	public boolean loginUser( String userName, String password){

		try {
			if(!userName.isEmpty() && !password.isEmpty() ) {
				EOKUser EOKUserTemplate = new EOKUser(userName, password);
				EOKUser EOKUserLogin = (EOKUser) space.readIfExists(EOKUserTemplate, null, TWO_SECONDS);

				if (EOKUserLogin != null) {
					EOKUserLogin.loggedIn = true;
					sessionUser = EOKUserLogin;
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * addItem:
	 *
	 * @param lotInfo
	 * @return
	 */
	public boolean addItem(Map<String,String> lotInfo){

		// check data sent
		if(!isNumeric(lotInfo.get("lotBuyoutPrice")) || !isNumeric(lotInfo.get("lotStartPrice"))){
			errorMessage = "BuyOut price and  Starting Bid Price needs to be a number.";
			return false;
		}

		Double lotBuyOutPrice = Double.parseDouble(lotInfo.get("lotBuyoutPrice"));
		Double lotStartPrice = Double.parseDouble(lotInfo.get("lotStartPrice"));

		if(!amountChecker(lotBuyOutPrice) || !amountChecker(lotStartPrice)){
			errorMessage = "BuyOut price and  Starting Bid Price needs to higher than 0.";
			return false;
		}

		try{

			Transaction.Created trc = null;

			try {
				trc = TransactionFactory.create(mgr, THREE_SECONDS);
			} catch (Exception e){
				System.out.println("Could not create transaction " + e);
			}

			Transaction txn = trc.transaction;

			try{
				EOKUser template = new EOKUser(sessionUser.userId);
				EOKUser user = (EOKUser) space.take(template,txn,TWO_MINUTES);

								//add Item
				EO2Lot EO2LotItem = new EO2Lot(sessionUser, lotInfo.get("lotName"));
				EO2LotItem.lotDescription = lotInfo.get("lotDescription");
				EO2LotItem.lotBuyOutPrice = lotBuyOutPrice;
				EO2LotItem.lotStartPrice = lotStartPrice;
				EO2LotItem.sold = false;
				EO2LotItem.bids = new ArrayList<Bid>();

				if (user != null){
					user.lots.add(EO2LotItem);
				}
				//write user into space
				space.write(user,txn,Lease.FOREVER);
				//write the item to space
				space.write(EO2LotItem,txn, Lease.FOREVER);

			}catch (Exception e){
				System.out.println("Failed to read or write to space " + e);
				txn.abort();
				return false;
			}

			// ... and commit the transaction.
			txn.commit();
			return true;
		} catch (Exception e) {
			System.out.print("Transaction failed " + e);
		}

		return false;
	}

	/**
	 * setBid:
	 * @param bidPrice
	 * @param lotItem
	 * @param highestBid
	 * @return
	 */
	public boolean setBid(Double bidPrice, EO2Lot lotItem, Double highestBid){

		// get the lot item, and get the latest bid
		try{
			Transaction.Created setTrc = null;

			try {
				setTrc = TransactionFactory.create(mgr, Lease.FOREVER);
			} catch (Exception e){
				System.out.println("Could not create transaction " + e);
			}

			Transaction addBids = setTrc.transaction;

			try{
				EO2Lot template = new EO2Lot(lotItem.lotOwner,lotItem.lotName);
				EO2Lot lot = (EO2Lot) space.takeIfExists(template,addBids,TWO_MINUTES);

				if (lot != null){
					int returnValue = Double.compare(highestBid,bidPrice);
					if (returnValue < 0){
						Bid bid = new Bid(lot,bidPrice,sessionUser);
						lot.bids.add(bid);
						space.write(bid,addBids,Lease.FOREVER);
						space.write(lot,addBids,Lease.FOREVER);
					}else if (returnValue > 0){
						errorMessage = "Error!! Bid price is less than highest bid " + lot.bids.get(lot.bids.size() - 1).bidValue;
						throw new Exception();
					}else{
						errorMessage = "Error!! Bid price is equal to the highest bid " + lot.bids.get(lot.bids.size() - 1).bidValue;
						throw new Exception();
					}
				}
			}catch (Exception e){
				System.out.println("Failed to read or write to space " + e);
				addBids.abort();
				return false;
			}

			// ... and commit the transaction.
			addBids.commit();
			return true;
		} catch (Exception e) {
			System.out.print("Transaction failed " + e);
		}

		return false;
	}

	/**
	 * getHighestBid:
	 * @param lotItem
	 * @return
	 */
	public String getHighestBid(EO2Lot lotItem){
		if (lotItem.bids.size() > 0){
			return "£"+ lotItem.bids.get(lotItem.bids.size() - 1).bidValue;
		}
		return "£0.0";
	};

	/**
	 * getAllLots:
	 * @return lotsCollection
	 */
	public DefaultListModel<EO2Lot> getAllLots(){
		DefaultListModel<EO2Lot> lotsCollection = new DefaultListModel<EO2Lot>();

		Collection<EO2Lot> templates = new ArrayList<EO2Lot>();
		EO2Lot template = new EO2Lot();
		templates.add(template);
			try {

				Collection<EO2Lot> EO2Lots = space.take(templates, null, FIVE_SECONDS, NUMBER_OF_OBJECTS_TO_RETURN);

				for (Object result : EO2Lots) {
					EO2Lot eo = (EO2Lot) result;
					lotsCollection.addElement(eo);
					space.write(eo,null,Lease.FOREVER);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		return lotsCollection;
	}

	/**
	 * removeItem:
	 * @param lotItem
	 * @return
	 */
	public boolean removeItem(EO2Lot lotItem){
		try {
			EO2Lot lot = (EO2Lot) space.takeIfExists(lotItem,null,ONE_SECOND);
			if (lot != null){
				for (Object result : lot.bids) {
					Bid bid = (Bid) space.take((Entry) result,null,TWO_SECONDS);
				}
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * buyOutItem:
	 * @param lotItem
	 * @return
	 */
	public boolean buyOutItem(EO2Lot lotItem){
		try {
			EO2Lot lot = (EO2Lot) space.takeIfExists(lotItem,null,ONE_SECOND);
			if (lot != null){
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * isNumeric : Check if inputs are numbers
	 * @param str
	 * @return
	 */
	public boolean isNumeric(String str){
		String numericRegex = "[0-9]+(\\.[0-9][0-9]?)?";

		Pattern pat = Pattern.compile(numericRegex);
		if (str == null)
			return false;
		return pat.matcher(str).matches();
	}

	/**
	 *  isValid : Check if email address is a valid email address
	 *
	 * @param  email an email address
	 * @return Boolean
	 */
	public boolean isValid(String email)
	{
		String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
				"[a-zA-Z0-9_+&*-]+)*@" +
				"(?:[a-zA-Z0-9-]+\\.)+[a-z" +
				"A-Z]{2,7}$";

		Pattern pat = Pattern.compile(emailRegex);
		if (email == null)
			return false;
		return pat.matcher(email).matches();
	}

	public JavaSpace05 getSpace() {
		return space;
	}

	public boolean amountChecker(Double amount){
		return amount > 0;
	}

	public void preLoad(){
		Map<String,String> preInfo = new HashMap<String, String>();
		preInfo.put("firstName","admin");
		preInfo.put("secondName","admin");
		preInfo.put("userName","admin");
		preInfo.put("email","admin@admin.com");
		preInfo.put("password","root");

		registerUser(preInfo);
	}

	public DefaultListModel<EO2Lot> getActiveLots(EO2Lot eo2Lot){
		DefaultListModel<EO2Lot> temp = new DefaultListModel<EO2Lot>();

		return temp;
	}

	public DefaultListModel<Bid> getActiveBids(EO2Lot eo2Lot){
		DefaultListModel<Bid> temp = new DefaultListModel<Bid>();

		return temp;
	}

	/**
	 *	logOut
	 * @return Boolean
	 */
	public Boolean logOut(){
		EOKUser template = new EOKUser(sessionUser.userId);
		try {
			EOKUser user = (EOKUser) space.take(template,null,TWO_MINUTES);

			if (user != null){
				user.loggedIn = false;
				space.write(user,null,Lease.FOREVER);
				return true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}


//	public String hashingPassword(){
//		byte[] salt = new byte[16];
//		random.nextBytes(salt);
//		KeySpec spec = new PBEKeySpec()
//	}
}
