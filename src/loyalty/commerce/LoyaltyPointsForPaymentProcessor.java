package loyalty.commerce;

import dynamusic.LoyaltyManager;
import atg.commerce.payment.PaymentManagerPipelineArgs;
import atg.nucleus.GenericService;
import atg.payment.creditcard.CreditCardInfo;
import atg.service.pipeline.PipelineProcessor;
import atg.service.pipeline.PipelineResult;

public class LoyaltyPointsForPaymentProcessor extends GenericService implements	PipelineProcessor {
	private static final int SUCCESS = 1;
	private static final int ERROR = 0;
	private LoyaltyConfiguration loyaltyConfiguration;
	private LoyaltyManager loyaltyManager;
	
	public LoyaltyConfiguration getLoyaltyConfiguration() {
		return loyaltyConfiguration;
	}

	public void setLoyaltyConfiguration(LoyaltyConfiguration loyaltyConfiguration) {
		this.loyaltyConfiguration = loyaltyConfiguration;
	}

	public LoyaltyManager getLoyaltyManager() {
		return loyaltyManager;
	}

	public void setLoyaltyManager(LoyaltyManager loyaltyManager) {
		this.loyaltyManager = loyaltyManager;
	}

	public int[] getRetCodes() {		
		return new int[]{SUCCESS};
	}

	public int runProcess(Object pParam, PipelineResult pResult) throws Exception {
		if (!getLoyaltyConfiguration().isEnable()) {
			if (isLoggingDebug())
			      logDebug("Trying to add points with disable LoyaltyPoints system");
			return ERROR;
		}
		try{
			PaymentManagerPipelineArgs params = (PaymentManagerPipelineArgs)pParam;
			CreditCardInfo cardInfo = (CreditCardInfo)params.getPaymentInfo();
			String UserId = cardInfo.getOrder().getProfileId();
			double amount = cardInfo.getAmount();
			if (isLoggingDebug())
			      logDebug("Trying to add points for amount " + amount);
			getLoyaltyManager().creditLoyaltyPoints(UserId, amount);
		}catch (Exception e) {
			if(isLoggingError())
				logError(e);  
			pResult.addError("LoyaltyPointsForPaymentProcessor", e);  
			return ERROR; 
		}
		return SUCCESS;
	}

}
