import net.jini.core.lease.Lease;
import net.jini.core.transaction.Transaction;
import net.jini.core.transaction.TransactionFactory;
import net.jini.core.transaction.server.TransactionManager;
import net.jini.space.JavaSpace;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class SessionManager {
	private static final long TWO_SECONDS = 2 * 1000;  // two thousand milliseconds
	private static final long TWO_MINUTES = 2 * 1000 * 60;
	private static final long ONE_SECOND = 1000;  // one thousand milliseconds
	private static int THREE_SECONDS = 3000;  // 3000 milliseconds

	private JavaSpace space;
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

		space = SpaceUtils.getSpace();
		if (space == null){
			System.err.println("Failed to find the javaspace");
			System.exit(1);
		}

//		new PopulateTableNotify(new SessionManager());


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
//                        EOKUserLogin.loggedIn = true;
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
		try {
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

			EOLot EOLotItem = new EOLot(sessionUser, lotInfo.get("lotName"));
			EOLotItem.lotDescription = lotInfo.get("lotDescription");
			EOLotItem.lotBuyOutPrice = lotBuyOutPrice;
			EOLotItem.lotStartPrice = lotStartPrice;
			EOLotItem.sold = false;

			space.write(EOLotItem,null, Lease.FOREVER);
			return true;
		} catch (Exception e){
			e.printStackTrace();
		}
		return false;
	}

	public void removeLots(){

	}



	public void getLotsByUser(){

	}

	public void setBid(EOKUser bidder, Float bidPrice, EOLot EOLotItem){
		Bid template = new Bid(bidder,bidPrice);
		template.EOLotItem = EOLotItem;
		try {
			space.write(template,null, TWO_SECONDS);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param EOLotItem
	 * @return
	 */
	public DefaultListModel<Bid> getBids(EOLot EOLotItem){
		DefaultListModel<Bid> bidsCollection = new DefaultListModel<Bid>();

		try {
			Transaction.Created getBidsTrc = null;

			try {
				getBidsTrc = TransactionFactory.create(mgr, THREE_SECONDS);
			} catch (Exception e) {
				System.out.println("Could not create transaction " + e);
			}

			Transaction txn = getBidsTrc.transaction;
			int counter = 0;
			while(true) {
				try {
					Bid template = new Bid(EOLotItem);
					Bid bids = (Bid) space.takeIfExists(template, txn, ONE_SECOND);
					if (bids == null) {
						txn.abort();
						break;
					} else {
						bidsCollection.addElement(bids);
					}
//					space.write(lots, txn, TWO_SECONDS);
				} catch (Exception e) {
					e.printStackTrace();
					txn.abort();
					break;
				}
			}
			// ... and commit the transaction.
//				txn.commit();

		} catch (Exception e) {
			e.printStackTrace();
		}
//		}
		return bidsCollection;

	}

	public DefaultListModel<String> getAllBids(){
		DefaultListModel<String> lotsCollection = new DefaultListModel<String>();

		try {
			Transaction.Created trc = null;

			try {
				trc = TransactionFactory.create(mgr, THREE_SECONDS);
			} catch (Exception e) {
				System.out.println("Could not create transaction " + e);
			}

			Transaction txn = trc.transaction;
			int counter = 0;
			while(true) {
				try {
					EOLot EOLotItem = new EOLot();
					EOLot lots = (EOLot) space.takeIfExists(EOLotItem, txn, ONE_SECOND);
					if (lots == null) {
						break;
					} else {
						System.out.println(lots.lotName);
						lotsCollection.addElement(lots.lotName);
					}
//					space.write(lots, txn, TWO_SECONDS);
				} catch (Exception e) {
					e.printStackTrace();
					txn.abort();
					break;
				}
			}
			// ... and commit the transaction.
				txn.commit();

		} catch (Exception e) {
			e.printStackTrace();
		}
//		}
		return lotsCollection;

	}

	public DefaultListModel<String> getHighestBidForItem(){
		DefaultListModel<String> lotsCollection = new DefaultListModel<String>();

		try {
			Transaction.Created trc = null;

			try {
				trc = TransactionFactory.create(mgr, THREE_SECONDS);
			} catch (Exception e) {
				System.out.println("Could not create transaction " + e);
			}

			Transaction txn = trc.transaction;
			int counter = 0;
			while(true) {
				try {
					EOLot EOLotItem = new EOLot();
					EOLot lots = (EOLot) space.takeIfExists(EOLotItem, txn, ONE_SECOND);
					if (lots == null) {
						txn.abort();
						break;
					} else {
						System.out.println(lots.lotName);
						lotsCollection.addElement(lots.lotName);
					}
//					space.write(lots, txn, TWO_SECONDS);
				} catch (Exception e) {
					e.printStackTrace();
					txn.abort();
					break;
				}
			}
			// ... and commit the transaction.
//				txn.commit();

		} catch (Exception e) {
			e.printStackTrace();
		}
//		}
		return lotsCollection;

	}

	public DefaultListModel<String>  getBidByUser(){
		DefaultListModel<String> lotsCollection = new DefaultListModel<String>();

		try {
			Transaction.Created trc = null;

			try {
				trc = TransactionFactory.create(mgr, THREE_SECONDS);
			} catch (Exception e) {
				System.out.println("Could not create transaction " + e);
			}

			Transaction txn = trc.transaction;
			int counter = 0;
			while(true) {
				try {
					EOLot EOLotItem = new EOLot();
					EOLot lots = (EOLot) space.takeIfExists(EOLotItem, txn, ONE_SECOND);
					if (lots == null) {
						txn.abort();
						break;
					} else {
						System.out.println(lots.lotName);
						lotsCollection.addElement(lots.lotName);
					}
//					space.write(lots, txn, TWO_SECONDS);
				} catch (Exception e) {
					e.printStackTrace();
					txn.abort();
					break;
				}
			}
			// ... and commit the transaction.
//				txn.commit();

		} catch (Exception e) {
			e.printStackTrace();
		}
//		}
		return lotsCollection;

	}

	/**
	 * isNumeric : Check if inputs are numbers
	 * @param str
	 * @return
	 */
	private boolean isNumeric(String str){
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

	public JavaSpace getSpace() {
		return space;
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
