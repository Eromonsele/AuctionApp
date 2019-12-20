
import net.jini.core.entry.UnusableEntryException;
import net.jini.core.event.RemoteEvent;
import net.jini.core.event.RemoteEventListener;
import net.jini.core.lease.Lease;
import net.jini.core.transaction.Transaction;
import net.jini.core.transaction.TransactionException;
import net.jini.core.transaction.TransactionFactory;
import net.jini.core.transaction.server.TransactionManager;
import net.jini.space.AvailabilityEvent;
import net.jini.space.JavaSpace05;

import javax.swing.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
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
	public EOKHUser sessionUser;


	public SessionManager(){
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

		//bidNotification();

	}

	/**
	 *  Registers a user based on the inputs from various fields and returns a true or false,
	 *  if registration is successful or not successful respectively
	 *
	 * @param userInfo an map collection of user information.
	 * @return true if registration is successful and has been written into the javaspace,false otherwise
	 */
	public boolean registerUser(Map<String,String> userInfo){
		if (!userInfo.isEmpty()){
			try {
				EOKHUser EOKHUserTemplate = new EOKHUser(userInfo.get("userName"));
				EOKHUser EOKHUserRegister = (EOKHUser)space.readIfExists(EOKHUserTemplate,null,TWO_MINUTES);

				if (EOKHUserRegister == null){
					try{
						EOKHUser EOKHUserReg = new EOKHUser(userInfo.get("userName"),userInfo.get("password"));
						EOKHUserReg.firstName = userInfo.get("firstName");
						EOKHUserReg.secondName = userInfo.get("secondName");
						EOKHUserReg.emailAddress = userInfo.get("email");
						EOKHUserReg.loggedIn = false;
						EOKHUserReg.EOKHBids = new ArrayList<EOKHBid>();
						EOKHUserReg.lots = new ArrayList<EOKHLot>();
						EOKHUserReg.messages = new ArrayList<Message>();

						space.write(EOKHUserReg,null, Lease.FOREVER);
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
	 * Creates a session for registered user and returns a true or false value,
	 * if login is successful or otherwise
	 *
	 * @param userName a string of the user's identification
	 * @param password a string if the user's password
	 * @return true if Login is successful and has been written into the javaspace, false otherwise
	 */
	public boolean loginUser( String userName, String password){

		try {
			if(!userName.isEmpty() && !password.isEmpty() ) {
				EOKHUser EOKHUserTemplate = new EOKHUser(userName, password);
				EOKHUser EOKHUserLogin = (EOKHUser) space.readIfExists(EOKHUserTemplate, null, TWO_SECONDS);

				if (EOKHUserLogin != null) {
					EOKHUserLogin.loggedIn = true;
					sessionUser = EOKHUserLogin;
					return true;
				}else{
					errorMessage = "User doesn't exist, Please register ";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Creates and inserts an item into the javaspace, this item will be owned by the session user.
	 *
	 * @param lotInfo a map collection of the lot info
	 * @return true if add item is successful and has been written into the javaspace, false otherwise
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
				EOKHUser template = new EOKHUser(sessionUser.userId);
				EOKHUser user = (EOKHUser) space.take(template,txn,TWO_MINUTES);

				//add Item
				EOKHLot EOKHLotItem = new EOKHLot(sessionUser, lotInfo.get("lotName"));
				EOKHLotItem.lotDescription = lotInfo.get("lotDescription");
				EOKHLotItem.lotBuyOutPrice = lotBuyOutPrice;
				EOKHLotItem.lotStartPrice = lotStartPrice;
				EOKHLotItem.sold = false;
				EOKHLotItem.lotId = generateRandomID();
				EOKHLotItem.EOKHBids = new ArrayList<EOKHBid>();

				if (user != null){
					user.lots.add(EOKHLotItem);
				}
				//write user into space
				space.write(user,txn,Lease.FOREVER);
				//write the item to space
				space.write(EOKHLotItem,txn, Lease.FOREVER);

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
	public boolean setBid(Double bidPrice, EOKHLot lotItem, Double highestBid){

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
				EOKHUser usertemplate = new EOKHUser(sessionUser.userId);
				EOKHUser user = (EOKHUser) space.take(usertemplate,addBids,TWO_MINUTES);

				EOKHLot template = new EOKHLot(lotItem.lotOwner,lotItem.lotName);
				EOKHLot lot = (EOKHLot) space.takeIfExists(template,addBids,TWO_MINUTES);

				if (lot != null && user != null){
					int returnValue = Double.compare(highestBid,bidPrice);
					if (returnValue < 0){
						EOKHBid EOKHBid = new EOKHBid(lot,bidPrice,sessionUser);
						lot.EOKHBids.add(EOKHBid);
						user.EOKHBids.add(EOKHBid);


						Message message = new Message();
						message.owner = user.userId;
						message.otherUser = user.userId;
						message.itemName =lot.toString();
						message.messageType = "bidByOther";

						user.messages.add(message);

						space.write(message,addBids,Lease.FOREVER);
						space.write(EOKHBid,addBids,Lease.FOREVER);
						space.write(lot,addBids,Lease.FOREVER);
						space.write(user,addBids,Lease.FOREVER);

					}else if (returnValue > 0){
						errorMessage = "Error!! Bid price is less than highest bid " + lot.EOKHBids.get(lot.EOKHBids.size() - 1).bidValue;
						throw new Exception("Error");
					}else{
						errorMessage = "Error!! Bid price is equal to the highest bid " + lot.EOKHBids.get(lot.EOKHBids.size() - 1).bidValue;
						throw new Exception("Error");
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
	public String getHighestBid(EOKHLot lotItem){
		if (lotItem.EOKHBids.size() > 0){
			return "£"+ lotItem.EOKHBids.get(lotItem.EOKHBids.size() - 1).bidValue;
		}
		return "£0.0";
	};

	/**
	 * removeItem:
	 * @param lotItem
	 * @return
	 */
	public boolean removeItem(EOKHLot lotItem){
		try {
			EOKHUser userTemplate = new EOKHUser(sessionUser.userId);
			EOKHUser user = (EOKHUser) space.take(userTemplate,null,TWO_MINUTES);

			EOKHLot lotTemplate = new EOKHLot(lotItem.lotOwner, lotItem.lotName);
			EOKHLot lot = (EOKHLot) space.take(lotTemplate,null, TWO_MINUTES);

			if (user != null && lot != null){
				for (int i = 0; i <  user.lots.size(); i++) {
					if (user.lots.get(i).equals(lot) ){
						user.lots.remove(i);
					}
				}

				for (int j = 0; j < lot.EOKHBids.size(); j++) {
					lot.EOKHBids.remove(j);
				}

				space.write(user,null,Lease.FOREVER);
			}
			return true;
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
	public boolean buyOutItem(EOKHLot lotItem){
		try {
			EOKHLot template = new EOKHLot(lotItem.lotOwner, lotItem.lotName);
			EOKHLot lot = (EOKHLot) space.takeIfExists(template,null,ONE_SECOND);
			if (lot != null){
				lot.sold = true;
				space.write(lot,null,Lease.FOREVER);
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * getNotifications:
	 * @return
	 */
	public ArrayList<Message> getNotifications(){
		ArrayList<Message> messages = new ArrayList<>();

		EOKHUser template = new EOKHUser(sessionUser.userId);
		try {
			EOKHUser user = (EOKHUser) space.read(template, null,TWO_MINUTES);

			if (user != null){
				if (user.lots.size() > 0){
					messages = user.messages;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return messages;
	};

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
	public boolean isValid(String email){
		String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
				"[a-zA-Z0-9_+&*-]+)*@" +
				"(?:[a-zA-Z0-9-]+\\.)+[a-z" +
				"A-Z]{2,7}$";

		Pattern pat = Pattern.compile(emailRegex);
		if (email == null)
			return false;
		return pat.matcher(email).matches();
	}

	/**
	 * amountChecker:
	 * @param amount
	 * @return
	 */
	public boolean amountChecker(Double amount){
		return amount > 0;
	}

	/**
	 * getActiveLots:
	 * @return
	 */
	public DefaultListModel<EOKHLot> getActiveLots(){
		DefaultListModel<EOKHLot> temp = new DefaultListModel<EOKHLot>();
		EOKHUser template = new EOKHUser(sessionUser.userId);
		try {
			EOKHUser user = (EOKHUser) space.take(template, null, TWO_MINUTES);

			if (user != null){
				for (int i = 0; i < user.lots.size(); i++) {
					temp.addElement(user.lots.get(i));
				}
				space.write(user,null,Lease.FOREVER);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return temp;
	}

	/**
	 * logOut:
	 * @return Boolean
	 */
	public boolean logOut(){
		EOKHUser template = new EOKHUser(sessionUser.userId);
		try {
			EOKHUser user = (EOKHUser) space.take(template,null,TWO_MINUTES);

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


	public void bidNotification(AuctionGUI auctionGUI){
		List<EOKHBid> templates = new ArrayList<>();

		EOKHBid template = new EOKHBid();
		templates.add(template);

		RemoteEventListener listener = new RemoteEventListener() {
			@Override
			public void notify(RemoteEvent remoteEvent) {
				System.out.println("Bid Notify Triggered");
				// Cast the RemoteEvent to an AvailabilityEvent, as this adds extra functionality
				AvailabilityEvent event = (AvailabilityEvent) remoteEvent;

				try {
					EOKHBid EOKHBid = (EOKHBid) event.getEntry();
					if (EOKHBid.bidder != null){
						EOKHUser userTemplate = new EOKHUser(EOKHBid.bidder.userId);
						try {
							EOKHUser user = (EOKHUser) space.take(userTemplate,null,TWO_MINUTES);
							if(user != null){
								System.out.println("Message working Triggered");
								Message message = new Message();
								message.owner = sessionUser.userId;
								message.otherUser = user.userId;
								message.itemName = EOKHBid.EOKHLotItem.toString();
								message.messageType = "bidByOther";
								user.messages.add(message);

								space.write(message,null,Lease.FOREVER);
								space.write(user,null,Lease.FOREVER);

							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
//					if (bid)
					System.out.println("kcrazy " + EOKHBid);
					auctionGUI.updateNotifications();
				} catch (UnusableEntryException e) {
					e.printStackTrace();
				}
			}
		};

		try {
			// export the listener object, so its "notify" method can be called remotely from the space
			UnicastRemoteObject.exportObject(listener, 0);

			// add the "registerForAvailabilityEvent, much like adding a "notify" to the space
			space.registerForAvailabilityEvent(templates, null, false, listener, Lease.FOREVER, null);

			EOKHBid EOKHBid = new EOKHBid();
			space.write(EOKHBid,null,FIVE_SECONDS);

		} catch (RemoteException | TransactionException e) {
			e.printStackTrace();
		}
	}

	/**
	 *
	 * @return
	 */
	public String generateRandomID(){
		String saltChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
		StringBuilder id = new StringBuilder();
		Random rnd = new Random();
		while (id.length() < 18) { // length of the random string.
			int index = (int) (rnd.nextFloat() * saltChars.length());
			id.append(saltChars.charAt(index));
		}
		String saltStr = id.toString();
		return saltStr;
	}

}
