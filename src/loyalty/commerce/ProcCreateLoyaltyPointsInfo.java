package loyalty.commerce;

import atg.commerce.order.Order;
import atg.commerce.payment.PaymentManagerPipelineArgs;
import atg.nucleus.GenericService;
import atg.service.pipeline.PipelineProcessor;
import atg.service.pipeline.PipelineResult;

public class ProcCreateLoyaltyPointsInfo extends GenericService implements	PipelineProcessor {
	
	public static final int SUCCESS = 1;
	String mLoyaltyPointsInfoClass = "loyalty.commerce.LoyaltyPointsInfo";
		
	public String getLoyaltyPointsInfoClass() {
		return mLoyaltyPointsInfoClass;
	}
	
	public void setLoyaltyPointsInfoClass(String pLoyaltyPointsInfoClass) {
		this.mLoyaltyPointsInfoClass = pLoyaltyPointsInfoClass;
	}
	
	protected void addDataToLoyaltyPointsInfo(Order pOrder,
		       LoyaltyPointsPaymentGroup pPaymentGroup, double pAmount,
		       PaymentManagerPipelineArgs pParams, LoyaltyPointsInfo
		       pLoyaltyPointsInfo)
	{
		if (isLoggingDebug())
		      logDebug("Adding data to LoyaltyPointsInfo: UserId " + pPaymentGroup.getUserId() + ", Amount " + pAmount + ", OrderAmount " + pOrder.getPriceInfo().getTotal());
	
		pLoyaltyPointsInfo.setUserId(pPaymentGroup.getUserId());
		pLoyaltyPointsInfo.setAmount(pAmount);   
		pLoyaltyPointsInfo.setOrderAmount(pOrder.getPriceInfo().getTotal());		
	}
	
	protected LoyaltyPointsInfo getLoyaltyPointsInfo()throws Exception{
		LoyaltyPointsInfo lpi = (LoyaltyPointsInfo) Class.forName(getLoyaltyPointsInfoClass()).newInstance();
	    return lpi;
	}
	

	public int[] getRetCodes() {
		int retCodes[]={SUCCESS};
		return retCodes;
	}

	public int runProcess(Object pParam, PipelineResult pResult) throws Exception {		
		PaymentManagerPipelineArgs params = (PaymentManagerPipelineArgs)pParam;
		Order order = params.getOrder();
		LoyaltyPointsPaymentGroup loyaltyPointsPaymentGroup = (LoyaltyPointsPaymentGroup)params.getPaymentGroup();
		double amount = params.getAmount();		
		LoyaltyPointsInfo lpi = getLoyaltyPointsInfo();
		addDataToLoyaltyPointsInfo(order, loyaltyPointsPaymentGroup, amount, params, lpi);
		if (isLoggingDebug())
		      logDebug("Putting LoyaltyPointsInfo object into pipeline: " + lpi.toString());
		params.setPaymentInfo(lpi);
				
		return SUCCESS;
	}

}
