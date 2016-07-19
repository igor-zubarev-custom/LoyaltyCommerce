package loyalty.commerce;

import java.util.Date;

import atg.payment.PaymentStatusImpl;

public class LoyaltyPointsStatus extends PaymentStatusImpl {
	public LoyaltyPointsStatus(){		
	}
	
	public LoyaltyPointsStatus(String pTransactionId, double pAmount, boolean pTransactionSuccess, String pErrorMessage, Date pTransactionTimestamp){
		super(pTransactionId, pAmount, pTransactionSuccess, pErrorMessage, pTransactionTimestamp);
	}

}
