package loyalty.commerce;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.transaction.TransactionManager;

import atg.commerce.CommerceException;
import atg.commerce.order.CreditCard;
import atg.commerce.order.PaymentGroupManager;
import atg.commerce.order.purchase.PurchaseProcessFormHandler;
import atg.service.pipeline.RunProcessException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

public class LoyaltyPointsGroupFormHandler extends PurchaseProcessFormHandler {
	private double pointsAmount;
	private String applyLoyaltyPointsSuccessURL;
	
	public String getApplyLoyaltyPointsSuccessURL() {
		return applyLoyaltyPointsSuccessURL;
	}
	public void setApplyLoyaltyPointsSuccessURL(String applyLoyaltyPointsSuccessURL) {
		this.applyLoyaltyPointsSuccessURL = applyLoyaltyPointsSuccessURL;
	}
	public double getPointsAmount() {
		return pointsAmount;
	}
	public void setPointsAmount(double pPointsAmount) {
		this.pointsAmount = pPointsAmount;
	}

	public boolean handleApplyLoyaltyPoints(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException,
			IOException, RunProcessException {
		try {		
			
			PaymentGroupManager pm = getPaymentGroupManager();
			pm.removeAllPaymentGroupsFromOrder(getOrder());				
						
			LoyaltyPointsPaymentGroup loyaltyPointsPG = (LoyaltyPointsPaymentGroup)pm.createPaymentGroup("loyaltyPoints");
			loyaltyPointsPG.setUserId(getOrder().getProfileId());							
			pm.addPaymentGroupToOrder(getOrder(), loyaltyPointsPG);
			getOrderManager().addOrderAmountToPaymentGroup(getOrder(), loyaltyPointsPG.getId(), getPointsAmount());			
			
			CreditCard creditCardPG = (CreditCard) pm.createPaymentGroup("creditCard");
			pm.addPaymentGroupToOrder(getOrder(), creditCardPG, 0);
			getOrderManager().addRemainingOrderAmountToPaymentGroup(getOrder(), creditCardPG.getId());
			
			runProcessRepriceOrder(getOrder(), getUserPricingModels(), getUserLocale(), getProfile(), null);			
			getOrderManager().getPaymentGroupManager().recalculatePaymentGroupAmounts(getOrder());			
			getOrderManager().updateOrder(getOrder());
			
			pResponse.sendLocalRedirect(getApplyLoyaltyPointsSuccessURL(), pRequest);			
			
			return false;
		} catch (CommerceException e) {
			if(isLoggingError())
				logError(e);			
		}
		return false;
	}
	
}
