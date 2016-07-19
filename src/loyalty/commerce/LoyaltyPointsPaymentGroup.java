package loyalty.commerce;

import atg.commerce.order.PaymentGroupImpl;

public class LoyaltyPointsPaymentGroup extends PaymentGroupImpl {
	public String getUserId() {
	    return (String) getPropertyValue("userId");
	  }

	public void setUserId(String pUserId) {
	    setPropertyValue("userId", pUserId);
	}	
}
