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
	public EOKUser sessionUser;

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
				EOKUser EOKUserTemplate = new EOKUser(userInfo.get("userName"));
				EOKUser EOKUserRegister = (EOKUser)space.readIfExists(EOKUserTemplate,null,TWO_MINUTES);

				if (EOKUserRegister == null){
					try{
						EOKUser EOKUserReg = new EOKUser(userInfo.get("userName"),userInfo.get("password"));
						EOKUserReg.firstName = userInfo.get("firstName");
						EOKUserReg.secondName = userInfo.get("secondName");
						EOKUserReg.emailAddress = userInfo.get("email");
//						EOKUserReg.bids = new ArrayList<Bid>();
//						EOKUserReg.lots = new ArrayList<EOLot>();
//						EOKUserReg.loggedIn = false;
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
//                     EOKUserLogin.loggedIn = true;
					sessionUser = EOKUserLogin;
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
//				EOKUser template = new EOKUser(sessionUser.userId);
//				EOKUser EOKUser = (EOKUser) space.takeIfExists(template, txn, THREE_SECONDS);

				// get Item Count in the space

				//add Item
				Lot LotItem = new Lot(sessionUser, lotInfo.get("lotName"));
				LotItem.lotDescription = lotInfo.get("lotDescription");
				LotItem.lotBuyOutPrice = lotBuyOutPrice;
				LotItem.lotStartPrice = lotStartPrice;
				LotItem.sold = false;
//				EOLotItem.bids = new ArrayList<Bid>();

				// if user exist
//				if (EOKUser != null){
////					EOKUser.lots.add(EOLotItem);
//					//write the count to the space
//
//
//					//write user back into the space
//					space.write(EOKUser, txn, Lease.FOREVER);
//
//				}

				//write the item to space
				space.write(LotItem,txn, Lease.FOREVER);




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

				Lot template = new Lot(sessionUser, itemName);
				try {
					Lot lot = (Lot) space.takeIfExists(template,removeLots,FIVE_SECONDS);

					if (lot != null){
						EOKUser userTemplate = new EOKUser(sessionUser.userId);
						EOKUser user = (EOKUser) space.takeIfExists(userTemplate, removeLots, FIVE_SECONDS);

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

	public boolean setBid(Float bidPrice, Lot LotItem){
		Bid template = new Bid(LotItem,bidPrice);
		try {
			space.write(template,null, TWO_SECONDS);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public String getLatestBid(Lot lotItem){
//		Bid template = new Bid(lotItem);
		return "";
	}
	public DefaultListModel<Lot> getAllLots(){
		DefaultListModel<Lot> lotsCollection = new DefaultListModel<Lot>();

		Collection<Lot> templates = new ArrayList<Lot>();
		Lot template = new Lot();
		templates.add(template);
			try {

				Collection<Lot> lots = space.take(templates, null, FIVE_SECONDS, NUMBER_OF_OBJECTS_TO_RETURN);

				for (Object result : lots) {
					Lot eo = (Lot) result;
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
