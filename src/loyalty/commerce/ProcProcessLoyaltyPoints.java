package loyalty.commerce;

import atg.commerce.CommerceException;
import atg.commerce.order.PaymentGroup;
import atg.commerce.payment.Constants;
import atg.commerce.payment.PaymentException;
import atg.commerce.payment.PaymentManagerAction;
import atg.commerce.payment.PaymentManagerPipelineArgs;
import atg.commerce.payment.processor.ProcProcessPaymentGroup;
import atg.payment.PaymentStatus;
import atg.service.pipeline.PipelineResult;

public class ProcProcessLoyaltyPoints extends ProcProcessPaymentGroup {
	private static final int SUCCESS = 1;
	private static final int ERROR = 0;
	LoyaltyPointsProcessor loyaltyPointsProcessor;

	public LoyaltyPointsProcessor getLoyaltyPointsProcessor() {
		return loyaltyPointsProcessor;
	}

	public void setLoyaltyPointsProcessor(LoyaltyPointsProcessor pLoyaltyPointsProcessor) {
		this.loyaltyPointsProcessor = pLoyaltyPointsProcessor;
	}

	@Override
	public PaymentStatus authorizePaymentGroup(PaymentManagerPipelineArgs pParams) throws CommerceException {		
		LoyaltyPointsInfo lpi = (LoyaltyPointsInfo)pParams.getPaymentInfo();
		PaymentStatus paymentStatus = getLoyaltyPointsProcessor().authorize(lpi);
		pParams.getPaymentGroup().addAuthorizationStatus(paymentStatus);
		if (isLoggingDebug())
		      logDebug("Authorization for amount " + paymentStatus.getAmount());
		return paymentStatus;
	}
	
	@Override
	public PaymentStatus debitPaymentGroup(PaymentManagerPipelineArgs pParams) throws CommerceException {
		LoyaltyPointsInfo lpi = (LoyaltyPointsInfo)pParams.getPaymentInfo();
		PaymentGroup pg = pParams.getPaymentGroup();
		PaymentStatus authStatus = pParams.getPaymentManager().getLastAuthorizationStatus(pg);			
		PaymentStatus debitStatus;
		try {
			debitStatus = getLoyaltyPointsProcessor().debit(lpi, (LoyaltyPointsStatus)authStatus);
			pg.addDebitStatus(debitStatus);
			pg.setAmountDebited(debitStatus.getAmount());
			if (isLoggingDebug())
			      logDebug("Debit for amount " + debitStatus.getAmount());
            return debitStatus;
        } catch (ClassCastException e) {
        	if(isLoggingError())
				logError("Trying to cast " + authStatus.getClass().getName() + " to LoyaltyPointsStatus", e);
            throw new PaymentException(Constants.INVALID_AUTH_STATUS);
        }
	}

	@Override
	public PaymentStatus creditPaymentGroup(PaymentManagerPipelineArgs pParams)	throws CommerceException {
		if (isLoggingDebug())
		      logDebug("Call unsupported method");
		throw new PaymentException("Not support method");
        
	}
	
	@Override
	protected void invokeProcessorAction(PaymentManagerAction pProcessorAction,	PaymentManagerPipelineArgs pParams) throws CommerceException {
		 PaymentStatus status = null;  
		  
		 if (isLoggingDebug()) {  
			logDebug("Obtained processorAction with: " + pProcessorAction);  
		 }  
		 if (pProcessorAction == PaymentManagerAction.AUTHORIZE){  
			status = authorizePaymentGroup(pParams);
			pParams.setAction(PaymentManagerAction.DEBIT);
		 }
		 else if (pProcessorAction == PaymentManagerAction.DEBIT){  
			 status = debitPaymentGroup(pParams); 
		 }else {  
			 throw new CommerceException("Invalid processor action specified: " + pProcessorAction);  
		 }  
		 pParams.setPaymentStatus(status);  
	}
	
	@Override
	public int runProcess(Object pParam, PipelineResult pResult) throws Exception {
		PaymentManagerPipelineArgs params = (PaymentManagerPipelineArgs)pParam; 		
	    PaymentManagerAction action = params.getAction();  
	    try {  
	      invokeProcessorAction(action, params);  	
	      if (!params.getPaymentStatus().getTransactionSuccess()) {
				return ERROR;
			}
	    }  
	    catch (CommerceException e) {  
	    	if(isLoggingError())
	    		logError(e);  
	    	pResult.addError("ProcProcessLoyaltyPointsFailed", e);  
	    	return ERROR;  
	    }  
	  
	    return SUCCESS;  
	}

	

}
