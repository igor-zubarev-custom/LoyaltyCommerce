package loyalty.commerce;

import java.util.Date;
import dynamusic.LoyaltyManager;
import atg.nucleus.GenericService;

public class LoyaltyPointsProcessorImpl extends GenericService implements LoyaltyPointsProcessor {
	private LoyaltyManager mLoyaltyManager = null;
	
	public LoyaltyManager getLoyaltyManager() {
		return mLoyaltyManager;
	}

	public void setLoyaltyManager(LoyaltyManager pLoyaltyManager) {
		this.mLoyaltyManager = pLoyaltyManager;
	}

	protected synchronized String getNextTransactionId() {
        return Long.toString(System.currentTimeMillis());
    }
	
	
	public LoyaltyPointsStatus authorize(LoyaltyPointsInfo pLoyaltyPointsInfo) {
		if (isLoggingDebug())
		      logDebug("Trying to authorize points from user accaunt for amount " + pLoyaltyPointsInfo.getAmount());
		boolean authorizeStatus = false;
		try {
			authorizeStatus = getLoyaltyManager().authorizeLoyaltyPoints(pLoyaltyPointsInfo.getUserId(), pLoyaltyPointsInfo.getAmount());
		} catch (Exception e) {
			if(isLoggingError())
	    		logError(e);
			String errorMsg = e.getMessage() != null ? e.getMessage() : e.toString();
			return new LoyaltyPointsStatus(this.getNextTransactionId(), pLoyaltyPointsInfo.getAmount(), false, errorMsg, new Date());
		}
		return new LoyaltyPointsStatus(this.getNextTransactionId(), pLoyaltyPointsInfo.getAmount(), authorizeStatus, "", new Date());
	}

	public LoyaltyPointsStatus debit(LoyaltyPointsInfo pLoyaltyPointsInfo, LoyaltyPointsStatus pStatus) {		
		if (isLoggingDebug())
		      logDebug("Trying to remove points from user accaunt for amount " + pLoyaltyPointsInfo.getAmount());
		
		try {
			getLoyaltyManager().debitLoyaltyPoints(pLoyaltyPointsInfo.getUserId(), pLoyaltyPointsInfo.getAmount());
		} catch (Exception e) {
			if(isLoggingError())
	    		logError(e);
			String errorMsg = e.getMessage() != null ? e.getMessage() : e.toString();
			return new LoyaltyPointsStatus(this.getNextTransactionId(), pLoyaltyPointsInfo.getAmount(), false, errorMsg, new Date());
		}
		return new LoyaltyPointsStatus(this.getNextTransactionId(), pLoyaltyPointsInfo.getAmount(), true, "", new Date());
	}

	public LoyaltyPointsStatus credit(LoyaltyPointsInfo pLoyaltyPointsInfo,	LoyaltyPointsStatus pStatus) {
		if (isLoggingDebug())
		      logDebug("Call unsupported method");
		
		String errorMsg = "No support method";
		return new LoyaltyPointsStatus(this.getNextTransactionId(), pLoyaltyPointsInfo.getAmount(), false, errorMsg, new Date());
	}

	public LoyaltyPointsStatus credit(LoyaltyPointsInfo pLoyaltyPointsInfo) {
		return this.credit(pLoyaltyPointsInfo, (LoyaltyPointsStatus)null);
	}
	

}
