package loyalty.commerce;

import dynamusic.LoyaltyManager;
import atg.commerce.order.PaymentGroup;
import atg.commerce.order.processor.ValidatePaymentGroupPipelineArgs;
import atg.nucleus.GenericService;
import atg.service.pipeline.PipelineProcessor;
import atg.service.pipeline.PipelineResult;

public class ProcValidateLoyaltyPoints extends GenericService implements PipelineProcessor {
	private static final int SUCCESS = 1;	
	private LoyaltyConfiguration loyaltyConfiguration;
	private LoyaltyManager loyaltyManager;
		
	public LoyaltyManager getLoyaltyManager() {
		return loyaltyManager;
	}
	public void setLoyaltyManager(LoyaltyManager loyaltyManager) {
		this.loyaltyManager = loyaltyManager;
	}
	public LoyaltyConfiguration getLoyaltyConfiguration() {
		return loyaltyConfiguration;
	}
	public void setLoyaltyConfiguration(LoyaltyConfiguration loyaltyConfiguration) {
		this.loyaltyConfiguration = loyaltyConfiguration;
	}
	public int[] getRetCodes() {		
		return new int[]{SUCCESS};
	}

	public int runProcess(Object pParam, PipelineResult pResult) throws Exception {		
		LoyaltyConfiguration config = getLoyaltyConfiguration();
		if (isLoggingDebug())
		      logDebug("Start validate vith configuration LoyaltyPoints PG: Enable = " + config.isEnable()
		    		  + "; CurrencyRate = " + config.getCurrencyRate()
		    		  + "; MaxRateOfPayment = " + config.getMaxRateOfPayment()
		    		  + "; PointsForPayment = " + config.getPointsForPayment());
		
		if (!config.isEnable()) {			
			pResult.addError("Validate error", "Loyalty points service is unable");
			return SUCCESS;
		}
		ValidatePaymentGroupPipelineArgs args;
		args = (ValidatePaymentGroupPipelineArgs) pParam;
		PaymentGroup pg = args.getPaymentGroup();		
		
		try {
			LoyaltyPointsPaymentGroup loyaltyPointsPaymentGroup = (LoyaltyPointsPaymentGroup)pg;
			double loyaltyAmount = loyaltyPointsPaymentGroup.getAmount();
			double totalAmount = args.getOrder().getPriceInfo().getTotal();
			double maxRateOfPayment = config.getMaxRateOfPayment();
			if (totalAmount * maxRateOfPayment < loyaltyAmount) {
				pResult.addError("Max rate error", "Only " + maxRateOfPayment * 100 +"% can be paid via Loyalty Points");
			}
			
			boolean athorizeStatus = getLoyaltyManager().authorizeLoyaltyPoints(args.getOrder().getProfileId(), loyaltyAmount);			
			if(!athorizeStatus){
				pResult.addError("Validate error", "Not enough points");			
			}
			
		} catch (Exception e) {
			if(isLoggingError())
				logError(e);
			pResult.addError("Validate error", "Expected LoyaltyPoints payment group, but got " + pg.getClass().getName());
		}	
		
		return SUCCESS;
	}

}
