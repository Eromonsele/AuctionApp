import net.jini.core.lease.Lease;
import net.jini.core.transaction.Transaction;
import net.jini.core.transaction.TransactionFactory;
import net.jini.core.transaction.server.TransactionManager;
import net.jini.space.JavaSpace05;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class SessionManager {
	private static final long TWO_SECONDS = 2 * 1000;  // two thousand milliseconds
	private static final long TWO_MINUTES = 2 * 1000 * 60;
	private static final long ONE_SECOND = 1000;  // one thousand milliseconds
	private static int THREE_SECONDS = 3000;  // 3000 milliseconds
	private final static int FIVE_SECONDS = 1000 * 5; // that's 5000 Milliseconds
	private final static int NUMBER_OF_OBJECTS_TO_RETURN = 100;

	private JavaSpace05 space;
	private TransactionManager mgr;

	public String errorMessage;
	public EOK3User sessionUser;

	/**
	 *
	 */
	public SessionManager() {
		// set up the security manager
		if (System.getSecurityManager() == null)
			System.setSecurityManager(new SecurityManager());

		// Find the transaction manager on the network

		mgr = SpaceUtils.getManager();
		if (mgr == null) {
			System.err.println("Failed to find the transaction manager");
			System.exit(1);
		}

		space = (JavaSpace05)SpaceUtils.getSpace();
		if (space == null){
			System.err.println("Failed to find the javaspace");
			System.exit(1);
		}

	}

	/**
	 *
	 * @param userInfo
	 * @return
	 */
	public boolean registerUser(Map<String,String> userInfo){
		if (!userInfo.isEmpty()){
			try {
				EOK3User EOK3UserTemplate = new EOK3User(userInfo.get("userName"));
				EOK3User EOK3UserRegister = (EOK3User)space.readIfExists(EOK3UserTemplate,null,TWO_MINUTES);

				if (EOK3UserRegister == null){
					try{
						EOK3User EOK3UserReg = new EOK3User(userInfo.get("userName"),userInfo.get("password"));
						EOK3UserReg.firstName = userInfo.get("firstName");
						EOK3UserReg.secondName = userInfo.get("secondName");
						EOK3UserReg.emailAddress = userInfo.get("email");
						EOK3UserReg.bids = new ArrayList<Bid>();
						EOK3UserReg.EO2Lots = new ArrayList<EO2Lot>();
						EOK3UserReg.loggedIn = false;
						space.write(EOK3UserReg,null, Lease.FOREVER);
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
				EOK3User EOK3UserTemplate = new EOK3User(userName, password);
				EOK3User EOK3UserLogin = (EOK3User) space.readIfExists(EOK3UserTemplate, null, TWO_SECONDS);

				if (EOK3UserLogin != null) {
					EOK3UserLogin.loggedIn = true;
					sessionUser = EOK3UserLogin;
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	public boolean addItem(Map<String,String> lotInfo){

		// check data sent
		if(!isNumeric(lotInfo.get("lotBuyoutPrice")) || !isNumeric(lotInfo.get("lotStartPrice"))){
			errorMessage = "BuyOut price and  Starting Bid Price needs to be a number.";
			return false;
		}

		Float lotBuyOutPrice = Float.parseFloat(lotInfo.get("lotBuyoutPrice"));
		Float lotStartPrice = Float.parseFloat(lotInfo.get("lotStartPrice"));

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
				// take user out
				EOK3User template = new EOK3User(sessionUser.userId);
				EOK3User EOK3User = (EOK3User) space.takeIfExists(template, txn, THREE_SECONDS);

				// get Item Count in the space

				//add Item
				EO2Lot EO2LotItem = new EO2Lot(sessionUser, lotInfo.get("lotName"));
				EO2LotItem.lotDescription = lotInfo.get("lotDescription");
				EO2LotItem.lotBuyOutPrice = lotBuyOutPrice;
				EO2LotItem.lotStartPrice = lotStartPrice;
				EO2LotItem.sold = false;
				EO2LotItem.bids = new ArrayList<Bid>();

//				 if user exist
				if (EOK3User != null){
					EOK3User.EO2Lots.add(EO2LotItem);
					//write the count to the space

					//write user back into the space
					space.write(EOK3User, txn, Lease.FOREVER);

					//write the item to space
					space.write(EO2LotItem,txn, Lease.FOREVER);

				}

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
	 *
	 * @param itemName
	 * @return
	 */
	public boolean removeLots(String itemName){
		try{

			Transaction.Created trc = null;

			try {
				trc = TransactionFactory.create(mgr, THREE_SECONDS);
			} catch (Exception e){
				System.out.println("Could not create transaction " + e);
			}

			Transaction removeLots = trc.transaction;

			try{

				EO2Lot template = new EO2Lot(sessionUser, itemName);
				try {
					EO2Lot EO2Lot = (EO2Lot) space.takeIfExists(template,removeLots,FIVE_SECONDS);

					if (EO2Lot != null){
						EOK3User userTemplate = new EOK3User(sessionUser.userId);
						EOK3User user = (EOK3User) space.takeIfExists(userTemplate, removeLots, FIVE_SECONDS);

						if (user != null){
//							for (int i = 0; i < user.lots.size(); i++) {
//								if(user.lots.get(i).lotName == lot.lotName && user.lots.get(i).lotOwner == lot.lotOwner){
//									user.lots.remove(i);
//								}
//							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}catch (Exception e){
				System.out.println("Failed to read or write to space " + e);
				removeLots.abort();
				return false;
			}

			// ... and commit the transaction.
			removeLots.commit();
			return true;
		} catch (Exception e) {
			System.out.print("Transaction failed " + e);
		}
		return false;
	}

	public void getLotsByUser(){

	}

	public boolean setBid(Float bidPrice, EO2Lot EO2LotItem){

		if (bidPrice <= EO2LotItem.lotStartPrice){
			return false;
		}

		try{

			Transaction.Created trc = null;

			try {
				trc = TransactionFactory.create(mgr, THREE_SECONDS);
			} catch (Exception e){
				System.out.println("Could not create transaction " + e);
			}

			Transaction addBids = trc.transaction;

			try{
				// take user out
				EOK3User template = new EOK3User(sessionUser.userId);
				EOK3User EOK3User = (EOK3User) space.takeIfExists(template, addBids, THREE_SECONDS);

				EO2Lot EO2LotTemplate = new EO2Lot(sessionUser, EO2LotItem.lotName);
				EO2Lot eO2Lot = (EO2Lot) space.takeIfExists(EO2LotTemplate, addBids, THREE_SECONDS);

				if (eO2Lot != null && EOK3User != null){
					Bid bidTemplate = new Bid(EO2LotItem,bidPrice);
					bidTemplate.bidder = sessionUser;

					try {
						EOK3User.bids.add(bidTemplate);
						eO2Lot.bids.add(bidTemplate);

						space.write(bidTemplate,addBids, TWO_SECONDS);
						space.write(EOK3User,addBids,TWO_SECONDS);
						space.write(eO2Lot,addBids,TWO_SECONDS);
						return true;
					} catch (Exception e) {
						e.printStackTrace();
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

	public String getLatestBid(EO2Lot EO2LotItem){
//		Bid template = new Bid(lotItem);
		return EO2LotItem.bids.get(EO2LotItem.bids.size() - 1).toString();
	}

	public DefaultListModel<EO2Lot> getAllLots(){
		DefaultListModel<EO2Lot> lotsCollection = new DefaultListModel<EO2Lot>();

		Collection<EO2Lot> templates = new ArrayList<EO2Lot>();
		EO2Lot template = new EO2Lot();
		templates.add(template);
			try {

				Collection<EO2Lot> EO2Lots = space.take(templates, null, FIVE_SECONDS, NUMBER_OF_OBJECTS_TO_RETURN);

				for (Object result : EO2Lots) {
					EO2Lot eo = (EO2Lot) result;
					System.out.println(eo.lotName);
					lotsCollection.addElement(eo);
					space.write(eo,null,Lease.FOREVER);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		return lotsCollection;
	}

	/**
	 *
	 * @param lotItem
	 * @return
	 */
	public boolean buyOutItem(EO2Lot lotItem){
		if (lotItem.lotOwner.userId == sessionUser.userId){
			return false;
		}

		// remove all bids
		return false;
	}

	/**
	 * isNumeric : Check if inputs are numbers
	 * @param str
	 * @return
	 */
	public boolean isNumeric(String str){
		String emailRegex = "^[0-9]*$";

		Pattern pat = Pattern.compile(emailRegex);
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

	public boolean amountChecker(Float amount){
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
}
