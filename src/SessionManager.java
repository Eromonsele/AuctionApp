import net.jini.core.lease.Lease;
import net.jini.core.transaction.server.TransactionManager;
import net.jini.space.JavaSpace;
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
	public EOUser sessionUser;

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
				EOUser EOUserTemplate = new EOUser(userInfo.get("userName"));
				EOUser EOUserRegister = (EOUser)space.readIfExists(EOUserTemplate,null,TWO_MINUTES);

				if (EOUserRegister == null){
					try{
						EOUser EOUserReg = new EOUser(userInfo.get("userName"),userInfo.get("password"));
						EOUserReg.firstName = userInfo.get("firstName");
						EOUserReg.secondName = userInfo.get("secondName");
						EOUserReg.emailAddress = userInfo.get("email");
						space.write(EOUserReg,null, Lease.FOREVER);
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
				EOUser EOUserTemplate = new EOUser(userName, password);
				EOUser EOUserLogin = (EOUser) space.readIfExists(EOUserTemplate, null, TWO_SECONDS);

				if (EOUserLogin != null) {
//                        EOKUserLogin.loggedIn = true;
					sessionUser = EOUserLogin;
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

	public void setBid(EOUser bidder, Float bidPrice, EOLot EOLotItem){
		Bid template = new Bid(bidder,bidPrice);
		template.EOLotItem = EOLotItem;
		try {
			space.write(template,null, TWO_SECONDS);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public DefaultListModel<String> getAllLots(){
		DefaultListModel<String> lotsCollection = new DefaultListModel<String>();

		Collection<EOLot> templates = new ArrayList<EOLot>();
		EOLot template = new EOLot();
		templates.add(template);
			try {

				Collection<EOLot> lots = space.take(templates, null, FIVE_SECONDS, NUMBER_OF_OBJECTS_TO_RETURN);

				for (Object result : lots) {
					EOLot eo = (EOLot) result;
					lotsCollection.addElement(eo.lotName);
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
